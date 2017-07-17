/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.com.kyle.infinity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
// import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.manchestersyntax.renderer.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

public class DLQuerySample {
    private DLQueryPrinter dlQueryPrinter = null;
    public DLQuerySample() {

    }
    
    public void LoadOntology(String ontoPath) throws Exception{
        if (!Files.exists(Paths.get(ontoPath))) {
            throw new FileNotFoundException("Cannot find " + ontoPath);
        }
        File ontoFile = new File(ontoPath);
        // Load an example ontology.
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager
                .loadOntologyFromOntologyDocument(new FileDocumentSource(ontoFile));
        // We need a reasoner to do our query answering

        // These two lines are the only relevant difference between this code and the original example
        // This example uses HermiT: http://hermit-reasoner.com/
        OWLReasoner reasoner = new Reasoner.ReasonerFactory().createReasoner(ontology);
        
        ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
        // Create the DLQueryPrinter helper class. This will manage the
        // parsing of input and printing of results
        dlQueryPrinter = new DLQueryPrinter(new DLQueryEngine(reasoner,
                shortFormProvider), shortFormProvider);
    }
    
    public JsonElement QueryAsJson(String queryStr) { 
        if (dlQueryPrinter != null){ 
            try{
                return dlQueryPrinter.askQuery(queryStr.trim());    
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.toString());
                return new JsonPrimitive("error");
            }
        } else {
            return new JsonPrimitive("Error: No ontology loaded");
        }        
    }
    
    public JsonElement AskAsJson(String queryStr) {
        if (dlQueryPrinter != null){
            try{
                return dlQueryPrinter.askSatisfiable(queryStr.trim());    
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.toString());
                return new JsonPrimitive("error");
            }
        } else {
            return new JsonPrimitive("Error: No ontology loaded");
        }    
    }
    
    public String Query(String queryStr) { 
        JsonElement jelem = QueryAsJson(queryStr);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String resp = gson.toJson(jelem);
        
        return resp;
    }
    
    public String Ask(String queryStr) {
        JsonElement jelem = AskAsJson(queryStr);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String resp = gson.toJson(jelem);
        
        return resp;
    }
}

class DLQueryEngine {

    private final OWLReasoner reasoner;
    private final DLQueryParser parser;

    public DLQueryEngine(OWLReasoner reasoner, ShortFormProvider shortFormProvider) {
        this.reasoner = reasoner;
        parser = new DLQueryParser(reasoner.getRootOntology(), shortFormProvider);
    }
    
    public boolean isSatisfiable(String classExpressionString) {
        if (classExpressionString.trim().length() == 0) {
            return false;
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        boolean isSatisfied = reasoner.isSatisfiable(classExpression);
        return isSatisfied;
    }
    
    public Set<OWLClass> getSuperClasses(String classExpressionString, boolean direct) {
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        NodeSet<OWLClass> superClasses = reasoner
                .getSuperClasses(classExpression, direct);
        return superClasses.getFlattened();
    }

    public Set<OWLClass> getEquivalentClasses(String classExpressionString) {
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        Node<OWLClass> equivalentClasses = reasoner.getEquivalentClasses(classExpression);
        Set<OWLClass> result = null;
        if (classExpression.isAnonymous()) {
            result = equivalentClasses.getEntities();
        } else {
            result = equivalentClasses.getEntitiesMinus(classExpression.asOWLClass());
        }
        return result;
    }

    public Set<OWLClass> getSubClasses(String classExpressionString, boolean direct) {
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        NodeSet<OWLClass> subClasses = reasoner.getSubClasses(classExpression, direct);
        return subClasses.getFlattened();
    }

    public Set<OWLNamedIndividual> getInstances(String classExpressionString,
            boolean direct) {
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(classExpression,
                direct);
        return individuals.getFlattened();
    }
}

class DLQueryParser {

    private final OWLOntology rootOntology;
    private final BidirectionalShortFormProvider bidiShortFormProvider;

    public DLQueryParser(OWLOntology rootOntology, ShortFormProvider shortFormProvider) {
        this.rootOntology = rootOntology;
        OWLOntologyManager manager = rootOntology.getOWLOntologyManager();
        Set<OWLOntology> importsClosure = rootOntology.getImportsClosure();
        // Create a bidirectional short form provider to do the actual mapping.
        // It will generate names using the input
        // short form provider.
        bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(manager,
                importsClosure, shortFormProvider);
    }

    public OWLClassExpression parseClassExpression(String classExpressionString) {
        OWLDataFactory dataFactory = rootOntology.getOWLOntologyManager()
                .getOWLDataFactory();
        ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(
                dataFactory, classExpressionString);
        parser.setDefaultOntology(rootOntology);
        OWLEntityChecker entityChecker = new ShortFormEntityChecker(bidiShortFormProvider);
        parser.setOWLEntityChecker(entityChecker);
        return parser.parseClassExpression();
    }
}

class DLQueryPrinter {

    private final DLQueryEngine dlQueryEngine;
    private final ShortFormProvider shortFormProvider;

    public DLQueryPrinter(DLQueryEngine engine, ShortFormProvider shortFormProvider) {
        this.shortFormProvider = shortFormProvider;
        dlQueryEngine = engine;
    }
    
    public JsonElement askSatisfiable(String classExpression) {
        if (classExpression.length() == 0) {
            System.out.println("No class expression specified");
            return new JsonPrimitive("Empty query");
        } else {            
            boolean isSatisfied = dlQueryEngine.isSatisfiable(classExpression);
            return new JsonPrimitive(isSatisfied);
        }
    }
    public JsonElement askQuery(String classExpression) {
        if (classExpression.length() == 0) {
            System.out.println("No class expression specified");
            return new JsonPrimitive("Empty query");
        } else {
            JsonObject jobj = new JsonObject();
            try {                                
                Set<OWLClass> superClasses = dlQueryEngine.getSuperClasses(
                        classExpression, false);
                jobj.add("SuperClasses", toJsonArray(superClasses));
                Set<OWLClass> equivalentClasses = dlQueryEngine
                        .getEquivalentClasses(classExpression);
                jobj.add("EquivalentClasses", toJsonArray(equivalentClasses));                
                Set<OWLClass> subClasses = dlQueryEngine.getSubClasses(classExpression,
                        true);
                jobj.add("SubClasses", toJsonArray(subClasses));                 
                Set<OWLNamedIndividual> individuals = dlQueryEngine.getInstances(
                        classExpression, true);
                jobj.add("Instances", toJsonArray(individuals));                
            } catch (ParserException e) {
                System.out.println(e.getMessage());
            }
            return jobj;
        }
    }
    
    private JsonArray toJsonArray(Set<? extends OWLEntity> entities) {
        if (entities.isEmpty()) return new JsonArray();
        
        JsonArray jarr = new JsonArray();
        for(OWLEntity entity: entities){
            jarr.add(shortFormProvider.getShortForm(entity));
        }
        
        return jarr;
    }
    
    private void printEntities(String name, Set<? extends OWLEntity> entities,
            StringBuilder sb) {
        sb.append(name);
        int length = 50 - name.length();
        for (int i = 0; i < length; i++) {
            sb.append(".");
        }
        sb.append("\n\n");
        if (!entities.isEmpty()) {
            for (OWLEntity entity : entities) {
                sb.append("\t").append(shortFormProvider.getShortForm(entity))
                        .append("\n");
            }
        } else {
            sb.append("\t[NONE]\n");
        }
        sb.append("\n");
    }
}
