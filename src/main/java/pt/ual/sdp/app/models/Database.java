package pt.ual.sdp.app.models;

import pt.ual.sdp.app.controllers.Delivery;
import pt.ual.sdp.app.controllers.Item;

import java.sql.*;
import java.util.*;

public class Database {
    public static final String address = "jdbc:postgresql://postgres-0:5432/restAPI";
    public static Connection conn = null;
    public static final String user = "postgres";
    public static final String password = "30002299";

    public static void connectDB(){
        try {
            conn = DriverManager.getConnection(address, user, password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("Falhou.");
        }
    }

    public static void disconnectDB(){
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("Falhou.");
        }
    }

    public static List<List<String>> getItems(){
        List<List<String>> get = new ArrayList<>();
        connectDB();
        try {
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery("select * from item");
            while(result.next()){
                int id = getItemId(result.getString(2));
                int stock = getStock(id);
                get.add(Arrays.asList(result.getString(2), result.getString(3), String.valueOf(stock)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        disconnectDB();
        return get;
    }

    public static int getItemId(String itemName){
        int id = 0;
        try {
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery("select id from item where name='" + itemName + "'");
            while(result.next()) {
                id = result.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return id;
    }

    public static String getItemName(int id){
        String itemName = null;
        try {
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery("select name from item where id=" + id + "");
            result.next();
            itemName = result.getString(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return itemName;
    }

    private static int getStock(int id) {
        int stock = 0;
        try {
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery("select * from stock where item_id=" + id);
            while(result.next()) {
                stock = stock + result.getInt(2);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return stock;
    }

    public static List<List<String>> getAllItemsInStock() {
        List<List<String>> get = new ArrayList<>();
        connectDB();
        try {
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery("select * from item");
            while(result.next()){
                int id = getItemId(result.getString(2));
                int stock = getStock(id);
                if (stock > 0) {
                    get.add(Arrays.asList(result.getString(2), result.getString(3), String.valueOf(stock)));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        disconnectDB();

        return get;
    }

    public static String createItem(Item item) {
        connectDB();
        try {
            Statement statement = conn.createStatement();
            statement.execute("INSERT INTO item(name, description) VALUES('" + item.getItemName() + "', '" + item.getDescription() + "')");
            statement.execute("INSERT INTO stock(item_id, stock) VALUES(" + getItemId(item.getItemName()) + ", " + 0 + ")");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        disconnectDB();
        return "criou item";
    }

    public static String createItem(String name, String description) {
        connectDB();
        try {
            Statement statement = conn.createStatement();
            statement.execute("INSERT INTO item(name, description) VALUES('" + name + "', '" + description + "')");
            statement.execute("INSERT INTO stock(item_id, stock) VALUES(" + getItemId(name) + ", " + 0 + ")");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        disconnectDB();
        return "criou item";
    }


    public static int depositItems(int qty, String itemName){
        connectDB();
        try {
            Statement statement = conn.createStatement();
            int id = getItemId(itemName);
            if (id == 0){
                return 1;
            }
            //INSERT INTO deposit(qty, item_id) VALUES(10, 2);
            statement.execute("INSERT INTO deposit(qty, item_id) VALUES(" + qty + ", " + id + ")");
            statement.execute("UPDATE stock SET stock = stock + %s WHERE item_id = %d".formatted(qty, id));
        } catch (SQLException throwables) {
            return 1;
        }
        disconnectDB();
        return 0;
    }

    /*//Old overloaded method (Unused)
    public static void depositItems(int qty, int id){
        connectDB();
        try {
            Statement statement = conn.createStatement();
            statement.execute("INSERT INTO deposit(qty, item_id) VALUES(" + qty + ", " + id + ")");
            statement.execute("update stock set stock = stock + %s where item_id = %d".formatted(qty, id));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        disconnectDB();
    }//*/

    public static int createDelivery(String address, int qty, String itemName){
        connectDB();
        try {
            Statement statement = conn.createStatement();
            statement.execute("INSERT INTO delivery(address) VALUES('" + address + "')", Statement.RETURN_GENERATED_KEYS);
            ResultSet result = statement.getGeneratedKeys();
            result.next();
            int deliveryid = result.getInt(1);
            int itemid = getItemId(itemName);
            statement.execute("INSERT INTO deliveryItem(delivery_id, item_id, qty) VALUES(" + deliveryid + ", " + itemid + ", " + qty + ")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        disconnectDB();
        return 0;
    }

    //Overloaded method for hashmaps<name, qty>
    public static int createDelivery(String address, Map<String, Integer> itemNames){
        connectDB();
        try {
            Statement statement = conn.createStatement();
            statement.execute("INSERT INTO delivery(address) VALUES('" + address + "')", Statement.RETURN_GENERATED_KEYS);
            ResultSet result = statement.getGeneratedKeys();
            result.next();
            int deliveryId = Integer.parseInt(result.getString(1));
            Iterator it = itemNames.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                statement.execute("INSERT INTO deliveryItem(delivery_id, item_id, qty) VALUES(" + deliveryId + ", " + getItemId(pair.getKey().toString()) + ", " + pair.getValue() + ")");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        disconnectDB();
        return 0;
    }
    //returns [delivery[address[[item, quantity]]], delivery[address[[item, quantity][item, quantity][item, quantity][item, quantity]]]]
    public static List<Delivery> getDelivery(){
        connectDB();
        List<Delivery> deliveries = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM delivery");
            while(result.next()){
                int deliveryID = result.getInt(1);
                String deliveryAddress = result.getString(2);
                Map<String, Integer> deliveryItemMap = new HashMap<>();
                ResultSet deliveryItems = statement.executeQuery("SELECT * FROM deliveryitem JOIN item ON deliveryitem.item_id = item.id WHERE delivery_id=" + deliveryID);
                while(deliveryItems.next()) {
                    String itemName = deliveryItems.getString(6);
                    int itemQty = deliveryItems.getInt(4);
                    deliveryItemMap.put(itemName, itemQty);
                }
                Delivery delivery = new Delivery(deliveryID, deliveryAddress, deliveryItemMap);
                deliveries.add(delivery);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        disconnectDB();
        return deliveries;
    }

    public static String alterDescription(String itemName, String newDescription){
        connectDB();
        try {
            Statement statement = conn.createStatement();
            int id = getItemId(itemName);
            statement.execute("UPDATE item SET description='" + newDescription + "' WHERE id=" + id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        disconnectDB();
        return "alterou o nome do item";
    }

    public static String alterAddress(int id, String newAddress){
        connectDB();
        try {
            Statement statement = conn.createStatement();
            statement.execute("UPDATE delivery SET address='" + newAddress + "' WHERE id=" + id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        disconnectDB();
        return "alterou a morada da entrega";
    }

    public static void deleteItem(String itemName){
        connectDB();
        try {
            Statement statement = conn.createStatement();
            int id = getItemId(itemName);
            if(getStock(id) == 0) {
                statement.execute("DELETE FROM stock WHERE item_id=" + id);
            }
            else {
                return;
            }
            statement.execute("DELETE FROM item WHERE id=" + id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        disconnectDB();
    }
}
