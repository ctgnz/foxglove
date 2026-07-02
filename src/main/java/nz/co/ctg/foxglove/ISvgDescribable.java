package nz.co.ctg.foxglove;

import java.util.Optional;

import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;

import jakarta.xml.bind.annotation.XmlTransient;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;

public interface ISvgDescribable extends ISvgContent {

    default void createDescription(String descriptionText) {
        SvgDescription desc = new SvgDescription();
        desc.setValue(descriptionText);
        getContent().add(desc);
    }

    default void createTitle(String titleText) {
        SvgTitle title = new SvgTitle();
        title.setValue(titleText);
        getContent().add(title);
    }

    @XmlTransient
    default Optional<SvgDescription> getDescription() {
        return getOptionalContent(SvgDescription.class);
    }

    @XmlTransient
    default Optional<SvgMetadata> getMetadata() {
        return getOptionalContent(SvgMetadata.class);
    }

    @XmlTransient
    default Optional<SvgTitle> getTitle() {
        return getOptionalContent(SvgTitle.class);
    }

    default void installTooltip(Node node) {
        getTitle().ifPresentOrElse(desc -> {
            Tooltip.install(node, new Tooltip(desc.getValue()));
        }, () -> node.setMouseTransparent(true));
    }

}