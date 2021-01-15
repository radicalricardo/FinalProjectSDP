package pt.ual.sdp.app.controllers;

import java.util.Map;

public class Delivery {

    private int id;
    private String address;
    private Map<String, Integer> item;

    public Delivery(int id, String address, Map<String, Integer> item){
        this.id = id;
        this.address = address;
        this.item = item;
    }

    public String getId() {
        return String.valueOf(id);
    }

    public String getAddress() {
        return address;
    }

    public Map<String, Integer> getItem() {
        return item;
    }
}
