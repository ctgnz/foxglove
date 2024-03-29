package nz.co.ctg.foxglove;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.catalog.Catalog;
import javax.xml.catalog.CatalogFeatures;
import javax.xml.catalog.CatalogFeatures.Feature;
import javax.xml.catalog.CatalogManager;
import javax.xml.catalog.CatalogResolver;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextProperties;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class FoxgloveParser {

    private static final Map<String, SvgGraphic> CACHE = Maps.newConcurrentMap();

    public static void cacheItem(String key, SvgGraphic graphic) {
        CACHE.put(key, graphic);
    }

    public static void clearCache() {
        CACHE.clear();
    }

    public static void clearCache(File selectedFile) {
        CACHE.remove(selectedFile.getAbsolutePath());
    }

    public static void clearCache(String selectedResource) {
        CACHE.remove(selectedResource);
    }

    private JAXBContext context;

    private XMLInputFactory xmlInputFactory;

    public FoxgloveParser() {
        try {
            Map<String, Object> properties = Collections.singletonMap(JAXBContextProperties.OXM_METADATA_SOURCE, getBindings());
            context = JAXBContext.newInstance(new Class[] { SvgGraphic.class }, properties);
            xmlInputFactory = XMLInputFactory.newFactory();
            Catalog catalog = CatalogManager.catalog(CatalogFeatures.builder().with(Feature.RESOLVE, "ignore").build(),
                FoxgloveParser.class.getResource("/catalog.xml").toURI());
            CatalogResolver resolver = CatalogManager.catalogResolver(catalog);
            xmlInputFactory.setProperty(XMLInputFactory.RESOLVER, resolver);
            xmlInputFactory.setProperty(XMLInputFactory.IS_VALIDATING, false);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to create JAXB context: " + e.getMessage(), e);
        }
    }

    public SvgGraphic parse(InputStream input) throws Exception {
        Unmarshaller u = context.createUnmarshaller();
        XMLStreamReader xsr = xmlInputFactory.createXMLStreamReader(new StreamSource(input));
        JAXBElement<SvgGraphic> element = u.unmarshal(xsr, SvgGraphic.class);
        return element.getValue();
    }

    public SvgGraphic parseFile(File selectedFile) {
        return CACHE.computeIfAbsent(selectedFile.getAbsolutePath(), key -> {
            try (InputStream in = Files.newInputStream(selectedFile.toPath())) {
                return parse(in);
            } catch (Exception e) {
                return new SvgGraphic();
            }
        });
    }

    public SvgGraphic parseFile(String filePath) {
        return CACHE.computeIfAbsent(filePath, key -> {
            try (InputStream in = FoxgloveParser.class.getResourceAsStream(filePath)) {
                return parse(in);
            } catch (Exception e) {
                return new SvgGraphic();
            }
        });
    }

    public SvgGraphic parseResource(String resourcePath, InputStream inputStream) {
        return CACHE.computeIfAbsent(resourcePath, key -> {
            try {
                return parse(inputStream);
            } catch (Exception e) {
                return new SvgGraphic();
            }
        });
    }

    public String write(SvgGraphic svg, Boolean prettyPrint) throws Exception {
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, prettyPrint);
        StringWriter out = new StringWriter();
        m.marshal(svg, out);
        return out.toString();
    }

    public void writeFile(Path directoryPath, String fileName, SvgGraphic graphic) throws Exception {
        Path filePath = directoryPath.resolve(fileName);
        String output = write(graphic, true);
        Files.write(filePath, output.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private List<InputStream> getBindings() {
        return ImmutableList.<InputStream> builder()
            .add(getClass().getResourceAsStream("/bindings/foxglove.xml"))
            .add(getClass().getResourceAsStream("/bindings/foxglove-description.xml"))
            .add(getClass().getResourceAsStream("/bindings/foxglove-animate.xml"))
            .add(getClass().getResourceAsStream("/bindings/foxglove-filter.xml"))
            .add(getClass().getResourceAsStream("/bindings/foxglove-element.xml"))
            .add(getClass().getResourceAsStream("/bindings/foxglove-clip.xml"))
            .add(getClass().getResourceAsStream("/bindings/foxglove-paint.xml"))
            .add(getClass().getResourceAsStream("/bindings/foxglove-shape.xml"))
            .add(getClass().getResourceAsStream("/bindings/foxglove-text.xml"))
            .add(getClass().getResourceAsStream("/bindings/javafx-css.xml"))
            .add(getClass().getResourceAsStream("/bindings/javafx-paint.xml"))
            .add(getClass().getResourceAsStream("/bindings/javafx-shape.xml"))
            .add(getClass().getResourceAsStream("/bindings/javafx-text.xml"))
            .build();
    }

}
