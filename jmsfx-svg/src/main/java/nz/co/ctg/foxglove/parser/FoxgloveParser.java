package nz.co.ctg.foxglove.parser;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
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

}
