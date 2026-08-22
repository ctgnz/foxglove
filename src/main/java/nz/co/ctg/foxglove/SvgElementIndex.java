package nz.co.ctg.foxglove;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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

/**
 * An index of the elements in a parsed document, keyed on their {@code id}, supporting the same document references
 * used throughout SVG: {@code url(#id)} in presentation attributes such as {@code fill} and {@code clip-path}, and
 * {@code xlink:href="#id"} on elements such as {@code <use>} and {@code <textPath>}.
 * <p>
 * The index is a snapshot taken when it is built, so an index taken over a document that is subsequently modified is
 * stale - see {@link SvgGraphic#rebuildElementIndex()}. Forward references resolve, as the whole document is walked
 * before any lookup is served.
 * <p>
 * Where a document declares the same {@code id} more than once it is in error, and the SVG specification leaves the
 * outcome undefined. This index keeps the first element encountered in document order and reports the offending id
 * from {@link #getDuplicateIds()}.
 * <p>
 * Only same document references are resolved. A reference naming another document, such as
 * {@code xlink:href="other.svg#id"}, yields an empty result rather than an error, leaving room for external documents
 * to be supported later without changing the signature.
 */
public final class SvgElementIndex {

    private static final String URL_PREFIX = "url(";
    private static final Map<Class<?>, List<Field>> CONTENT_FIELDS = Maps.newConcurrentMap();

    /**
     * Builds an index over the given document. The whole tree is walked, including nested {@code <svg>} elements and
     * the contents of {@code <defs>}.
     */
    public static SvgElementIndex of(SvgGraphic root) {
        SvgElementIndex index = new SvgElementIndex();
        if (root != null) {
            index.add(root, null, Sets.newIdentityHashSet());
        }
        return index;
    }

    /**
     * Extracts the target id from a same document reference, accepting either a bare {@code #id} fragment or a
     * {@code url(#id)} wrapper, with or without quotes, and ignoring anything after the closing bracket so that a
     * paint fallback such as {@code url(#grad) red} parses.
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
            iri = StringUtils.strip(iri.substring(URL_PREFIX.length(), close).trim(), "\"'");
        }
        // Anything before the '#' names another document, which is not resolvable against this index
        if (!StringUtils.startsWith(iri, "#")) {
            return Optional.empty();
        }
        return Optional.of(iri.substring(1).trim()).filter(StringUtils::isNotEmpty);
    }

    private final Map<String, ISvgElement> elementsById = new LinkedHashMap<>();
    private final Map<ISvgElement, ISvgElement> parents = new IdentityHashMap<>();
    private final Set<String> duplicateIds = new LinkedHashSet<>();

    private SvgElementIndex() {
    }

    /**
     * Resolves a same document reference to the element it names.
     */
    public Optional<ISvgElement> resolve(String reference) {
        return parseReference(reference).map(elementsById::get);
    }

    /**
     * Resolves a same document reference to an element of the expected type. An element of a different type yields an
     * empty result, as a reference to the wrong kind of element is not usable by the caller.
     */
    public <T extends ISvgElement> Optional<T> resolve(String reference, Class<T> type) {
        return resolve(reference).filter(type::isInstance).map(type::cast);
    }

    /**
     * Follows a chain of references from the given element, as gradients and patterns do through {@code xlink:href}.
     * The returned list starts with {@code start} and continues while each element references another of the same
     * type. A cycle terminates the chain at the point the repeat is found, so the result is always finite and each
     * element appears at most once.
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
     * The element that contains the given one, or empty for the document root and for any element that was not part
     * of the document when the index was built.
     */
    public Optional<ISvgElement> getParent(ISvgElement element) {
        return Optional.ofNullable(parents.get(element));
    }

    /**
     * Whether {@code candidate} is the given element or one of its ancestors. This is what makes a {@code <use>}
     * reference illegal, as reusing an ancestor would expand forever.
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
        String id = element.getId();
        if (StringUtils.isNotBlank(id) && elementsById.putIfAbsent(id, element) != null) {
            duplicateIds.add(id);
        }
        for (ISvgElement child : getChildren(element)) {
            add(child, element, visited);
        }
    }

    /**
     * Collects the child elements of an element.
     * <p>
     * The content accessors across the element classes are not uniform - most expose {@code getContent()} returning a
     * {@code List<ISvgElement>}, the animation elements use {@code getContents()}, a few return a list of a narrower
     * type, and {@code SvgText} returns a {@code List<Object>} of mixed character data and elements. Reading the
     * collection fields directly covers every element type through one code path, and picks up any type added later
     * without it having to be registered here.
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
