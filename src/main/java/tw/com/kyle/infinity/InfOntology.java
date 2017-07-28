package tw.com.kyle.infinity;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.OntologyAxiomPair;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

class InfOntology {
    private OWLOntology ontology = null;
    private OWLOntologyManager manager = null;
    private BidirectionalShortFormProvider bidiShortFormProvider = null;

    public InfOntology() throws Exception {
        manager = OWLManager.createOWLOntologyManager();
        ontology = manager.createOntology();
        Stream<OWLOntology> importsClosure = ontology.imports();
        ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
        bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(manager,
                importsClosure.collect(Collectors.toList()), shortFormProvider);
    }

    public void ImportFile(String ontoPath) throws Exception {
        if (!Files.exists(Paths.get(ontoPath))) {
            throw new FileNotFoundException("Cannot find " + ontoPath);
        }
        File ontoFile = new File(ontoPath);
        throw new UnsupportedOperationException();

    }

    private ManchesterOWLSyntaxParser getManchesterParser(
        String manchesterString
    ){
        ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();
        OWLEntityChecker entityChecker = new ShortFormEntityChecker(bidiShortFormProvider);

        parser.setOWLEntityChecker(entityChecker);
        parser.setStringToParse(manchesterString);
        parser.setDefaultOntology(ontology);
        return parser;
    }

    public void ParseAxiom(String manchesterString) {
        ManchesterOWLSyntaxParser parser = getManchesterParser(manchesterString);
        Set<OntologyAxiomPair> axiom = parser.parseClassFrame();
        System.out.println(axiom);
    }

    public void ParseExpression(String manchesterString) {
        ManchesterOWLSyntaxParser parser = getManchesterParser(manchesterString);  
        OWLClassExpression expr = parser.parseClassExpression();
        System.out.println(expr);
    }
}