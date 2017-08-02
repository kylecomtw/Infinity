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

import java.util.List;

/**
 *
 * @author Sean_S325
 */
public class InfinityMain {



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

    public String SyntaxCheck(InfOntology onto, String manString){
        ManchesterParser parser = new ManchesterParser(onto);
        try {
            return parser.CheckSyntax(manString);
        } catch (Exception e) {
            // e.printStackTrace();
            return "Error: " + e.toString();
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
                + "infOnto = infinity.CreateOntology()\n"
                + "infOnto.ImportOntologyFromString(<ontoString>)\n"
                + "infOnto.ImportOntology(<ontoFiles>)\n"
                + "infOnto.AddAxioms(<ontoString>)\n"
                + "infOnto.RemoveAxioms(<ontoString>)\n"
                + "---"
                + "infQuery = infinity.CreateQuery(infOnto)\n"
                + "infQuery.QueryForJson(<Manchester class expr>)\n"
                + "infQuery.AskForJson(<Manchester class expr>)");
    }
}
