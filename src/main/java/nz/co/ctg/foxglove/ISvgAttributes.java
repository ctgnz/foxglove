package nz.co.ctg.foxglove;

import java.util.Map;

public interface ISvgAttributes {

    <T> T get(String property);

    void set(String property, Object value);

    Map<String, Object> getProperties();

}
