package tw.com.kyle.infinity;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.OWLParser;
import org.semanticweb.owlapi.io.OWLParserException;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxOntologyParser;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxOntologyParserFactory;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxParserException;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxParserImpl;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

import java.util.stream.Stream;

public class ManchesterParser {

    private BidirectionalShortFormProvider bidiShortFormProvider = null;
    private InfOntology ontology = null;

    public ManchesterParser(InfOntology inf_onto){
        bidiShortFormProvider = inf_onto.GetShortFormProvider();
        ontology = inf_onto;
    }

    private ManchesterOWLSyntaxParser getManchesterParser(
            String manchesterString
    ){
        ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();

        OWLEntityChecker entityChecker = new ShortFormEntityChecker(bidiShortFormProvider);
        parser.setOWLEntityChecker(entityChecker);
        OWLOntology def_onto = ontology.GetOwlOntology();
        if (def_onto != null) {
            parser.setDefaultOntology(def_onto);
        }
        parser.setStringToParse(manchesterString);
        return parser;
    }

    public OWLClassExpression ParseClassExpression(String manchesterString) {
        ManchesterOWLSyntaxParser parser = getManchesterParser(manchesterString);
        OWLClassExpression expr = parser.parseClassExpression();

        System.out.println("ClassExpr: " + expr);
        return expr;
    }

    public String CheckSyntax(String manchesterString) throws Exception {
        ManchesterOWLSyntaxParser parser = getManchesterParser(manchesterString);
        OWLOntology base_onto = ontology.GetOwlOntology();
        OWLOntology onto = null;
        if (base_onto != null) {
            onto = base_onto.getOWLOntologyManager().createOntology();
        } else {
            onto = OWLManager.createOWLOntologyManager().createOntology();
        }

        try {
            parser.parseOntology(onto);
        } catch (OWLParserException ex){
            return ex.getMessage();
        }
        return "";
    }

    public void ParseAxioms(String manchesterString){
        OWLOntology ori_onto = ontology.GetOwlOntology();

        InfinityOWLSyntaxOntologyParser parser = new InfinityOWLSyntaxOntologyParser();
        OWLOntologyDocumentSource ontoSrc = new StringDocumentSource(manchesterString);

        parser.parse(ontoSrc, ori_onto, null, bidiShortFormProvider, new OWLOntologyLoaderConfiguration());
    }

    public void ParseAxiomsToNewOnto(String manchesterString, OWLOntology new_onto){
        OWLOntology ori_onto = ontology.GetOwlOntology();

        InfinityOWLSyntaxOntologyParser parser = new InfinityOWLSyntaxOntologyParser();
        OWLOntologyDocumentSource ontoSrc = new StringDocumentSource(manchesterString);

        parser.parse(ontoSrc, new_onto, ori_onto, bidiShortFormProvider, new OWLOntologyLoaderConfiguration());
    }
}
