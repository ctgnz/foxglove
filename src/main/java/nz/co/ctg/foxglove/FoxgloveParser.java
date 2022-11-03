package nz.co.ctg.foxglove;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

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

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class FoxgloveParser {

    private JAXBContext context;
    private XMLInputFactory xmlInputFactory;

    public FoxgloveParser() throws Exception {
        context = JAXBContext.newInstance(new Class[] {SvgGraphic.class}, Collections.singletonMap(JAXBContextProperties.OXM_METADATA_SOURCE, getBindings()));
        xmlInputFactory = XMLInputFactory.newFactory();
        Catalog catalog = CatalogManager.catalog(CatalogFeatures.builder().with(Feature.RESOLVE, "ignore").build(), FoxgloveParser.class.getResource("/catalog.xml").toURI());
        CatalogResolver resolver = CatalogManager.catalogResolver(catalog);
        xmlInputFactory.setProperty(XMLInputFactory.RESOLVER, resolver);
        xmlInputFactory.setProperty(XMLInputFactory.IS_VALIDATING, false);
    }

    public SvgGraphic parse(InputStream input) throws Exception {
        Unmarshaller u = context.createUnmarshaller();
        XMLStreamReader xsr = xmlInputFactory.createXMLStreamReader(new StreamSource(input));
        JAXBElement<SvgGraphic> element = u.unmarshal(xsr, SvgGraphic.class);
        return element.getValue();
    }

    public String write(SvgGraphic svg) throws Exception {
        Marshaller m = context.createMarshaller();
        StringWriter out = new StringWriter();
        m.marshal(svg, out);
        return out.toString();
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
            .add(getClass().getResourceAsStream("/bindings/javafx-shape.xml"))
            .build();
    }

}
