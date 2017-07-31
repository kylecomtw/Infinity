package tw.com.kyle.infinity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.formats.ManchesterSyntaxDocumentFormat;
import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxOntologyParserFactory;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.OntologyAxiomPair;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

class InfOntology {
    private OWLOntology ontology = null;
    private OWLOntologyManager manager = null;

    public InfOntology() throws Exception {
        manager = OWLManager.createOWLOntologyManager();
        ontology = manager.createOntology();

    }

    public OWLOntology GetOwlOntology() {
        return ontology;
    }

    public BidirectionalShortFormProvider GetShortFormProvider() {
        Stream<OWLOntology> importsClosure = ontology.imports();
        ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
        return new BidirectionalShortFormProviderAdapter(
                Arrays.asList(ontology), shortFormProvider);
    }

    public void ImportFile(String ontoPath) throws Exception {
        if (!Files.exists(Paths.get(ontoPath))) {
            throw new FileNotFoundException("Cannot find " + ontoPath);
        }
        OWLOntologyDocumentSource ontoSrc = new FileDocumentSource(new File(ontoPath));
        OWLParser parser = new ManchesterOWLSyntaxOntologyParserFactory().createParser();
        OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
        parser.parse(ontoSrc, ontology, config);

        // in_onto.axioms().forEach((axiom)-> ontology.addAxiom(axiom));
    }

    public void AddAxioms(String manchesterString) throws Exception {
        ManchesterParser parser = new ManchesterParser(this);
        parser.ParseAxioms(manchesterString);

        return;
    }

    public void RemoveAxioms(String manchesterString) throws Exception {
        ManchesterParser parser = new ManchesterParser(this);
        OWLOntology new_onto = OWLManager.createOWLOntologyManager().createOntology();
        parser.ParseAxiomsToNewOnto(manchesterString, new_onto);

        new_onto.axioms().forEach((axiom) -> ontology.removeAxiom(axiom));
        return;
    }

    public void LoadManchester(String manchesterString){
        OWLParser parser = new ManchesterOWLSyntaxOntologyParserFactory().createParser();
        OWLOntologyDocumentSource src = new StringDocumentSource(manchesterString);
        OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
        parser.parse(src, ontology, config);

        System.out.println(ontology.toString());
    }

    public String toManchester(){
        OWLOntologyDocumentTarget out = new StringDocumentTarget();
        OWLDocumentFormat mformat = new ManchesterSyntaxDocumentFormat();
        try {
            ontology.saveOntology(mformat, out);
            return out.toString();
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
            return "";
        }
    }

}