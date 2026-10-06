package nz.co.ctg.foxglove.description;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Read-only Dublin Core accessors over the RDF block of a {@code <metadata>} element (#169), from {@link SvgMetadata#getDublinCore()}.
 * <p>
 * Deliberately permissive about RDF structure, because the metadata real documents carry is not the textbook {@code rdf:Description}: Inkscape - which wrote every SVG in jmsfx
 * that has metadata - puts its Dublin Core properties in a typed node, {@code <cc:Work rdf:about="">}, and gives {@code dc:type} its value as an {@code rdf:resource} attribute
 * rather than as text. So properties are read from every node directly under {@code <rdf:RDF>}, whatever its type, in document order; a value is the property's text, or failing
 * that its {@code rdf:resource}; and a blank value - Inkscape's empty {@code <dc:title/>} - counts as absent. Nothing is validated or rejected.
 * <p>
 * Read-only by design: it reads the DOM {@link SvgMetadata#getContent()} already captures, and writes nothing back.
 */
public final class DublinCore {

    /** The Dublin Core Metadata Element Set, version 1.1. */
    public static final String NAMESPACE = "http://purl.org/dc/elements/1.1/";

    static final String RDF_NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";

    /**
     * One value of a Dublin Core property.
     *
     * @param text
     *            the property's text, or failing that its {@code rdf:resource}; never blank
     * @param language
     *            its {@code xml:lang}, or {@code null} when it has none - kept for the caller, never used here to choose between values
     */
    public record Value(String text, String language) {

        public Optional<String> getLanguage() {
            return Optional.ofNullable(language);
        }
    }

    private final Element rdf;

    DublinCore(Element rdf) {
        this.rdf = rdf;
    }

    /** The {@code <rdf:RDF>} element these properties are read from. */
    public Element getRdf() {
        return rdf;
    }

    /**
     * Every value of one Dublin Core property, in document order, across every node under {@code <rdf:RDF>}.
     *
     * @param term
     *            the property's local name in the Dublin Core namespace, for example {@code "creator"}
     */
    public List<Value> getValues(String term) {
        List<Value> values = new ArrayList<>();
        for (Element node : childElements(rdf)) {
            for (Element property : childElements(node)) {
                if (NAMESPACE.equals(property.getNamespaceURI()) && term.equals(property.getLocalName())) {
                    valueOf(property).ifPresent(values::add);
                }
            }
        }
        return values;
    }

    /** The text of every value of one Dublin Core property, in document order. */
    public List<String> getAll(String term) {
        return getValues(term).stream()
            .map(Value::text)
            .toList();
    }

    /** The first value of one Dublin Core property, if it has one. */
    public Optional<String> getFirst(String term) {
        return getAll(term).stream()
            .findFirst();
    }

    public Optional<String> getContributor() {
        return getFirst("contributor");
    }

    public Optional<String> getCoverage() {
        return getFirst("coverage");
    }

    public Optional<String> getCreator() {
        return getFirst("creator");
    }

    public Optional<String> getDate() {
        return getFirst("date");
    }

    public Optional<String> getDescription() {
        return getFirst("description");
    }

    public Optional<String> getFormat() {
        return getFirst("format");
    }

    public Optional<String> getIdentifier() {
        return getFirst("identifier");
    }

    public Optional<String> getLanguage() {
        return getFirst("language");
    }

    public Optional<String> getPublisher() {
        return getFirst("publisher");
    }

    public Optional<String> getRelation() {
        return getFirst("relation");
    }

    public Optional<String> getRights() {
        return getFirst("rights");
    }

    public Optional<String> getSource() {
        return getFirst("source");
    }

    public Optional<String> getSubject() {
        return getFirst("subject");
    }

    public Optional<String> getTitle() {
        return getFirst("title");
    }

    public Optional<String> getType() {
        return getFirst("type");
    }

    /**
     * The property's text - including the text of nested nodes, which is how Inkscape writes {@code dc:creator}: a {@code cc:Agent} holding a {@code dc:title} - or failing that
     * its {@code rdf:resource}.
     */
    private static Optional<Value> valueOf(Element property) {
        String text = property.getTextContent()
            .strip();
        if (text.isEmpty()) {
            text = property.getAttributeNS(RDF_NAMESPACE, "resource")
                .strip();
        }
        if (text.isEmpty()) {
            return Optional.empty();
        }
        String language = property.getAttributeNS(XML_NAMESPACE, "lang");
        return Optional.of(new Value(text, language.isEmpty() ? null : language));
    }

    private static List<Element> childElements(Element parent) {
        List<Element> elements = new ArrayList<>();
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element element) {
                elements.add(element);
            }
        }
        return elements;
    }
}
