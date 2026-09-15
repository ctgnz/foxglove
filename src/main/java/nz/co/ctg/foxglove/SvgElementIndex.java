package nz.co.ctg.foxglove;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import nz.co.ctg.foxglove.style.CssStylesheet;

/**
 * An index of the elements in a parsed document, keyed on their {@code id}, supporting the same document references used throughout SVG: {@code url(#id)} in presentation
 * attributes such as {@code fill} and {@code clip-path}, and {@code xlink:href="#id"} on elements such as {@code <use>} and {@code <textPath>}.
 * <p>
 * The index is a snapshot taken when it is built, so an index taken over a document that is subsequently modified is stale - see {@link SvgGraphic#rebuildElementIndex()}. Forward
 * references resolve, as the whole document is walked before any lookup is served.
 * <p>
 * Where a document declares the same {@code id} more than once it is in error, and the SVG specification leaves the outcome undefined. This index keeps the first element
 * encountered in document order and reports the offending id from {@link #getDuplicateIds()}.
 * <p>
 * A reference naming another document, such as {@code xlink:href="other.svg#id"}, is also resolved (#175) - relative to this index's own document's base URI, never an absolute
 * location given directly in the document (the same trust boundary {@link nz.co.ctg.foxglove.element.SvgImage} already applies to its own relative references) - by loading and
 * indexing that document in turn. This only applies to a bare {@code xlink:href}-shaped reference; a {@code url(#id)} funciri (as used by {@code fill}, {@code clip-path}, and
 * every other presentation attribute this class resolves) stays same-document-only, per the specification's own restriction on those properties.
 */
public final class SvgElementIndex {

    /**
     * The element a reference resolves to, together with the {@link SvgElementIndex} that actually owns it - {@code this} for an ordinary same-document reference, or the loaded
     * external document's own index for one naming another document. Only {@link nz.co.ctg.foxglove.element.SvgUse} needs the owner: it is the one caller that recurses into
     * rendering the resolved target's own content, which must resolve any further reference inside that content against the right document.
     */
    public record ResolvedElement(SvgElementIndex index, ISvgElement element) {
    }

    private static final String URL_PREFIX = "url(";
    private static final Map<Class<?>, List<Field>> CONTENT_FIELDS = Maps.newConcurrentMap();

    /**
     * Builds an index over the given document. The whole tree is walked, including nested {@code <svg>} elements and the contents of {@code <defs>}.
     */
    public static SvgElementIndex of(SvgGraphic root) {
        SvgElementIndex index = new SvgElementIndex();
        if (root != null) {
            index.add(root, null, Sets.newIdentityHashSet());
            index.baseUri = root.getBaseUri();
        }
        return index;
    }

    /**
     * Extracts the target id from a same document reference, accepting either a bare {@code #id} fragment or a {@code url(#id)} wrapper, with or without quotes, and ignoring
     * anything after the closing bracket so that a paint fallback such as {@code url(#grad) red} parses.
     *
     * @return the id, or empty if the reference is blank, malformed, or names another document
     */
    public static Optional<String> parseReference(String reference) {
        String iri = StringUtils.trimToEmpty(reference);
        if (StringUtils.startsWithIgnoreCase(iri, URL_PREFIX)) {
            int close = iri.indexOf(')', URL_PREFIX.length());
            if (close < 0) {
                return Optional.empty();
            }
            iri = StringUtils.strip(iri.substring(URL_PREFIX.length(), close)
                .trim(), "\"'");
        }
        // Anything before the '#' names another document, which is not resolvable against this index
        if (!StringUtils.startsWith(iri, "#")) {
            return Optional.empty();
        }
        return Optional.of(iri.substring(1)
            .trim())
            .filter(StringUtils::isNotEmpty);
    }

    private final Map<String, ISvgElement> elementsById = new LinkedHashMap<>();
    private final Map<ISvgElement, ISvgElement> parents = new IdentityHashMap<>();
    private final Set<String> duplicateIds = new LinkedHashSet<>();
    private final List<SvgStyle> styleElements = new ArrayList<>();
    private final List<ISvgElement> allElements = new ArrayList<>();
    private CssStylesheet stylesheet;
    private URI baseUri;

    private SvgElementIndex() {
    }

    /**
     * The parsed content of every {@code <style>} element in the document, built on first use and cached thereafter - the index is already a one-time snapshot, so the stylesheet
     * built from it can be too.
     */
    public CssStylesheet getStylesheet() {
        if (stylesheet == null) {
            stylesheet = CssStylesheet.of(styleElements);
        }
        return stylesheet;
    }

    /**
     * Resolves a reference - same document or, per this class's own javadoc, another document - to the element it names.
     */
    public Optional<ISvgElement> resolve(String reference) {
        return resolveWithOwner(reference).map(ResolvedElement::element);
    }

    /**
     * Resolves a reference the same way {@link #resolve(String)} does, but also reports which {@link SvgElementIndex} owns the result - see {@link ResolvedElement}.
     */
    public Optional<ResolvedElement> resolveWithOwner(String reference) {
        Optional<String> sameDocument = parseReference(reference);
        if (sameDocument.isPresent()) {
            return sameDocument.map(elementsById::get)
                .map(element -> new ResolvedElement(this, element));
        }
        return resolveExternal(reference);
    }

    /**
     * Resolves an {@code xlink:href}-shaped reference naming another document, such as {@code other.svg#id} or {@code ../images/svgRef4.svg#alpha} - {@link #parseReference}
     * already rejected it as a same-document reference by the time this runs, since anything before a leading {@code #} (or absent entirely) means it names something other than a
     * bare fragment. Absent a base URI, a blank document part, a blank fragment (nothing to look up - this is the {@code <image xlink:href="other.svg">} whole-file-as-image-source
     * case, out of scope here, see #178), a malformed URI, or the document part being absolute (a network or {@code file:} location given directly in the document, rather than
     * reached by resolving relative to the already-trusted base URI - the same restriction {@link nz.co.ctg.foxglove.element.SvgImage#resolveImage} already applies) - every one of
     * these degrades to an empty result rather than throwing.
     */
    private Optional<ResolvedElement> resolveExternal(String reference) {
        String iri = StringUtils.trimToEmpty(reference);
        if (baseUri == null || iri.isEmpty()) {
            return Optional.empty();
        }
        int hash = iri.indexOf('#');
        String documentPart = hash < 0 ? iri : iri.substring(0, hash);
        String fragmentPart = hash < 0 ? "" : iri.substring(hash + 1);
        if (StringUtils.isBlank(documentPart) || StringUtils.isBlank(fragmentPart)) {
            return Optional.empty();
        }
        URI target;
        try {
            target = new URI(documentPart);
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
        if (target.isAbsolute()) {
            return Optional.empty();
        }
        SvgGraphic externalGraphic = FoxgloveParser.shared()
            .parseFile(baseUri.resolve(target));
        if (externalGraphic == null) {
            return Optional.empty();
        }
        return externalGraphic.getElementIndex()
            .resolveWithOwner("#" + fragmentPart);
    }

    /**
     * Resolves a same document reference to an element of the expected type. An element of a different type yields an empty result, as a reference to the wrong kind of element is
     * not usable by the caller.
     */
    public <T extends ISvgElement> Optional<T> resolve(String reference, Class<T> type) {
        return resolve(reference).filter(type::isInstance)
            .map(type::cast);
    }

    /**
     * Every element in the document matching {@code type}, in document order, regardless of whether it carries an {@code id} - unlike {@link #resolve}, which only ever finds an
     * element someone can reference by name. Used to find every animation element in a document (#30), most of which have no reason to declare an {@code id} at all.
     */
    public <T> List<T> getElementsOfType(Class<T> type) {
        return allElements.stream()
            .filter(type::isInstance)
            .map(type::cast)
            .toList();
    }

    /**
     * Follows a chain of references from the given element, as gradients and patterns do through {@code xlink:href}. The returned list starts with {@code start} and continues
     * while each element references another of the same type. A cycle terminates the chain at the point the repeat is found, so the result is always finite and each element
     * appears at most once.
     */
    public <T extends ISvgElement> List<T> resolveChain(T start, Function<? super T, String> reference, Class<T> type) {
        List<T> chain = new ArrayList<>();
        Set<ISvgElement> visited = Sets.newIdentityHashSet();
        for (T current = start; current != null && visited.add(current); current = resolve(reference.apply(current), type).orElse(null)) {
            chain.add(current);
        }
        return chain;
    }

    /**
     * The element that contains the given one, or empty for the document root and for any element that was not part of the document when the index was built.
     */
    public Optional<ISvgElement> getParent(ISvgElement element) {
        return Optional.ofNullable(parents.get(element));
    }

    /**
     * Whether {@code candidate} is the given element or one of its ancestors. This is what makes a {@code <use>} reference illegal, as reusing an ancestor would expand forever.
     */
    public boolean isSelfOrAncestor(ISvgElement candidate, ISvgElement element) {
        for (ISvgElement current = element; current != null; current = parents.get(current)) {
            if (current == candidate) {
                return true;
            }
        }
        return false;
    }

    /**
     * The ids declared more than once, in the order they were first seen. Empty for a well formed document.
     */
    public Set<String> getDuplicateIds() {
        return Collections.unmodifiableSet(duplicateIds);
    }

    /**
     * The elements carrying an id, keyed on that id, in document order.
     */
    public Map<String, ISvgElement> getElementsById() {
        return Collections.unmodifiableMap(elementsById);
    }

    public int size() {
        return elementsById.size();
    }

    private void add(ISvgElement element, ISvgElement parent, Set<ISvgElement> visited) {
        // An element reached twice would mean a malformed object graph rather than a malformed document, but guarding
        // here keeps the walk terminating either way
        if (!visited.add(element)) {
            return;
        }
        parents.put(element, parent);
        allElements.add(element);
        String id = element.getId();
        if (StringUtils.isNotBlank(id) && elementsById.putIfAbsent(id, element) != null) {
            duplicateIds.add(id);
        }
        if (element instanceof SvgStyle style) {
            styleElements.add(style);
        }
        for (ISvgElement child : getChildren(element)) {
            add(child, element, visited);
        }
    }

    /**
     * Collects the child elements of an element.
     * <p>
     * The content accessors across the element classes are not uniform - most expose {@code getContent()} returning a {@code List<ISvgElement>}, the animation elements use
     * {@code getContents()}, a few return a list of a narrower type, and {@code SvgText} returns a {@code List<Object>} of mixed character data and elements. Reading the
     * collection fields directly covers every element type through one code path, and picks up any type added later without it having to be registered here.
     */
    private static List<ISvgElement> getChildren(ISvgElement element) {
        List<ISvgElement> children = new ArrayList<>();
        for (Field field : getContentFields(element.getClass())) {
            try {
                Object value = field.get(element);
                if (value instanceof Collection) {
                    for (Object item : (Collection<?>) value) {
                        if (item instanceof ISvgElement) {
                            children.add((ISvgElement) item);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Unable to read content of " + element.getElementName(), e);
            }
        }
        return children;
    }

    private static List<Field> getContentFields(Class<?> type) {
        return CONTENT_FIELDS.computeIfAbsent(type, SvgElementIndex::findContentFields);
    }

    private static List<Field> findContentFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> current = type; current != null && current != Object.class; current = current.getSuperclass()) {
            for (Field field : current.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && Collection.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    fields.add(field);
                }
            }
        }
        return fields;
    }

}
