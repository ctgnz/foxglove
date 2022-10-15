package nz.co.ctg.jmsfx.svg;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.junit.BeforeClass;
import org.junit.Test;

import nz.co.ctg.jmsfx.svg.document.SvgGroup;
import nz.co.ctg.jmsfx.svg.document.SvgRootElement;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class SvgParseTest {

    @BeforeClass
    public static void prep() {
        System.setProperty("javax.xml.accessExternalDTD", "all");
    }

    @Test
    public void testParse() throws Exception {
        JAXBContext context = JAXBContext.newInstance(SvgRootElement.class);
        Unmarshaller u = context.createUnmarshaller();
        JAXBElement<SvgRootElement> element = u.unmarshal(new StreamSource(SvgRootElement.class.getResourceAsStream("/test.svg")), SvgRootElement.class);
        assertThat(element.getValue(), notNullValue());
        element.getValue().getElements().forEach(el -> {
            printElement(el);
        });
    }

    private void printElement(Object el) {
        System.out.println(el);
        if (el instanceof SvgGroup) {
            ((SvgGroup) el).getElements().forEach(this::printElement);
        }
    }

    @Test
    public void testWrite() throws Exception {
        JAXBContext context = JAXBContext.newInstance(SvgRootElement.class);
        Marshaller m = context.createMarshaller();
        StringWriter out = new StringWriter();
        SvgRootElement svgRootElement = new SvgRootElement();
        svgRootElement.getElements().add(new SvgGroup());
        m.marshal(svgRootElement, out);
        System.out.println(out);
    }

}
