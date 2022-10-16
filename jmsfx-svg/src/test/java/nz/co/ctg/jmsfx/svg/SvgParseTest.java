package nz.co.ctg.jmsfx.svg;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.junit.BeforeClass;
import org.junit.Test;

import nz.co.ctg.jmsfx.svg.document.FXVGGroup;
import nz.co.ctg.jmsfx.svg.document.FXVGRootElement;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class SvgParseTest {

    @BeforeClass
    public static void prep() {
        System.setProperty("javax.xml.accessExternalDTD", "all");
    }

    @Test
    public void testParse() throws Exception {
        JAXBContext context = JAXBContext.newInstance(FXVGRootElement.class);
        Unmarshaller u = context.createUnmarshaller();
        JAXBElement<FXVGRootElement> element = u.unmarshal(new StreamSource(FXVGRootElement.class.getResourceAsStream("/test.svg")), FXVGRootElement.class);
        assertThat(element.getValue(), notNullValue());
        element.getValue().getElements().forEach(el -> {
            printElement(el);
        });
    }

    private void printElement(Object el) {
        System.out.println(el);
        if (el instanceof FXVGGroup) {
            ((FXVGGroup) el).getElements().forEach(this::printElement);
        }
    }

    @Test
    public void testWrite() throws Exception {
        JAXBContext context = JAXBContext.newInstance(FXVGRootElement.class);
        Marshaller m = context.createMarshaller();
        StringWriter out = new StringWriter();
        FXVGRootElement fXVGRootElement = new FXVGRootElement();
        fXVGRootElement.getElements().add(new FXVGGroup());
        m.marshal(fXVGRootElement, out);
        System.out.println(out);
    }

}
