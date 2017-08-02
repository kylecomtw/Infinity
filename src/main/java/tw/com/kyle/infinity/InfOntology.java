package tw.com.kyle.infinity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
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
    private IRI default_iri = null;
    private OWLOntology ontology = null;
    private OWLOntologyManager manager = null;

    public InfOntology() throws Exception {
        String timestamp = new SimpleDateFormat("YYMMddHHmmss").format(new Date());
        String namespace = "http://tw.com.kyle.infinity/ontology/";
        IRI iri = IRI.create(namespace, "inf-" + timestamp);
        init(iri);
    }

    public InfOntology(String iri_str) throws Exception {
        IRI iri = IRI.create(iri_str);
        init(iri);
    }

    private void init(IRI iri) throws Exception {
        default_iri = iri;
        manager = OWLManager.createOWLOntologyManager();
    }

    public boolean SetDefaultIRI(IRI iri){
        if (manager.contains(iri)) {
            default_iri = iri;
            return true;
        } else {
            return false;
        }
    }

    private void setDefaultOntology(OWLOntology onto){
        OWLOntologyID oid = onto.getOntologyID();
        if(oid.getOntologyIRI().isPresent()){
            SetDefaultIRI(oid.getOntologyIRI().get());
        }
    }

    public boolean RemoveIfExists(IRI iri){
        if (manager.contains(iri)){
            OWLOntology onto = manager.getOntology(iri);
            manager.removeOntology(onto);
            return true;
        } else {
            return false;
        }
    }

    public OWLOntology GetOwlOntology() {
        return manager.getOntology(default_iri);
    }

    public OWLOntology GetOwlOntology(IRI iri) {
        if (manager.contains(iri)) {
            return manager.getOntology(iri);
        } else {
            System.out.println("WARNING: Cannot find " + iri.toString());
            return null;
        }
    }

    public int AxiomCount(){
        return manager.ontologies().mapToInt((x)->x.getAxiomCount()).sum();
    }

    public BidirectionalShortFormProvider GetShortFormProvider(IRI iri) {
        OWLOntology onto = GetOwlOntology(iri);
        Set<OWLOntology> importsClosureSet = new HashSet<>();
        if (onto != null) {
            Stream<OWLOntology> importsClosure = onto.imports();
            importsClosureSet = importsClosure.collect(Collectors.toSet());
            importsClosureSet.add(GetOwlOntology());
        }

        ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
        return new BidirectionalShortFormProviderAdapter(manager,
                importsClosureSet,
                shortFormProvider);
    }

    public BidirectionalShortFormProvider GetShortFormProvider() {
        return GetShortFormProvider(default_iri);
    }

    public List<String> ListOntologyIRIs(){
        return manager.ontologies().map((x)->{
            OWLOntologyID oid = x.getOntologyID();
            if (oid.getDefaultDocumentIRI().isPresent()){
                return oid.getDefaultDocumentIRI().get().toString();
            } else {
                return oid.toString();
            }
        }).collect(Collectors.toList());
    }

    public OWLOntology ImportOntology(String ontoLocation) throws Exception {
        IRI iri = IRI.create(ontoLocation);
        OWLOntology in_onto = manager.loadOntology(iri);
        setDefaultOntology(in_onto);
        return in_onto;
    }

    public OWLOntology ImportOntologyFromString(String ontoString) throws Exception {
        OWLOntologyDocumentSource ontoSrc = new StringDocumentSource(ontoString);
        OWLOntology in_onto = manager.loadOntologyFromOntologyDocument(ontoSrc);

        setDefaultOntology(in_onto);
        return in_onto;
    }

    public void AddAxioms(String ontoString) throws Exception {
        // ManchesterParser parser = new ManchesterParser(this);
        // parser.ParseAxioms(manchesterString);
        OWLOntologyDocumentSource ontoSrc = new StringDocumentSource(ontoString);
        OWLOntology new_onto = manager.loadOntologyFromOntologyDocument(ontoSrc);

        Optional<OWLOntology> maybe_base_onto = new_onto.directImports().findFirst();
        if (maybe_base_onto.isPresent()){
            manager.addAxioms(maybe_base_onto.get(), new_onto.axioms());
        }

        manager.removeOntology(new_onto);

        return;
    }

    public void RemoveAxioms(String ontoString) throws Exception {
        OWLOntologyDocumentSource ontoSrc = new StringDocumentSource(ontoString);
        OWLOntology new_onto = manager.loadOntologyFromOntologyDocument(ontoSrc);

        Optional<OWLOntology> maybe_base_onto = new_onto.directImports().findFirst();
        if (maybe_base_onto.isPresent()){
            manager.removeAxioms(maybe_base_onto.get(), new_onto.axioms());
        }

        manager.removeOntology(new_onto);
        return;
    }

    public String toManchester() {
        return toManchester(default_iri);
    }

    public String toManchester(IRI iri){
        OWLOntologyDocumentTarget out = new StringDocumentTarget();
        OWLDocumentFormat mformat = new ManchesterSyntaxDocumentFormat();
        try {
            OWLOntology ontology = GetOwlOntology(iri);
            ontology.saveOntology(mformat, out);
            return out.toString();
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
            return "";
        }
    }

}