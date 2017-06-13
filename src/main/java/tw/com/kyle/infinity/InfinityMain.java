/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.com.kyle.infinity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
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
        JsonElement jelem = dl_query.QueryAsJson("Animal");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String resp = gson.toJson(jelem);
        System.out.println(resp);
    }       
    
    private final DLQuerySample query_inst = new DLQuerySample();
    public InfinityMain(){}
    public DLQuerySample CreateInstance() {
        DLQuerySample inst = new DLQuerySample();
        return inst;
    }   

    public static void main(String[] argv) throws Exception {
        GatewayServer gateway = new GatewayServer(new InfinityMain(), 21322);
        gateway.start();
        System.out.println("Gateway server running on 0.0.0.0:21322");
        System.out.println(""
                + "from py4j.java_gateway import JavaGateway,  GatewayParameters\n"
                + "gateway = JavaGateway(gateway_parameters=GatewayParameters(port=21322))\n"
                + "infinity = gateway.entry_point\n"
                + "owlQuery = infinity.CreateInstance()\n"
                + "owlQuery.LoadOntology(<ontofilepath>)\n"
                + "owlQuery.Query(<Manchester syntax>)\n");

    }
}
