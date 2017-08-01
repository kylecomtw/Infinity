package tw.com.kyle.infinity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ManchesterParserTest {
    @Test
    public void testManchesterParser() throws Exception {
        InfOntology onto = new InfOntology();
        ManchesterParser parser = new ManchesterParser(onto);
        String results = parser.CheckSyntax("" +
            "Prefix: : <http://example.com/>\n" +
            "Class: BaseClass\n");
        assertEquals(0, results.length());
    }

    @Test
    public void testInvalidManchester() throws Exception {
        InfOntology onto = new InfOntology();
        ManchesterParser parser = new ManchesterParser(onto);
        String results = parser.CheckSyntax("" +
                "Prefix: : <http://example.com/>\n" +
                "Class: BaseClass\n" +
                "  subclassof: ParentClass\n");
        assertEquals(true, results.length() > 0);

    }
}
