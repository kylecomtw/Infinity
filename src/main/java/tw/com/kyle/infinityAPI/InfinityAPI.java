package tw.com.kyle.infinityAPI;

import java.util.List;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.springframework.web.bind.annotation.*;
import tw.com.kyle.infinity.*;
import tw.com.kyle.infinityAPI.InfinityTask.StatusEnum;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@RestController
public class InfinityAPI {

    private static final String template = "Hello, %s!";
    private InfOntology infInst = null;

    public InfinityAPI(){
        try {
            infInst = new InfOntology();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @RequestMapping("/")
    @ResponseBody()
    public String index(){
        return "Infinity API standing by";
    }

    @RequestMapping(value="/ontologies", method=GET)
    public List<String> ontologies() {
        List<String> ontoList = infInst.ListOntologyIRIs();
        return ontoList;
    }

    @RequestMapping(value="/ontology", method=GET)
    public InfinityTask ontology(@RequestParam(value="iri", defaultValue="") String iri) {
        OWLOntology onto = infInst.GetOwlOntology(IRI.create(iri));
        String ontoStr = infInst.toManchester(IRI.create(iri));
        if (ontoStr.length() > 0){
            return new InfinityTask(StatusEnum.SUCCESS, ontoStr);
        } else {
            return new InfinityTask(StatusEnum.FAILED, "Cannot find " + iri);
        }
    }

    @RequestMapping(value="/ontology/info", method=GET)
    public InfinityOntoInfo ontologyInfo(@RequestParam(value="iri", defaultValue="") String iri_str) {
        boolean hasIRI = infInst.SetDefaultIRI(IRI.create(iri_str));

        if (hasIRI) {
            IRI iri = IRI.create(iri_str);
            List<String> classes = infInst.ListClasses(iri);
            List<String> idvs = infInst.ListIndividuals(iri);
            List<String> props = infInst.ListProperties(iri);
            InfinityOntoInfo onto_info = new InfinityOntoInfo(classes, props, idvs);
            return onto_info;
        } else {
            return InfinityOntoInfo.EmptyInfo();
        }
    }

    @RequestMapping(value="/ontology", method=POST)
    public InfinityTask ontology(
            @RequestParam(value="iri") String iri_str,
            @RequestBody String ontoStr){
        IRI iri = IRI.create(iri_str);
        infInst.RemoveIfExists(iri);
        try {
            OWLOntology onto = infInst.ImportOntologyFromString(ontoStr);
            String onto_iri = getOntologyIRI(onto);
            if (onto_iri.equals(iri_str)) {
                return new InfinityTask(StatusEnum.SUCCESS, onto_iri);
            } else {
                return new InfinityTask(StatusEnum.FAILED, "IRI mismatch");
            }
        } catch (Exception ex){
            return new InfinityTask(StatusEnum.ERROR, ex.toString());
        }
    }

    @RequestMapping(value="/ontology/check", method=POST)
    public InfinityTask ontologyCheck(@RequestBody String ontoStr){

        ManchesterParser parser = new ManchesterParser(infInst);
        try {
            String checkRet = parser.CheckSyntax(ontoStr);
            if(checkRet.isEmpty()){
                return new InfinityTask(StatusEnum.SUCCESS, "");
            } else {
                return new InfinityTask(StatusEnum.FAILED, checkRet);
            }
        } catch (Exception e) {
            // e.printStackTrace();
            return new InfinityTask(StatusEnum.ERROR, e.toString());
        }

    }

    @RequestMapping(value="/ontology/query", method=POST)
    public InfinityQueryResult ontologyQuery(
            @RequestParam(value="iri", defaultValue="") String iri_str,
            @RequestBody String classExpr){
        boolean hasIRI = infInst.SetDefaultIRI(IRI.create(iri_str));
        if (hasIRI){
            InfDLQuery query = new InfDLQuery(infInst);
            try {
                InfQueryResult queryRes = query.Query(classExpr);
                return new InfinityQueryResult(StatusEnum.SUCCESS, queryRes);
            } catch(Exception ex){
                return new InfinityQueryResult(StatusEnum.ERROR, null);
            }

        } else {
            return new InfinityQueryResult(StatusEnum.ERROR, null);
        }
    }

    @RequestMapping(value="/ontology/ask", method=POST)
    public InfinityTask ontologyAsk(
            @RequestParam(value="iri", defaultValue="") String iri_str,
            @RequestBody String classExpr){
        boolean hasIRI = infInst.SetDefaultIRI(IRI.create(iri_str));
        if (hasIRI){
            InfDLQuery query = new InfDLQuery(infInst);
            try {
                InfAskResult queryRes = query.Ask(classExpr);
                return new InfinityTask(StatusEnum.SUCCESS, Boolean.toString(queryRes.isSatisfiable));
            } catch(Exception ex){
                return new InfinityTask(StatusEnum.ERROR, "");
            }

        } else {
            return new InfinityTask(StatusEnum.ERROR, "");
        }
    }

    private String getOntologyIRI(OWLOntology onto){
        OWLOntologyID oid = onto.getOntologyID();
        if (oid.getOntologyIRI().isPresent()){
            return oid.getOntologyIRI().get().toString();
        } else {
            return oid.toString();
        }
    }

}

