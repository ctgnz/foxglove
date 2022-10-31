package nz.co.ctg.foxglove.attributes;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.MoreObjects.ToStringHelper;

import nz.co.ctg.foxglove.parser.SvgTransformListHandler;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.scene.transform.Transform;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class SvgTransformAttribute {
    private String transform;
    private static SvgTransformListHandler adapter = new SvgTransformListHandler();

    public SvgTransformAttribute() {
    }

    @XmlAttribute(name = "transform")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public String getTransform() {
        return transform;
    }

    public void setTransform(String value) {
        this.transform = value;
    }

    public List<Transform> getTransformList() {
        if (StringUtils.isBlank(getTransform())) {
            return Collections.emptyList();
        }
        return adapter.parse(getTransform());
    }

    public void toStringDetail(ToStringHelper builder) {
        builder.add("transform", transform);
    }

}
