package pt.ual.sdp.app.controllers;

public class Item {

    private String itemName;
    private String description;

    public Item(String itemName, String description) {
        this.itemName = itemName;
        this.description = description;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }
}
