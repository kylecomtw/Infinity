package tw.com.kyle.infinity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InfDLQuery {
    private InfDLQueryEngine engine = null;
    private InfOntology ontology = null;
    private ShortFormProvider shortFormProvider = null;

    public InfDLQuery(InfOntology onto) {
        ontology = onto;
        engine = new InfDLQueryEngine(ontology);
        shortFormProvider = new SimpleShortFormProvider();
    }

    private <T extends OWLEntity> List<String> mapToShortForm(Stream<T> entstream){
        return entstream
            .map((x)->shortFormProvider.getShortForm(x))
            .collect(Collectors.toList());
    }

    public InfQueryResult Query(String queryStr) throws Exception {
        InfQueryResult result = new InfQueryResult();
        result.subClasses = mapToShortForm(engine.getSubClasses(queryStr, false));
        result.superClasses = mapToShortForm(engine.getSuperClasses(queryStr, false));
        result.equivalents = mapToShortForm(engine.getEquivalentClasses(queryStr));
        result.instances = mapToShortForm(engine.getInstances(queryStr, true));

        return result;
    }

    public InfAskResult AskAsJson(String queryStr) {
        InfAskResult result = new InfAskResult();
        result.isSatisfiable = engine.isSatisfiable(queryStr);

        return result;
    }


}
