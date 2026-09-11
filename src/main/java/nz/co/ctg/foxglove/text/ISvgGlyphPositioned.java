package nz.co.ctg.foxglove.text;

import java.util.List;

/**
 * A text content element that carries SVG's per-character positioning lists (#28) - {@code x}, {@code y},
 * {@code dx}, {@code dy} and {@code rotate} each apply to successive characters of the element's own text, with any
 * character beyond the list's length either continuing the normal flow ({@code x}/{@code y}/{@code dx}/{@code dy})
 * or repeating the list's last value ({@code rotate}), per the specification.
 * <p>
 * Implemented by every text content element that declares these attributes - {@code SvgTextPath} does not, since a
 * {@code textPath}'s position comes from the path it follows rather than from {@code x}/{@code y}.
 */
public interface ISvgGlyphPositioned {

    List<Double> getX();

    List<Double> getY();

    List<Double> getDx();

    List<Double> getDy();

    List<Double> getRotate();

}
