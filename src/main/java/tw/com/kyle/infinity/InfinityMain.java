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
import py4j.GatewayServer;

/**
 *
 * @author Sean_S325
 */
public class InfinityMain {

    public static void testDummy() throws Exception {
        String ontoFilePath = "E:\\Kyle\\TextInf\\etc\\dbpedia_2016-04.owl";
        DLQuerySample dl_query = new DLQuerySample();
        dl_query.LoadOntology(ontoFilePath);
        JsonElement jelem = dl_query.QueryAsJson("Bird and Animal");
        JsonElement jsatis = dl_query.AskAsJson("Bird and Plant");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jobj = new JsonObject();
        jobj.add("Query", jelem);
        jobj.add("Ask", jsatis);
        String resp = gson.toJson(jobj);        
        System.out.println(resp);
    }       

    public static void testInfOnto() throws Exception {
        InfOntology inf_onto = new InfOntology();
        inf_onto.LoadOWL("Prefix: : <http://example.com/>\n" + "Class: Person\n");
        inf_onto.AddAxioms("Class: Ghost");
        inf_onto.RemoveAxioms("Class: Ghost");
        System.out.println(inf_onto.toManchester());
        // inf_onto.ParseExpression("Human and not Parent");
    }
    
    private final DLQuerySample query_inst = new DLQuerySample();
    public InfinityMain(){}
    public InfOntology CreateOntology() {
        InfOntology ontology = null;
        try {
            ontology = new InfOntology();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ontology;
    }

    public InfDLQuery CreateQuery(InfOntology ontology) {
        InfDLQuery query = new InfDLQuery(ontology);
        return query;
    }
    
    public static void main(String[] argv) throws Exception {
        // testDummy();
        testInfOnto();
    }
    
    public static void main2(String[] argv) throws Exception {
        GatewayServer gateway = new GatewayServer(new InfinityMain(), 21322);
        gateway.start();
        System.out.println("Gateway server running on 0.0.0.0:21322");
        System.out.println(""
                + "from py4j.java_gateway import JavaGateway,  GatewayParameters\n"
                + "gateway = JavaGateway(gateway_parameters=GatewayParameters(port=21322))\n"
                + "infinity = gateway.entry_point\n"
                + "infOnto = infinity.CreateOntology()\n"
                + "infQuery = infinity.CreateQuery(infOnto)"
                + "infOnto.LoadOntology(<ontofilepath>)\n"
                + "infQuery.Query(<Manchester syntax>)\n"
                + "infQuery.Ask(<Manchester syntax>)");

    }
}
