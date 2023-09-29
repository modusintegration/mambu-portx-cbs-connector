package io.portx.cbs.connector;

//import org.json.JSONObject;
import net.minidev.json.JSONObject;
import com.google.gson.Gson;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Base64;

public class JsonToStringProc implements Processor {

    public void process(Exchange exchange) throws Exception {
////        JSONObject jsonObject = new JSONObject();
//        String s = exchange.getIn().getBody(String.class);
//        System.out.print("JSON TO STRING AT PROC - BODY equals: ");
//        System.out.print(exchange.getIn().getBody(String.class));
//        System.out.println();
////        jsonObject.put("message", exchange.getIn().getBody(String.class));
////        String s = jsonObject.toString();
//
//        JsonObject json = new JsonObject();
//        json.addProperty("message", exchange.getIn().getBody(String.class));
//        Gson gson = new Gson();
//        String s = gson.toJson(json);
//        System.out.print("JSON TO STRING AT PROC - json:");
//        System.out.print(json);
//        System.out.println();


        String body = exchange.getIn().getBody(String.class);
        System.out.println("BODY: "+ body);
        Gson gson = new Gson();
        String json = gson.toJson(body);
        System.out.println(json);

        exchange.getIn().setBody(json);

    }

}
