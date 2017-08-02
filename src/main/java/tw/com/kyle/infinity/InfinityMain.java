/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.com.kyle.infinity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import py4j.GatewayServer;

import java.net.URI;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author Sean_S325
 */
public class InfinityMain {


    private InfOntology inf_onto = null;
    public static InfinityMain CreateInstance() {
        return new InfinityMain();
    }

    public InfinityMain(){
        try{
            inf_onto = new InfOntology();
        } catch (Exception ex){
            ex.printStackTrace();
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

    public String RemoveIfExists(String iri){
        boolean ret = inf_onto.RemoveIfExists(IRI.create(iri));
        return String.format("{\"ret\": \"%s\"}", ret);
    }

    public String List(){
        String iris = String.join("\",\"", inf_onto.ListOntologyIRIs());
        return String.format("[\"%s\"]", iris);
    }

    public String Import(String ontoFile){
        try {
            URI onto_uri = Paths.get(ontoFile).toUri();
            OWLOntology in_onto = inf_onto.ImportOntology(onto_uri.toString());
            return String.format("{\"ret\": \"success\", \"iri\": \"%s\"}", getOntologyIRI(in_onto));
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"ret\": \"error\"}";
        }
    }

    public String Submit(String ontoString) {
        try {
            OWLOntology in_onto = inf_onto.ImportOntologyFromString(ontoString);
            return String.format("{\"ret\": \"success\", \"iri\": \"%s\"}", getOntologyIRI(in_onto));
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"ret\": \"error\"}";
        }
    }

    public String Query(String iri, String classExpr) {
        boolean hasIRI = inf_onto.SetDefaultIRI(IRI.create(iri));
        if (hasIRI){
            InfDLQuery query = new InfDLQuery(inf_onto);
            String queryRes = query.QueryForJson(classExpr);
            return queryRes;
        } else {
            return "{\"ret\": \"error\", \"message\": \"IRI not available\"}";
        }

    }

    public String Ask(String iri, String classExpr) {
        boolean hasIRI = inf_onto.SetDefaultIRI(IRI.create(iri));
        if (hasIRI){
            InfDLQuery query = new InfDLQuery(inf_onto);
            String askRes = query.AskForJson(classExpr);
            return askRes;
        } else {
            return "{\"ret\": \"error\", \"message\": \"IRI not available\"}";
        }
    }

    public String Check(String iri, String manString){
        boolean hasIRI = inf_onto.SetDefaultIRI(IRI.create(iri));
        if (hasIRI) {
            inf_onto.SetDefaultIRI(IRI.create(iri));
            ManchesterParser parser = new ManchesterParser(inf_onto);
            try {
                return parser.CheckSyntax(manString);
            } catch (Exception e) {
                // e.printStackTrace();
                return String.format("{\"res\": \"error\", \"message\": \"%s\"}", e.getMessage());
            }
        } else {
            return "{\"ret\": \"error\", \"message\": \"IRI not available\"}";
        }
    }

    public static void main(String[] argv) throws Exception {
        GatewayServer gateway = new GatewayServer(new InfinityMain(), 21322);
        gateway.start();
        System.out.println("Gateway server running on 0.0.0.0:21322");
        System.out.println(""
                + "from py4j.java_gateway import JavaGateway,  GatewayParameters\n"
                + "gateway = JavaGateway(gateway_parameters=GatewayParameters(port=21322))\n"
                + "infinity = gateway.entry_point\n"
                + "inf = infinity.CreateInstance()\n"
                + "inf.Submit(<ontoStr>)\n"
                + "inf.Import(<ontoFilePath>)\n"
                + "inf.Query(<ClassExpr>)\n"
                + "inf.Ask(<ClassExpr>)\n"
                + "inf.Check(<ontoStr>)\n");
    }
}
