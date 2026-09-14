package nz.co.ctg.foxglove.description;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.AbstractSvgElement;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlMixed;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * {@code <metadata>}'s content model is "any elements or character data" (#2) - almost always a single
 * {@code <rdf:RDF>} block in practice (RDF/Dublin Core, the schemas the issue itself names), but the specification
 * does not require exactly one child, so this captures every child in document order rather than only the first.
 * <p>
 * {@code @XmlAnyElement} captures an element from a namespace this binding has no class for as a real DOM
 * {@link org.w3c.dom.Element} (the same mechanism {@code SvgForeignObject} already uses for {@code <foreignObject>}
 * content), and {@code @XmlMixed} alongside it also keeps whatever character data sits between/around those
 * elements - both needed together for the marshaller to round-trip the original content rather than only the parts
 * one alone would capture. Deliberately raw rather than a typed RDF/Dublin Core domain model for now: nothing in
 * this codebase reads {@code <metadata>}'s content today, so there is no consumer yet to design a typed API around,
 * and a caller who does have a specific RDF/Dublin Core need can query the DOM directly. Typed accessors are their
 * own, separate follow-up (issue filed once this lands) once there's a concrete need driving their shape.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "metadata")
public class SvgMetadata extends AbstractSvgElement implements ISvgDescriptiveElement {

    @XmlAnyElement
    @XmlMixed
    private List<Object> content;

    public SvgMetadata() {
    }

    /**
     * Every child in document order - a {@link String} for character data, an {@link org.w3c.dom.Element} for a
     * child from any namespace (there is no other kind of child this binding has its own class for).
     */
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return content;
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        super.toStringDetail(builder);
        builder.add("content", content);
    }
}
