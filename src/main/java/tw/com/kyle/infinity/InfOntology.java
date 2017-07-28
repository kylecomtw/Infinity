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
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.OWLParser;
import org.semanticweb.owlapi.io.StringDocumentSource;
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
    private BidirectionalShortFormProvider bidiShortFormProvider = null;

    public InfOntology() throws Exception {
        manager = OWLManager.createOWLOntologyManager();
        ontology = manager.createOntology();
        Stream<OWLOntology> importsClosure = ontology.imports();
        ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
        bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(manager,
                importsClosure.collect(Collectors.toList()), shortFormProvider);
    }

    public OWLOntology GetOwlOntology() {
        return ontology;
    }

    public BidirectionalShortFormProvider GetShortFormProvider() {
        return bidiShortFormProvider;
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
        parser.setDefaultOntology(ontology);
        parser.setStringToParse(manchesterString);
        return parser;
    }

    public void LoadOWL(String manchesterString){
        OWLParser parser = new ManchesterOWLSyntaxOntologyParserFactory().createParser();
        OWLOntologyDocumentSource src = new StringDocumentSource(manchesterString);
        OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
        parser.parse(src, ontology, config);

        System.out.println(ontology.toString());
    }

}