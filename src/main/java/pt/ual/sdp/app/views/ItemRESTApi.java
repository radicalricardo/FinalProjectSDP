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
@SuppressWarnings("serial")
@WebServlet("/Items/*")
public class ItemRESTApi extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
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
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            JsonObject reader = Json.createReader(req.getReader()).readObject();
            Item item = new Item(reader.getString("name"), reader.getString("description"));
            //Database.createItem(reader.getString("name"),reader.getString("description"));
            Database.createItem(item);
        }
}

