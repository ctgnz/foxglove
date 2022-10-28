package nz.co.ctg.foxglove;

import javax.xml.bind.annotation.XmlRegistry;

import nz.co.ctg.foxglove.animate.SvgAnimate;
import nz.co.ctg.foxglove.animate.SvgAnimateColor;
import nz.co.ctg.foxglove.animate.SvgAnimateMotion;
import nz.co.ctg.foxglove.animate.SvgAnimateTransform;
import nz.co.ctg.foxglove.animate.SvgMotionPath;
import nz.co.ctg.foxglove.animate.SvgSet;
import nz.co.ctg.foxglove.clip.SvgClipElement;
import nz.co.ctg.foxglove.clip.SvgClipPath;
import nz.co.ctg.foxglove.clip.SvgMask;
import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;
import nz.co.ctg.foxglove.element.SvgAnchor;
import nz.co.ctg.foxglove.element.SvgCursor;
import nz.co.ctg.foxglove.element.SvgDefs;
import nz.co.ctg.foxglove.element.SvgForeignObject;
import nz.co.ctg.foxglove.element.SvgGroup;
import nz.co.ctg.foxglove.element.SvgImage;
import nz.co.ctg.foxglove.element.SvgMarker;
import nz.co.ctg.foxglove.element.SvgScript;
import nz.co.ctg.foxglove.element.SvgSwitch;
import nz.co.ctg.foxglove.element.SvgSymbol;
import nz.co.ctg.foxglove.element.SvgUse;
import nz.co.ctg.foxglove.element.SvgView;
import nz.co.ctg.foxglove.filter.FeBlend;
import nz.co.ctg.foxglove.filter.FeColorMatrix;
import nz.co.ctg.foxglove.filter.FeComponentTransfer;
import nz.co.ctg.foxglove.filter.FeComposite;
import nz.co.ctg.foxglove.filter.FeConvolveMatrix;
import nz.co.ctg.foxglove.filter.FeDiffuseLighting;
import nz.co.ctg.foxglove.filter.FeDisplacementMap;
import nz.co.ctg.foxglove.filter.FeDistantLight;
import nz.co.ctg.foxglove.filter.FeFlood;
import nz.co.ctg.foxglove.filter.FeFuncA;
import nz.co.ctg.foxglove.filter.FeFuncB;
import nz.co.ctg.foxglove.filter.FeFuncG;
import nz.co.ctg.foxglove.filter.FeFuncR;
import nz.co.ctg.foxglove.filter.FeGaussianBlur;
import nz.co.ctg.foxglove.filter.FeImage;
import nz.co.ctg.foxglove.filter.FeMerge;
import nz.co.ctg.foxglove.filter.FeMergeNode;
import nz.co.ctg.foxglove.filter.FeMorphology;
import nz.co.ctg.foxglove.filter.FeOffset;
import nz.co.ctg.foxglove.filter.FePointLight;
import nz.co.ctg.foxglove.filter.FeSpecularLighting;
import nz.co.ctg.foxglove.filter.FeSpotLight;
import nz.co.ctg.foxglove.filter.FeTile;
import nz.co.ctg.foxglove.filter.FeTurbulence;
import nz.co.ctg.foxglove.filter.Filter;
import nz.co.ctg.foxglove.paint.SvgColorProfile;
import nz.co.ctg.foxglove.paint.SvgLinearGradient;
import nz.co.ctg.foxglove.paint.SvgPattern;
import nz.co.ctg.foxglove.paint.SvgRadialGradient;
import nz.co.ctg.foxglove.paint.SvgStop;
import nz.co.ctg.foxglove.shape.SvgCircle;
import nz.co.ctg.foxglove.shape.SvgEllipse;
import nz.co.ctg.foxglove.shape.SvgLine;
import nz.co.ctg.foxglove.shape.SvgPath;
import nz.co.ctg.foxglove.shape.SvgPolygon;
import nz.co.ctg.foxglove.shape.SvgPolyline;
import nz.co.ctg.foxglove.shape.SvgRectangle;
import nz.co.ctg.foxglove.style.SvgStyle;
import nz.co.ctg.foxglove.text.SvgAltGlyph;
import nz.co.ctg.foxglove.text.SvgAltGlyphDef;
import nz.co.ctg.foxglove.text.SvgAltGlyphItem;
import nz.co.ctg.foxglove.text.SvgFont;
import nz.co.ctg.foxglove.text.SvgFontFace;
import nz.co.ctg.foxglove.text.SvgFontFaceFormat;
import nz.co.ctg.foxglove.text.SvgFontFaceName;
import nz.co.ctg.foxglove.text.SvgFontFaceSrc;
import nz.co.ctg.foxglove.text.SvgFontFaceUri;
import nz.co.ctg.foxglove.text.SvgGlyph;
import nz.co.ctg.foxglove.text.SvgGlyphRef;
import nz.co.ctg.foxglove.text.SvgHorizontalKerning;
import nz.co.ctg.foxglove.text.SvgMissingGlyph;
import nz.co.ctg.foxglove.text.SvgText;
import nz.co.ctg.foxglove.text.SvgTextPath;
import nz.co.ctg.foxglove.text.SvgTextReference;
import nz.co.ctg.foxglove.text.SvgTextSpan;
import nz.co.ctg.foxglove.text.SvgVerticalKerning;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the nz.co.ctg.jmsfx.svg package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: nz.co.ctg.jmsfx.svg
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SvgSymbol }
     *
     */
    public SvgSymbol createSymbol() {
        return new SvgSymbol();
    }

    /**
     * Create an instance of {@link SvgDescription }
     *
     */
    public SvgDescription createDesc() {
        return new SvgDescription();
    }

    /**
     * Create an instance of {@link SvgTitle }
     *
     */
    public SvgTitle createTitle() {
        return new SvgTitle();
    }

    /**
     * Create an instance of {@link SvgMetadata }
     *
     */
    public SvgMetadata createMetadata() {
        return new SvgMetadata();
    }

    /**
     * Create an instance of {@link SvgAnimate }
     *
     */
    public SvgAnimate createAnimate() {
        return new SvgAnimate();
    }

    /**
     * Create an instance of {@link SvgSet }
     *
     */
    public SvgSet createSet() {
        return new SvgSet();
    }

    /**
     * Create an instance of {@link SvgAnimateMotion }
     *
     */
    public SvgAnimateMotion createAnimateMotion() {
        return new SvgAnimateMotion();
    }

    /**
     * Create an instance of {@link SvgAnimateColor }
     *
     */
    public SvgAnimateColor createAnimateColor() {
        return new SvgAnimateColor();
    }

    /**
     * Create an instance of {@link SvgAnimateTransform }
     *
     */
    public SvgAnimateTransform createAnimateTransform() {
        return new SvgAnimateTransform();
    }

    /**
     * Create an instance of {@link SvgGraphic }
     *
     */
    public SvgGraphic createSvgRootElement() {
        return new SvgGraphic();
    }

    /**
     * Create an instance of {@link SvgGroup }
     *
     */
    public SvgGroup createSvgGroup() {
        return new SvgGroup();
    }

    /**
     * Create an instance of {@link SvgDefs }
     *
     */
    public SvgDefs createDefs() {
        return new SvgDefs();
    }

    /**
     * Create an instance of {@link SvgUse }
     *
     */
    public SvgUse createUse() {
        return new SvgUse();
    }

    /**
     * Create an instance of {@link SvgSwitch }
     *
     */
    public SvgSwitch createSwitch() {
        return new SvgSwitch();
    }

    /**
     * Create an instance of {@link SvgImage }
     *
     */
    public SvgImage createImage() {
        return new SvgImage();
    }

    /**
     * Create an instance of {@link SvgStyle }
     *
     */
    public SvgStyle createStyle() {
        return new SvgStyle();
    }

    /**
     * Create an instance of {@link SvgPath }
     *
     */
    public SvgPath createSvgPath() {
        return new SvgPath();
    }

    /**
     * Create an instance of {@link SvgRectangle }
     *
     */
    public SvgRectangle createSvgRectangle() {
        return new SvgRectangle();
    }

    /**
     * Create an instance of {@link SvgCircle }
     *
     */
    public SvgCircle createSvgCircle() {
        return new SvgCircle();
    }

    /**
     * Create an instance of {@link SvgLine }
     *
     */
    public SvgLine createSvgLine() {
        return new SvgLine();
    }

    /**
     * Create an instance of {@link SvgEllipse }
     *
     */
    public SvgEllipse createSvgEllipse() {
        return new SvgEllipse();
    }

    /**
     * Create an instance of {@link SvgPolyline }
     *
     */
    public SvgPolyline createSvgPolyline() {
        return new SvgPolyline();
    }

    /**
     * Create an instance of {@link SvgPolygon }
     *
     */
    public SvgPolygon createSvgPolygon() {
        return new SvgPolygon();
    }

    /**
     * Create an instance of {@link SvgText }
     *
     */
    public SvgText createSvgText() {
        return new SvgText();
    }

    /**
     * Create an instance of {@link SvgAltGlyphDef }
     *
     */
    public SvgAltGlyphDef createAltGlyphDef() {
        return new SvgAltGlyphDef();
    }

    /**
     * Create an instance of {@link SvgMarker }
     *
     */
    public SvgMarker createMarker() {
        return new SvgMarker();
    }

    /**
     * Create an instance of {@link SvgColorProfile }
     *
     */
    public SvgColorProfile createColorProfile() {
        return new SvgColorProfile();
    }

    /**
     * Create an instance of {@link SvgLinearGradient }
     *
     */
    public SvgLinearGradient createLinearGradient() {
        return new SvgLinearGradient();
    }

    /**
     * Create an instance of {@link SvgRadialGradient }
     *
     */
    public SvgRadialGradient createRadialGradient() {
        return new SvgRadialGradient();
    }

    /**
     * Create an instance of {@link SvgPattern }
     *
     */
    public SvgPattern createPattern() {
        return new SvgPattern();
    }

    /**
     * Create an instance of {@link SvgClipPath }
     *
     */
    public SvgClipElement createClipPath() {
        return new SvgClipPath();
    }

    /**
     * Create an instance of {@link SvgMask }
     *
     */
    public SvgMask createMask() {
        return new SvgMask();
    }

    /**
     * Create an instance of {@link Filter }
     *
     */
    public Filter createFilter() {
        return new Filter();
    }

    /**
     * Create an instance of {@link SvgCursor }
     *
     */
    public SvgCursor createCursor() {
        return new SvgCursor();
    }

    /**
     * Create an instance of {@link SvgAnchor }
     *
     */
    public SvgAnchor createAnchor() {
        return new SvgAnchor();
    }

    /**
     * Create an instance of {@link SvgView }
     *
     */
    public SvgView createView() {
        return new SvgView();
    }

    /**
     * Create an instance of {@link SvgScript }
     *
     */
    public SvgScript createScript() {
        return new SvgScript();
    }

    /**
     * Create an instance of {@link SvgFont }
     *
     */
    public SvgFont createFont() {
        return new SvgFont();
    }

    /**
     * Create an instance of {@link SvgFontFace }
     *
     */
    public SvgFontFace createFontFace() {
        return new SvgFontFace();
    }

    /**
     * Create an instance of {@link SvgForeignObject }
     *
     */
    public SvgForeignObject createForeignObject() {
        return new SvgForeignObject();
    }

    /**
     * Create an instance of {@link SvgAltGlyph }
     *
     */
    public SvgAltGlyph createAltGlyph() {
        return new SvgAltGlyph();
    }

    /**
     * Create an instance of {@link SvgGlyphRef }
     *
     */
    public SvgGlyphRef createGlyphRef() {
        return new SvgGlyphRef();
    }

    /**
     * Create an instance of {@link SvgAltGlyphItem }
     *
     */
    public SvgAltGlyphItem createAltGlyphItem() {
        return new SvgAltGlyphItem();
    }

    /**
     * Create an instance of {@link FeImage }
     *
     */
    public FeImage createFeImage() {
        return new FeImage();
    }

    /**
     * Create an instance of {@link FeConvolveMatrix }
     *
     */
    public FeConvolveMatrix createFeConvolveMatrix() {
        return new FeConvolveMatrix();
    }

    /**
     * Create an instance of {@link FeComposite }
     *
     */
    public FeComposite createFeComposite() {
        return new FeComposite();
    }

    /**
     * Create an instance of {@link SvgHorizontalKerning }
     *
     */
    public SvgHorizontalKerning createHkern() {
        return new SvgHorizontalKerning();
    }

    /**
     * Create an instance of {@link FeOffset }
     *
     */
    public FeOffset createFeOffset() {
        return new FeOffset();
    }

    /**
     * Create an instance of {@link FeTurbulence }
     *
     */
    public FeTurbulence createFeTurbulence() {
        return new FeTurbulence();
    }

    /**
     * Create an instance of {@link FeFuncG }
     *
     */
    public FeFuncG createFeFuncG() {
        return new FeFuncG();
    }

    /**
     * Create an instance of {@link SvgTextSpan }
     *
     */
    public SvgTextSpan createTspan() {
        return new SvgTextSpan();
    }

    /**
     * Create an instance of {@link SvgMotionPath }
     *
     */
    public SvgMotionPath createMpath() {
        return new SvgMotionPath();
    }

    /**
     * Create an instance of {@link SvgStop }
     *
     */
    public SvgStop createStop() {
        return new SvgStop();
    }

    /**
     * Create an instance of {@link SvgTextReference }
     *
     */
    public SvgTextReference createTref() {
        return new SvgTextReference();
    }

    /**
     * Create an instance of {@link FeFuncR }
     *
     */
    public FeFuncR createFeFuncR() {
        return new FeFuncR();
    }

    /**
     * Create an instance of {@link SvgFontFaceFormat }
     *
     */
    public SvgFontFaceFormat createFontFaceFormat() {
        return new SvgFontFaceFormat();
    }

    /**
     * Create an instance of {@link FeColorMatrix }
     *
     */
    public FeColorMatrix createFeColorMatrix() {
        return new FeColorMatrix();
    }

    /**
     * Create an instance of {@link FeComponentTransfer }
     *
     */
    public FeComponentTransfer createFeComponentTransfer() {
        return new FeComponentTransfer();
    }

    /**
     * Create an instance of {@link FeFuncB }
     *
     */
    public FeFuncB createFeFuncB() {
        return new FeFuncB();
    }

    /**
     * Create an instance of {@link FeFuncA }
     *
     */
    public FeFuncA createFeFuncA() {
        return new FeFuncA();
    }

    /**
     * Create an instance of {@link FeMorphology }
     *
     */
    public FeMorphology createFeMorphology() {
        return new FeMorphology();
    }

    /**
     * Create an instance of {@link FeMerge }
     *
     */
    public FeMerge createFeMerge() {
        return new FeMerge();
    }

    /**
     * Create an instance of {@link FeMergeNode }
     *
     */
    public FeMergeNode createFeMergeNode() {
        return new FeMergeNode();
    }

    /**
     * Create an instance of {@link FePointLight }
     *
     */
    public FePointLight createFePointLight() {
        return new FePointLight();
    }

    /**
     * Create an instance of {@link FeDiffuseLighting }
     *
     */
    public FeDiffuseLighting createFeDiffuseLighting() {
        return new FeDiffuseLighting();
    }

    /**
     * Create an instance of {@link FeDistantLight }
     *
     */
    public FeDistantLight createFeDistantLight() {
        return new FeDistantLight();
    }

    /**
     * Create an instance of {@link FeSpotLight }
     *
     */
    public FeSpotLight createFeSpotLight() {
        return new FeSpotLight();
    }

    /**
     * Create an instance of {@link FeBlend }
     *
     */
    public FeBlend createFeBlend() {
        return new FeBlend();
    }

    /**
     * Create an instance of {@link SvgVerticalKerning }
     *
     */
    public SvgVerticalKerning createVkern() {
        return new SvgVerticalKerning();
    }

    /**
     * Create an instance of {@link FeSpecularLighting }
     *
     */
    public FeSpecularLighting createFeSpecularLighting() {
        return new FeSpecularLighting();
    }

    /**
     * Create an instance of {@link FeTile }
     *
     */
    public FeTile createFeTile() {
        return new FeTile();
    }

    /**
     * Create an instance of {@link SvgFontFaceName }
     *
     */
    public SvgFontFaceName createFontFaceName() {
        return new SvgFontFaceName();
    }

    /**
     * Create an instance of {@link FeDisplacementMap }
     *
     */
    public FeDisplacementMap createFeDisplacementMap() {
        return new FeDisplacementMap();
    }

    /**
     * Create an instance of {@link SvgTextPath }
     *
     */
    public SvgTextPath createTextPath() {
        return new SvgTextPath();
    }

    /**
     * Create an instance of {@link FeFlood }
     *
     */
    public FeFlood createFeFlood() {
        return new FeFlood();
    }

    /**
     * Create an instance of {@link SvgFontFaceUri }
     *
     */
    public SvgFontFaceUri createFontFaceUri() {
        return new SvgFontFaceUri();
    }

    /**
     * Create an instance of {@link SvgFontFaceSrc }
     *
     */
    public SvgFontFaceSrc createFontFaceSrc() {
        return new SvgFontFaceSrc();
    }

    /**
     * Create an instance of {@link FeGaussianBlur }
     *
     */
    public FeGaussianBlur createFeGaussianBlur() {
        return new FeGaussianBlur();
    }

    /**
     * Create an instance of {@link SvgGlyph }
     *
     */
    public SvgGlyph createGlyph() {
        return new SvgGlyph();
    }

    /**
     * Create an instance of {@link SvgMissingGlyph }
     *
     */
    public SvgMissingGlyph createMissingGlyph() {
        return new SvgMissingGlyph();
    }

}
