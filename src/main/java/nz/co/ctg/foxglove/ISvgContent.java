package nz.co.ctg.foxglove;

import java.util.List;
import java.util.Optional;

public interface ISvgContent {

    default <T> Optional<T> getOptionalContent(Class<T> contentType) {
        return getContent().stream().filter(contentType::isInstance).map(contentType::cast).findFirst();
    }

    List<ISvgElement> getContent();

}