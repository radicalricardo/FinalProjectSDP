$(document).ready(async function(){
    var items = await getItems();
    var stock = await getStock();
    var deliveries = await getDelivery();

    for(i = 0; i < items.length; i++){
        let trHTML = '<tr><td>' + items[i].name + '</td><td>' + items[i].description + '</td><td>' + items[i].quantityStock + '</td></tr>'   
        $("#items").append(trHTML);
    }

    for(i = 0; i < stock.length; i++){
        let trHTML = '<tr><td>' + stock[i].name + '</td><td>' + stock[i].description + '</td><td>' + stock[i].quantityStock + '</td></tr>'   
        $("#stock").append(trHTML);
    }

    for(i = 0; i < deliveries.length; i++){
        let trHTML = '<tr><td>' + deliveries[i].id + '</td><td>' + deliveries[i].address + '</td><td>' + deliveries[i].items.map(function(item){return item.name + ": " + item.qty}).join(", ") + '</td></tr>'   
        $("#deliveries").append(trHTML);
    }

});

async function getItems () {
const request = await fetch("http://localhost:8080/api/Items") // no nginx tudo o que é /api vai para a nossa api
const result = await request.json()
return result.items
}

async function getDelivery () {
const request = await fetch("http://localhost:8080/api/Delivery")
const result = await request.json()
return result.deliveries
}

async function getStock () {
const request = await fetch("http://localhost:8080/api/Stock")
const result = await request.json()
return result.stock
}