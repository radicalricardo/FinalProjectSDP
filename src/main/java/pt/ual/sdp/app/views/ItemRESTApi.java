package pt.ual.sdp.app.views;

import pt.ual.sdp.app.controllers.Item;
import pt.ual.sdp.app.models.Database;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

//Items
@WebServlet("/Items/*")
public class ItemRESTApi extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        setAccessControlHeaders(resp);
        List<List<String>> items = Database.getItems();
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        for(int i = 0; i < items.size(); i++){
            List<String> innerList = items.get(i);
            JsonObjectBuilder arrayElement = Json.createObjectBuilder();
            arrayElement.add("name", innerList.get(0));
            arrayElement.add("description", innerList.get(1));
            arrayElement.add("quantityStock", innerList.get(2));
            arrayBuilder.add(arrayElement);
        }
        jsonBuilder.add("items", arrayBuilder);
        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();
    }

    //{"name": "shaman", "description": "enhacement"}
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject reader = Json.createReader(req.getReader()).readObject();
        Item item = new Item(reader.getString("name"), reader.getString("description"));
        //Database.createItem(reader.getString("name"),reader.getString("description"));
        int result = Database.createItem(item);
        if (result == 1) {
            resp.sendError(201, "Erro a registar o item.");
        }
    }

    //{"name": "shaman", "description": "elemental"}
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject reader = Json.createReader(req.getReader()).readObject();
        Database.alterDescription(reader.getString("name"), reader.getString("description"));
    }

    //{"name": "mage"}
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject reader = Json.createReader(req.getReader()).readObject();
        Database.deleteItem(reader.getString("name"));
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

