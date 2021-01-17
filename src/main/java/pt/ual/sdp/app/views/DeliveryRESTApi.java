package pt.ual.sdp.app.views;

import pt.ual.sdp.app.controllers.Delivery;
import pt.ual.sdp.app.models.Database;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Delivery
@WebServlet("/Delivery/*")
public class DeliveryRESTApi extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        setAccessControlHeaders(resp);
        List<Delivery> deliveryList = Database.getDelivery();
        JsonArrayBuilder jsonArray = Json.createArrayBuilder();

        for (Delivery delivery : deliveryList){
            JsonObjectBuilder addressJson = Json.createObjectBuilder();
            addressJson.add("address", delivery.getAddress());
            JsonArrayBuilder deliveryArray = Json.createArrayBuilder();
            for(Map.Entry<String, Integer> item : delivery.getItem().entrySet()){
                JsonObjectBuilder buffer = Json.createObjectBuilder();
                buffer.add("name", item.getKey());
                buffer.add("qty", item.getValue());
                deliveryArray.add(buffer);
            }
            addressJson.add("items", deliveryArray.build());
            addressJson.add("id", delivery.getId());
            jsonArray.add(addressJson.build());
        }
        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("deliveries", jsonArray);
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();
    }

    //{"address": "Avenida do Exemplo", "items": [{"name" : "shaman", "qty": 30}, {"name" : "druid", "qty": 30}]}
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject reader = Json.createReader(req.getReader()).readObject();
        String address = reader.getString("address");
        JsonArray itemList = reader.getJsonArray("items");
        Map<String, Integer> delivery = new HashMap<>();

        for(int i = 0; i < itemList.size(); i++){
            delivery.put(itemList.getJsonObject(i).getString("name"), itemList.getJsonObject(i).getInt("qty"));
        }

        int result = Database.createDelivery(address, delivery);
        if (result == 1){
            resp.sendError(201, "A entrega não foi registada");
        }
    }

    //{"id": 30, "address": "Rua do Teste"}
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject reader = Json.createReader(req.getReader()).readObject();
        Database.alterAddress(reader.getInt("id"), reader.getString("address"));
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setAccessControlHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void setAccessControlHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET");
    }

}

