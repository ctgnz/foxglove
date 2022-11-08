package nz.co.ctg.foxglove;

import java.util.Optional;

import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;

public interface ISvgDescribable extends ISvgContent {

    default Optional<SvgDescription> getDescription() {
        return getOptionalContent(SvgDescription.class);
    }

    default Optional<SvgMetadata> getMetadata() {
        return getOptionalContent(SvgMetadata.class);
    }

    default Optional<SvgTitle> getTitle() {
        return getOptionalContent(SvgTitle.class);
    }

    default void installTooltip(Node node) {
        getTitle().ifPresent(desc -> {
            Tooltip.install(node, new Tooltip(desc.getValue()));
        });
    }

}