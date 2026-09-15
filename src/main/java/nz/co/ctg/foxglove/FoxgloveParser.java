package nz.co.ctg.foxglove;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
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
            context = JAXBContext.newInstance(new Class<?>[] {
                SvgGraphic.class
            }, properties);
            xmlInputFactory = XMLInputFactory.newFactory();
            Catalog catalog = CatalogManager.catalog(CatalogFeatures.builder()
                .with(Feature.RESOLVE, "ignore")
                .build(),
                FoxgloveParser.class.getResource("/catalog.xml")
                    .toURI());
            CatalogResolver resolver = CatalogManager.catalogResolver(catalog);
            xmlInputFactory.setProperty(XMLInputFactory.RESOLVER, resolver);
            xmlInputFactory.setProperty(XMLInputFactory.IS_VALIDATING, false);
            // The real SVG 1.1 DTD's own %SVG.Presentation.attrib parameter entity is ~15041 characters - one
            // character over the JDK's own jdk.xml.maxParameterEntitySizeLimit default of 15000 (a security
            // hardening limit against XML entity-expansion attacks). Raised here, scoped to this factory instance
            // only (not a JVM-wide system property, which would affect every other XML processor an embedding
            // application uses) - to a generous bound rather than disabled outright (0/unlimited), so this still
            // offers some protection against a genuinely malicious, unbounded entity.
            xmlInputFactory.setProperty("jdk.xml.maxParameterEntitySizeLimit", 100_000);
            // A document declaring a DOCTYPE with an external SYSTEM/PUBLIC identifier must never cause this parser
            // to make an outbound network request while parsing (#158) - regardless of what host it names, and
            // regardless of whether the catalog above happens to already redirect it to a local resource. Catalog
            // resolution is unaffected by this: once the resolver rewrites a systemId to a classpath/local URI, the
            // resulting fetch is local, not "external" in the sense this property governs - confirmed empirically,
            // not merely assumed, that a Full-profile document (whose DTD is fully catalog-resolved locally) still
            // parses correctly with this disabled, while a document whose reference the catalog cannot redirect at
            // all no longer silently reaches the network for it. Internally-declared general entities (a document's
            // own inline <!ENTITY Name "..."> declarations, used throughout the W3C suite) are unaffected either
            // way - this property concerns only entities whose value comes from another resource, not inline text.
            xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
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
                SvgGraphic graphic = parse(in);
                graphic.setBaseUri(selectedFile.toURI());
                return graphic;
            } catch (Exception e) {
                return new SvgGraphic();
            }
        });
    }

    /**
     * Loads and parses the document at {@code uri}, caching by the URI's own string form the same way {@link #parseFile(File)} caches by absolute path - a repeat resolution of the
     * same external document (sibling {@code <use>}s, or the same document reached via more than one reference) reuses the cached {@link SvgGraphic}, giving its elements stable
     * identity across every caller. Used by {@link SvgElementIndex} to load a document named by an {@code xlink:href="other.svg#id"} reference.
     */
    public SvgGraphic parseFile(URI uri) {
        return CACHE.computeIfAbsent(uri.toString(), key -> {
            try (InputStream in = uri.toURL()
                .openStream()) {
                SvgGraphic graphic = parse(in);
                graphic.setBaseUri(uri);
                return graphic;
            } catch (Exception e) {
                return new SvgGraphic();
            }
        });
    }

    public SvgGraphic parseFile(String filePath) {
        return CACHE.computeIfAbsent(filePath, key -> {
            try (InputStream in = FoxgloveParser.class.getResourceAsStream(filePath)) {
                SvgGraphic graphic = parse(in);
                URL resource = FoxgloveParser.class.getResource(filePath);
                if (resource != null) {
                    graphic.setBaseUri(resource.toURI());
                }
                return graphic;
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
            .add(getClass().getResourceAsStream("/bindings/foxglove-type.xml"))
            .add(getClass().getResourceAsStream("/bindings/javafx-css.xml"))
            .add(getClass().getResourceAsStream("/bindings/javafx-shape.xml"))
            .add(getClass().getResourceAsStream("/bindings/javafx-text.xml"))
            .build();
    }

}
