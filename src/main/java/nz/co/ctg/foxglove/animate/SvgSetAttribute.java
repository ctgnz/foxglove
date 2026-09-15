package nz.co.ctg.foxglove.animate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.RenderContext;
import nz.co.ctg.foxglove.animate.SvgAnimationTiming.FillBehavior;
import nz.co.ctg.foxglove.description.ISvgDescriptiveElement;
import nz.co.ctg.foxglove.description.SvgDescription;
import nz.co.ctg.foxglove.description.SvgMetadata;
import nz.co.ctg.foxglove.description.SvgTitle;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "set", propOrder = {
    "contents"
})
@XmlRootElement(name = "set")
public class SvgSetAttribute extends AbstractSvgAnimationElement {

    @XmlAttribute(name = "attributeName", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String attributeName;

    @XmlAttribute(name = "attributeType")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String attributeType;

    @XmlElements({
        @XmlElement(name = "desc", type = SvgDescription.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "title", type = SvgTitle.class, namespace = "http://www.w3.org/2000/svg"),
        @XmlElement(name = "metadata", type = SvgMetadata.class, namespace = "http://www.w3.org/2000/svg")
    })
    private List<ISvgDescriptiveElement> contents;

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String value) {
        this.attributeName = value;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String value) {
        this.attributeType = value;
    }

    public List<ISvgDescriptiveElement> getContents() {
        if (contents == null) {
            contents = new ArrayList<>();
        }
        return this.contents;
    }

    /**
     * {@code <set>} has no interpolation at all - it just writes {@code to} at the start of its active duration and either holds it ({@code fill="freeze"}, a JavaFX
     * {@code Animation}'s own default end-of-run behaviour, so nothing extra is needed) or reverts to whatever the target property held before this animation touched it
     * ({@code fill="remove"}, a second {@link Interpolator#DISCRETE} {@link KeyFrame} at the end of {@code dur} writing the pre-animation value captured up front). No value list,
     * {@code calcMode}, or {@code additive}/ {@code accumulate} - none of those attributes exist on this element - so this needs none of {@link SvgValueAnimationBuilder}'s
     * machinery.
     */
    @Override
    public Optional<Animation> buildAnimation(Node target, RenderContext context) {
        return SvgAttributeRegistry.resolve(target, getAttributeName())
            .flatMap(this::buildAnimation);
    }

    @SuppressWarnings("unchecked")
    private Optional<Animation> buildAnimation(SvgAttributeBinding<?> rawBinding) {
        SvgAttributeBinding<Object> binding = (SvgAttributeBinding<Object>) rawBinding;
        String to = StringUtils.trimToNull(getTo());
        if (to == null) {
            return Optional.empty();
        }
        Optional<Object> toValue = binding.parser()
            .apply(to);
        if (toValue.isEmpty()) {
            return Optional.empty();
        }
        Object baseValue = binding.property()
            .getValue();
        SvgAnimationTiming timing = SvgAnimationTiming.parse(this);

        List<KeyFrame> frames = new ArrayList<>();
        frames.add(new KeyFrame(Duration.ZERO, new KeyValue(binding.property(), toValue.get(), Interpolator.DISCRETE)));
        if (timing.fill() == FillBehavior.REMOVE) {
            frames.add(new KeyFrame(timing.duration(), new KeyValue(binding.property(), baseValue, Interpolator.DISCRETE)));
        }
        return Optional.of(new Timeline(frames.toArray(new KeyFrame[0])));
    }

    @Override
    public void toStringDetail(ToStringHelper builder) {
        builder.add("attributeName", attributeName);
        builder.add("attributeType", attributeType);
        super.toStringDetail(builder);
    }

}
