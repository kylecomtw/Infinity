package tw.com.kyle.infinity;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InfOntologyTest {
    @Test
    void dummyTest() {
        assertEquals(0, 0);
    }

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
        // System.out.println(inf_onto.toManchester());

        InfDLQuery query = new InfDLQuery(inf_onto);
        int nsuper = query.Query("Student").superClasses.size();
        assertEquals(2, nsuper);

    }

    @Test
    void infReasonerImport2() throws Exception {
        InfOntology inf_onto = new InfOntology();
        String base_iri = "http://example.com/ontology/person";

        URL onto_loc = getClass().getClassLoader().getResource("xmen.owl");
        if (Files.exists(Paths.get(onto_loc.toURI()))) {
            inf_onto.ImportOntology(onto_loc.toString());
        }

        OWLOntology base_onto = inf_onto.ImportOntologyFromString("" +
                "Prefix: : <http://example.com/>\n" +
                "Prefix: o: <http://seantyh.idv.tw/ontoLex/practice/2017/7/untitled-ontology-20#>\n" +
                "Ontology: <" + base_iri + ">\n" +
                "Import: <http://seantyh.idv.tw/ontoLex/practice/2017/7/untitled-ontology-20>\n" +
                "Class: Person\n" +
                "Class: o:XMen\n" +
                "  SubClassOf: Person\n");

        inf_onto.SetDefaultIRI(IRI.create(base_iri));
        // System.out.println(inf_onto.toManchester());

        String in_manchester = "" +
                "Prefix: : <http://example.com/> \n" +
                "Prefix: o: <http://seantyh.idv.tw/ontoLex/practice/2017/7/untitled-ontology-20#>" +
                "Ontology: <http://temp.add/1>\n" +
                "Import: <" + base_iri + ">\n" +
                "Class: Wolverine\n" +
                "  SubClassOf: o:XMen\n\n" +
                "Class: Ghost\n";
        ManchesterParser parser = new ManchesterParser(inf_onto);
        String result = parser.CheckSyntax(in_manchester);
        if(result.length() > 0){
            System.out.println(result);
        } else {
            inf_onto.AddAxioms(in_manchester);
        }
        // System.out.println(inf_onto.toManchester());

        InfDLQuery query = new InfDLQuery(inf_onto);
        List<String> supers = query.Query("Wolverine").superClasses;
        // System.out.println(supers);
        int nsuper = supers.size();
        assertEquals(3, nsuper);

    }

    @Test
    public void testListIRI() {
        try {
            InfOntology inf_onto = new InfOntology();
            inf_onto.ImportOntologyFromString("" +
                    "Prefix: : <http://example.com/>\n" +
                    "Ontology: <http://example.com/1>\n" +
                    "Class: A\n");
            inf_onto.ImportOntologyFromString("" +
                    "Prefix: : <http://example.com/>\n" +
                    "Ontology: <http://example.com/2>\n" +
                    "Class: B\n");
            List<String> onto_list = inf_onto.ListOntologyIRIs();
            assertEquals(2, onto_list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    void infOntologyUpdate() throws Exception {
        InfOntology inf_onto = new InfOntology();
        String base_iri = "http://example.com/ontology/person";

        inf_onto.ImportOntologyFromString("" +
                "Prefix: : <http://example.com/>\n" +
                "Ontology: <" + base_iri + ">\n" +
                "Class: Person\n");

        inf_onto.ImportOntologyFromString("" +
                "Prefix: : <http://example.com/>\n" +
                "Ontology: <" + base_iri + ">\n" +
                "Class: Person\n" +
                "Class: Animal\n");

        assertEquals(5, inf_onto.GetOwlOntology(IRI.create(base_iri)).getAxiomCount());
        // inf_onto.ParseExpression("Human and not Parent");
    }
}