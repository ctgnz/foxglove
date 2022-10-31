package nz.co.ctg.foxglove.parser;

import org.apache.commons.lang3.StringUtils;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;

public abstract class SvgEnumAdapter<E extends Enum<E>> extends XmlAdapter<String, E> {

    private static class ParsedValueImpl<E extends Enum<E>> extends ParsedValue<String, E> {

        protected ParsedValueImpl(String value, StyleConverter<String, E> converter) {
            super(value, converter);
        }

    }

    private E defaultValue;
    private StyleConverter<String, E> converter;

    public SvgEnumAdapter(Class<E> enumType, E defaultValue) {
        this.defaultValue = defaultValue;
        this.converter = StyleConverter.getEnumConverter(enumType);
    }

    @Override
    public E unmarshal(String value) throws Exception {
        if (StringUtils.isBlank(value) || "none".equals(value)) {
            return null;
        }
        try {
            return converter.convert(new ParsedValueImpl<>(value, converter), null);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public String marshal(E value) throws Exception {
        return value != null ? value.name().toLowerCase() : null;
    }

}
