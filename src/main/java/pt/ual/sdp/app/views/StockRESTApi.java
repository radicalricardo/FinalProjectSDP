package pt.ual.sdp.app.views;

import pt.ual.sdp.app.models.Database;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

//Stock
@WebServlet("/Stock/*")
public class StockRESTApi extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        setAccessControlHeaders(resp);
        List<List<String>> items = Database.getAllItemsInStock();
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        JsonObjectBuilder innerJson = Json.createObjectBuilder();

        for(int i = 0; i < items.size(); i++){
            List<String> innerList = items.get(i);
            innerJson.add("name", innerList.get(0));
            innerJson.add("description", innerList.get(1));
            innerJson.add("quantityStock", innerList.get(2));
            jsonBuilder.add("item-" + i, innerJson);
        }
        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();
    }

    //{"name": "shaman", "qty" : 30}
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject reader = Json.createReader(req.getReader()).readObject();
        int result = Database.depositItems(reader.getInt("qty"),reader.getString("name"));
        if (result == 1){
            resp.sendError(201, "O item não foi registado");
        }
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

