package nz.co.ctg.foxglove.parser;

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.catalog.Catalog;
import javax.xml.catalog.CatalogFeatures;
import javax.xml.catalog.CatalogFeatures.Feature;
import javax.xml.catalog.CatalogManager;
import javax.xml.catalog.CatalogResolver;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import nz.co.ctg.foxglove.SvgGraphic;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class FoxgloveParser {

    private JAXBContext context;
    private XMLInputFactory xmlInputFactory;

    public FoxgloveParser() throws Exception {
        context = JAXBContext.newInstance(SvgGraphic.class);
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

}
