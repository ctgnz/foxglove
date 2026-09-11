package nz.co.ctg.foxglove;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.style.CssDeclaration;
import nz.co.ctg.foxglove.style.CssDeclarations;
import nz.co.ctg.foxglove.style.CssStylesheet;
import nz.co.ctg.foxglove.style.SvgPropertyTable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlTransient
public abstract class AbstractSvgStylable extends AbstractSvgElement implements ISvgStylable {

    public AbstractSvgStylable() {
    }

    public boolean isVisible() {
        String display = getDisplay();
        return display == null || !StringUtils.equalsIgnoreCase("none", display);
    }

    /**
     * Applies this element's style cascade: any stylesheet rule that matches it, then its own inline {@code style}
     * attribute, then {@code !important} declarations from either - each step overwriting only the properties it
     * mentions, so an earlier step's value survives untouched where a later one is silent.
     * <p>
     * A presentation attribute such as {@code fill="red"} needs no step here - it is already in the property map,
     * parsed by JAXB before this ever runs, and simply stands as the value beneath all of this until overwritten.
     */
    public void applyStyle(RenderContext context) {
        CssStylesheet stylesheet = context.getElementIndex() == null ? null : context.getElementIndex().getStylesheet();
        List<CssDeclaration> stylesheetDeclarations = stylesheet == null ? List.of() : stylesheet.matchingDeclarations(this);
        List<CssDeclaration> inlineDeclarations = CssDeclarations.parse(getStyle());

        applyAll(stylesheetDeclarations, false);
        applyAll(inlineDeclarations, false);
        applyAll(stylesheetDeclarations, true);
        applyAll(inlineDeclarations, true);
    }

    private void applyAll(List<CssDeclaration> declarations, boolean important) {
        for (CssDeclaration declaration : declarations) {
            if (declaration.important() == important) {
                SvgPropertyTable.apply(this, declaration.property(), declaration.value());
            }
        }
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        super.toStringDetail(builder);
        ISvgStylable.super.toStringDetail(builder);
    }

}
