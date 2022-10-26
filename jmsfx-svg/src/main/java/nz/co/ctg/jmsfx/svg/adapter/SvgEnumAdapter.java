package nz.co.ctg.jmsfx.svg.adapter;

import java.lang.reflect.Method;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public abstract class SvgEnumAdapter<E extends Enum<?>> extends XmlAdapter<String, E> {

    private Class<E> enumType;
    private E defaultValue;

    public SvgEnumAdapter(Class<E> enumType, E defaultValue) {
        this.enumType = enumType;
        this.defaultValue = defaultValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E unmarshal(String value) throws Exception {
        try {
            Method method = enumType.getMethod("valueOf", String.class);
            return (E) method.invoke(enumType, value.toUpperCase());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public String marshal(E value) throws Exception {
        return value.name().toLowerCase();
    }

}
