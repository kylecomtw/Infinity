package tw.com.kyle.infinity;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

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
        parser.setDefaultOntology(ontology.GetOwlOntology());
        parser.setStringToParse(manchesterString);
        return parser;
    }

    public OWLClassExpression ParseClassExpression(String manchesterString) {
        ManchesterOWLSyntaxParser parser = getManchesterParser(manchesterString);
        OWLClassExpression expr = parser.parseClassExpression();

        System.out.println(expr);
        return expr;
    }
}
