package nz.co.ctg.foxglove.text;

import java.util.List;
import java.util.stream.Stream;

import nz.co.ctg.foxglove.AbstractSvgStylable;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlTransient
public abstract class AbstractSvgTextContentElement extends AbstractSvgStylable implements ISvgTextContentElement {

    public abstract List<Object> getContent();

    @XmlTransient
    public List<ISvgTextContentElement> getTextContent() {
        return streamTextContent().collect(toList());
    }

    @Override
    @XmlTransient
    public String getValue() {
        if (getContent().stream().allMatch(String.class::isInstance)) {
            return getContent().stream().map(String.class::cast).collect(joining(" "));
        }
        return streamTextContent().findFirst().map(ISvgTextContentElement::getValue).orElse(null);
    }

    private Stream<ISvgTextContentElement> streamTextContent() {
        return getContent().stream()
            .filter(ISvgTextContentElement.class::isInstance)
            .map(ISvgTextContentElement.class::cast);
    }

}
