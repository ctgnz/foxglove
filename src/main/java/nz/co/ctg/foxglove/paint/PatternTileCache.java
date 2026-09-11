package nz.co.ctg.foxglove.paint;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import javafx.scene.image.Image;

/**
 * Caches a pattern's rasterised tiles, one map per pattern instance, keyed on the resolved pixel size so a different
 * bounding box (under {@code objectBoundingBox} {@code patternUnits}) gets its own entry.
 * <p>
 * Deliberately not a field on {@link SvgPattern} itself: {@code SvgPattern} is JAXB-bound, and EclipseLink MOXy
 * resolves a {@code Map} field's value type even when the field is {@code @XmlTransient} - {@link Image} has no
 * zero-argument constructor, which made the whole context fail to build. A {@link WeakHashMap} keyed on the pattern
 * gives the same effective lifetime (gone once the pattern, and so the document, is) without touching it at all.
 */
final class PatternTileCache {

    private static final Map<SvgPattern, Map<String, Image>> CACHE = Collections.synchronizedMap(new WeakHashMap<>());

    static Image getOrRasterize(SvgPattern pattern, double width, double height, Supplier<Image> rasterizer) {
        Map<String, Image> tiles = CACHE.computeIfAbsent(pattern, p -> new ConcurrentHashMap<>());
        return tiles.computeIfAbsent(width + "x" + height, key -> rasterizer.get());
    }

    private PatternTileCache() {
    }

}
