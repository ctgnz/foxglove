package nz.co.ctg.jmsfx.svg;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

public class FXVGParser {

    private JAXBContext context;
    private XMLInputFactory xmlInputFactory;

    public FXVGParser() throws JAXBException {
        context = JAXBContext.newInstance(FXVGSvgElement.class);
        xmlInputFactory = XMLInputFactory.newFactory();
        xmlInputFactory.setProperty(XMLInputFactory.IS_VALIDATING, false);
        xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    }

    public FXVGSvgElement parse(InputStream input) throws Exception {
        Unmarshaller u = context.createUnmarshaller();
        XMLStreamReader xsr = xmlInputFactory.createXMLStreamReader(new StreamSource(input));
        JAXBElement<FXVGSvgElement> element = u.unmarshal(xsr, FXVGSvgElement.class);
        return element.getValue();
    }

}
