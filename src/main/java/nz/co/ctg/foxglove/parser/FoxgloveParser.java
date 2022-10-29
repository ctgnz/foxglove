package nz.co.ctg.foxglove.parser;

import java.io.InputStream;
import java.io.StringWriter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import nz.co.ctg.foxglove.SvgGraphic;

public class FoxgloveParser {

    private JAXBContext context;
    private XMLInputFactory xmlInputFactory;

    public FoxgloveParser() throws JAXBException {
        context = JAXBContext.newInstance(SvgGraphic.class);
        xmlInputFactory = XMLInputFactory.newFactory();
        xmlInputFactory.setProperty(XMLInputFactory.IS_VALIDATING, false);
        xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
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
