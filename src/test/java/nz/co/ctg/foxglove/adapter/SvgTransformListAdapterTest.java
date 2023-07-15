package nz.co.ctg.foxglove.adapter;

import java.util.List;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

public class SvgTransformListAdapterTest {
    private SvgTransformListAdapter candidate = new SvgTransformListAdapter();

    @Test
    public void testParseSpacing() {
        List<Transform> result = candidate.parse("matrix(1 2 3 4 5 6)");
        assertThat(result, hasSize(1));
        result = candidate.parse("matrix(1,2,3,4,5,6)");
        assertThat(result, hasSize(1));
        result = candidate.parse("matrix(1, 2, 3, 4, 5, 6)");
        assertThat(result, hasSize(1));
        result = candidate.parse("matrix(1,2 3,4 5,6)");
        assertThat(result, hasSize(1));
    }

    @Test
    public void testMultiple() {
        List<Transform> result = candidate.parse("matrix(1,2,3,4,5,6)");
        assertThat(result, hasSize(1));
        result = candidate.parse("matrix(1,2,3,4,5,6) translate(42)");
        assertThat(result, hasSize(2));
        result = candidate.parse("matrix(1,2,3,4,5,6) translate(42) scale(42)");
        assertThat(result, hasSize(3));
        result = candidate.parse("matrix(1,2,3,4,5,6) translate(42) scale(42) rotate(42)");
        assertThat(result, hasSize(4));
        result = candidate.parse("matrix(1,2,3,4,5,6) translate(42) scale(42) rotate(42) skewX(42)");
        assertThat(result, hasSize(5));
        result = candidate.parse("matrix(1,2,3,4,5,6) translate(42) scale(42) rotate(42) skewX(42) skewY(42)");
        assertThat(result, hasSize(6));
    }

    @Test
    public void testParseMatrix() {
        List<Transform> result = candidate.parse("matrix(1 2 3 4 5 6)");
        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(instanceOf(Affine.class)));
        Affine affine = (Affine) result.get(0);
        assertThat(affine.getMxx(), is(1.0));
        assertThat(affine.getMxy(), is(3.0));
        assertThat(affine.getTx(), is(5.0));
        assertThat(affine.getMyx(), is(2.0));
        assertThat(affine.getMyy(), is(4.0));
        assertThat(affine.getTy(), is(6.0));
    }

    @Test
    public void testParseTranslate() {
        List<Transform> result = candidate.parse("translate(42 24)");
        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(instanceOf(Translate.class)));
        Translate translate = (Translate) result.get(0);
        assertThat(translate.getX(), is(42.0));
        assertThat(translate.getY(), is(24.0));
    }

    @Test
    public void testParseTranslateHorizontal() {
        List<Transform> result = candidate.parse("translate(42)");
        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(instanceOf(Translate.class)));
        Translate translate = (Translate) result.get(0);
        assertThat(translate.getX(), is(42.0));
        assertThat(translate.getY(), is(0.0));
    }

    @Test
    public void testParseScaleProportional() {
        List<Transform> result = candidate.parse("scale(42)");
        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(instanceOf(Scale.class)));
        Scale scale = (Scale) result.get(0);
        assertThat(scale.getX(), is(42.0));
        assertThat(scale.getY(), is(42.0));
    }

    @Test
    public void testParseScaleSeparate() {
        List<Transform> result = candidate.parse("scale(42 24)");
        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(instanceOf(Scale.class)));
        Scale scale = (Scale) result.get(0);
        assertThat(scale.getX(), is(42.0));
        assertThat(scale.getY(), is(24.0));
    }

    @Test
    public void testParseRotate() {
        List<Transform> result = candidate.parse("rotate(42)");
        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(instanceOf(Rotate.class)));
        Rotate rotate = (Rotate) result.get(0);
        assertThat(rotate.getAngle(), is(42.0));
        assertThat(rotate.getPivotX(), is(0.0));
        assertThat(rotate.getPivotY(), is(0.0));
    }

    @Test
    public void testParseRotateWithPivot() {
        List<Transform> result = candidate.parse("rotate(42 23 56)");
        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(instanceOf(Rotate.class)));
        Rotate rotate = (Rotate) result.get(0);
        assertThat(rotate.getAngle(), is(42.0));
        assertThat(rotate.getPivotX(), is(23.0));
        assertThat(rotate.getPivotY(), is(56.0));
    }

    @Test
    public void testParseSkewX() {
        List<Transform> result = candidate.parse("skewX(42)");
        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(instanceOf(Shear.class)));
        Shear shear = (Shear) result.get(0);
        assertThat(shear.getX(), is(42.0));
        assertThat(shear.getY(), is(0.0));
    }

    @Test
    public void testParseSkewY() {
        List<Transform> result = candidate.parse("skewY(42)");
        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(instanceOf(Shear.class)));
        Shear shear = (Shear) result.get(0);
        assertThat(shear.getX(), is(0.0));
        assertThat(shear.getY(), is(42.0));
    }

}
