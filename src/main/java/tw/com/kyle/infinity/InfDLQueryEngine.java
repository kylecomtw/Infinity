package tw.com.kyle.infinity;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.ShortFormProvider;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

class InfDLQueryEngine {

    private final OWLReasoner reasoner;
    private final ManchesterParser parser;

    public InfDLQueryEngine(InfOntology ontology) {
        this.reasoner = new ReasonerFactory().createReasoner(ontology.GetOwlOntology());
        parser = new ManchesterParser(ontology);
    }

    public boolean isSatisfiable(String classExpressionString) {
        if (classExpressionString.trim().length() == 0) {
            return false;
        }
        OWLClassExpression classExpression = parser
                .ParseClassExpression(classExpressionString);
        boolean isSatisfied = reasoner.isSatisfiable(classExpression);
        return isSatisfied;
    }

    public Stream<OWLClass> getSuperClasses(String classExpressionString, boolean direct) {
        if (classExpressionString.trim().length() == 0) {
            return Stream.empty();
        }
        OWLClassExpression classExpression = parser
                .ParseClassExpression(classExpressionString);
        NodeSet<OWLClass> superClasses = reasoner
                .getSuperClasses(classExpression, direct);
        return superClasses.entities();
    }

    public Stream<OWLClass> getEquivalentClasses(String classExpressionString) {
        if (classExpressionString.trim().length() == 0) {
            return Stream.empty();
        }
        OWLClassExpression classExpression = parser
                .ParseClassExpression(classExpressionString);
        Node<OWLClass> equivalentClasses = reasoner.getEquivalentClasses(classExpression);
        Stream<OWLClass> result = null;
        if (classExpression.isAnonymous()) {
            result = equivalentClasses.entities();
        } else {
            result = equivalentClasses.getEntitiesMinus(classExpression.asOWLClass()).stream();
        }
        return result;
    }

    public Stream<OWLClass> getSubClasses(String classExpressionString, boolean direct) {
        if (classExpressionString.trim().length() == 0) {
            return Stream.empty();
        }
        OWLClassExpression classExpression = parser
                .ParseClassExpression(classExpressionString);
        NodeSet<OWLClass> subClasses = reasoner.getSubClasses(classExpression, direct);
        return subClasses.entities();
    }

    public Stream<OWLNamedIndividual> getInstances(String classExpressionString,
                                                boolean direct) {
        if (classExpressionString.trim().length() == 0) {
            return Stream.empty();
        }
        OWLClassExpression classExpression = parser
                .ParseClassExpression(classExpressionString);
        NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(classExpression,
                direct);
        return individuals.entities();
    }
}
