
var selectProducers = function(){
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "http://localhost:3000/api/queries/selectAllParticipants", false);
    xhttp.setRequestHeader("Accept", "application/json");
    xhttp.send();
    var response = JSON.parse(xhttp.responseText);


    // var Participants = Backbone.Model.extend({
    //     url: '"http://localhost:3000/api/queries/selectAllParticipants'
    // });
    //
    // let tmp = Participants.fetch();


    console.log('Hello');
    console.log(response);
}

var getStorageInfo = function(){
    let user_id = "storage_1";

    let balanceField = document.getElementById("Balance");
    let companyField = document.getElementById("CompanyName");

    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "http://localhost:3000/api/Storage/" + user_id, false);
    xhttp.setRequestHeader("Accept", "application/json");
    xhttp.send();

    var storage = JSON.parse(xhttp.responseText);



    companyField.textContent = storage["companyName"];
    balanceField.textContent = storage["balance"];
    console.log(storage["companyName"]);
    console.log(storage["balance"]);
};


var getStorageProducts = function(){
    let user_id = "storage_1";

    let table = document.getElementById("tbl");

    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "http://localhost:3000/api/Product", false);
    xhttp.setRequestHeader("Accept", "application/json");
    xhttp.send();

    var products = JSON.parse(xhttp.responseText);

    var n = products.length + 1 - table.rows.length;
    if(n > 0){
        addRows(table, n);
    }

    for(var i = 0; i < products.length; i++) {
        console.log(products[i].owner);
        if (products[i].owner == ("resource:ru.nsk.decentury.Storage#" + user_id)) {
            printTableRow(table, products[i], i);
        }
    }
};


var addRows = function(table, number){
    for(var i = 0; i < number; i++){
        var row = table.insertRow();
        for(var j = 0; j < table.rows[0].cells.length; j++){
            row.insertCell(j);
        }
    }
};


var printTableRow = function (table, product, index) {
    var row = table.rows[index + 1];

    row.cells[0].textContent = product.id;
    row.cells[1].textContent = product.name;
    row.cells[2].textContent = product.vendorCode;
    row.cells[3].textContent = product.owner;
    row.cells[4].textContent = product.state;
};



var sendRequest = function(){
    let user_id = "storage_1";

    let productAmount = document.getElementById("ProductAmount");
    let vendorCode = document.getElementById("VendorCode");
    let recipient = document.getElementById("Seller");
    let price = document.getElementById("Price");

    let request = {
        "$class": "ru.nsk.decentury.Request",
        "id": user_id + "_request",// + Math.random(),
        "seller": "resource:ru.nsk.decentury.Producer#" + recipient.value,
        "recipient": "resource:ru.nsk.decentury.Storage#" + user_id,
        "vendorCode": vendorCode.value,
        "amount": productAmount.value,
        "price": price.value,
        "termOfSupply": "2018-04-28T04:59:20.601Z",
        "state": "SENT"
    };

    // let request =
    //     {
    //         "$class": "ru.nsk.decentury.Request",
    //         "id": "789456",
    //         "seller": "resource:ru.nsk.decentury.Producer#producer_2",
    //         "recipient": "resource:ru.nsk.decentury.Storage#storage_1",
    //         "vendorCode": "1",
    //         "amount": 1,
    //         "price": 1,
    //         "termOfSupply": "2018-04-28T09:03:28.883Z",
    //         "state": "SENT"
    //     };


    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "http://localhost:3000/api/Request", false);
    xhttp.setRequestHeader("Accept", "application/json");
    xhttp.setRequestHeader("Content-Type", "application/json");

    // xhttp.onreadystatechange = function f(){
    //     console.log("Hello");
    //     console.log(xhttp.getAllResponseHeaders());
    //     console.log(JSON.parse(xhttp.responseText));
    //     console.log("Hello");
    // };

    xhttp.send(JSON.stringify(request));


    var responseText = JSON.parse(xhttp.responseText);

    if ('error' in responseText) {
        alert(responseText.error.message);
    } else {
        alert("Request has been sent successfully");
        location.reload();
    }
    //
    //
    // console.log(storage["companyName"]);
    // console.log(storage["balance"]);
};


var productTransition = function(){
    let user_id = "storage_1";

    let deal = document.getElementById("DealId");

    console.log(deal.value);

    let request = {
        "$class": "ru.nsk.decentury.ProductTransition",
        "deal": "resource:ru.nsk.decentury.Deal#" + deal.value,
        // "transactionId": user_id + "product_transition" + Math.random(),
        // "timestamp": "2018-04-28T06:14:18.150Z"
    };


    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "http://localhost:3000/api/ProductTransition", false);
    xhttp.setRequestHeader("Accept", "application/json");
    xhttp.setRequestHeader("Content-Type", "application/json");

    xhttp.send(JSON.stringify(request));


    var responseText = JSON.parse(xhttp.responseText);
    if ('error' in responseText) {
        alert(responseText.error.message);
    } else {
        alert("Product received successfully");
        location.reload();
    }
};



getStorageInfo();
getStorageProducts();