package tw.com.kyle.infinity;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class InfOntologyTests {
    @Test
    void dummyTest() {
        assertEquals(0, 0);
    }

    @Disabled
    @Test
    void infOntologyImport() throws Exception {
        InfOntology inf_onto = new InfOntology();
        String base_iri = "http://example.com/ontology/person";

        inf_onto.ImportOntologyFromString("" +
                "Prefix: : <http://example.com/>\n" +
                "Ontology: <" + base_iri + ">\n" +
                "Class: Person\n");

        inf_onto.AddAxioms("" +
                "Prefix: : <http://example.com/> \n" +
                "Ontology: <http://temp.add/1>\n" +
                "Import: <" + base_iri + ">\n" +
                "Class: Student\n" +
                "  SubClassOf: :Person\n\n" +
                "Class: Ghost\n");

        assertEquals(4, inf_onto.GetOwlOntology(IRI.create(base_iri)).getAxiomCount());
        // inf_onto.ParseExpression("Human and not Parent");
    }

    @Disabled
    @Test
    void infOntologyRemove() throws Exception {
        InfOntology inf_onto = new InfOntology();
        String base_iri = "http://example.com/ontology/person";

        inf_onto.ImportOntologyFromString("" +
                "Prefix: : <http://example.com/>\n" +
                "Ontology: <" + base_iri + ">\n" +
                "Class: Person\n");

        inf_onto.AddAxioms("" +
                "Prefix: : <http://example.com/> \n" +
                "Ontology: <http://temp.add/1>\n" +
                "Import: <" + base_iri + ">\n" +
                "Class: Student\n" +
                "  SubClassOf: :Person\n\n" +
                "Class: Ghost\n");
        inf_onto.RemoveAxioms("Prefix: : <http://example.com/> \n" +
                "Import: <" + base_iri + ">\n" +
                "Class: :Ghost\n");

        assertEquals(3, inf_onto.GetOwlOntology(IRI.create(base_iri)).getAxiomCount());
        // inf_onto.ParseExpression("Human and not Parent");
    }

    @Disabled
    @Test
    void infOntology() throws Exception {
        InfOntology inf_onto = new InfOntology();
        URL onto_loc = getClass().getClassLoader().getResource("xmen.owl");
        if (Files.exists(Paths.get(onto_loc.toURI()))){
            System.out.println(onto_loc);
            inf_onto.ImportOntology(onto_loc.toString());

            assertEquals(1, inf_onto.AxiomCount());
        } else{
            fail("Cannot find " + onto_loc);
        }

    }

    @Test
    void infReasoner() throws Exception {
        InfOntology inf_onto = new InfOntology();
        String base_iri = "http://example.com/ontology/person";

        OWLOntology base_onto = inf_onto.ImportOntologyFromString("" +
                "Prefix: : <http://example.com/>\n" +
                "Ontology: <" + base_iri + ">\n" +
                "Class: Person\n");
        inf_onto.SetDefaultIRI(IRI.create(base_iri));

        inf_onto.AddAxioms("" +
                "Prefix: : <http://example.com/> \n" +
                "Ontology: <http://temp.add/1>\n" +
                "Import: <" + base_iri + ">\n" +
                "Class: Student\n" +
                "  SubClassOf: :Person\n\n" +
                "Class: Ghost\n");

        InfDLQuery query = new InfDLQuery(inf_onto);
        int nsuper = query.Query("Student").superClasses.size();
        assertEquals(nsuper, 1);

    }

}