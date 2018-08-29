var getProducerInfo = function(){
    let user_id = "producer_1";

    let balanceField = document.getElementById("Balance");
    let companyField = document.getElementById("CompanyName");

    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "http://localhost:3000/api/Producer/" + user_id, false);
    xhttp.setRequestHeader("Accept", "application/json");
    xhttp.send();

    var storage = JSON.parse(xhttp.responseText);

    companyField.textContent = storage["companyName"];
    balanceField.textContent = storage["balance"];
    console.log(storage["companyName"]);
    console.log(storage["balance"]);
};


var getStorageProducts = function(){
    let user_id = "producer_1";

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
        if (products[i].owner == ("resource:ru.nsk.decentury.Producer#" + user_id)) {
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


var responseToRequest = function(){
    let user_id = "producer_1";

    let requestId = document.getElementById("ReqestId2");
    let product = document.getElementById("ProductId");

    let response = {
        "$class": "ru.nsk.decentury.ResponseToRequest",
        "request": "resource:ru.nsk.decentury.Request#" + requestId.value,
        "products": ["resource:ru.nsk.decentury.Product#" + product.value],
        // "transactionId": user_id + "request" + Math.random(),
        // "timestamp": "2018-04-28T06:14:18.192Z"
    };


    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "http://localhost:3000/api/ResponseToRequest", false);
    xhttp.setRequestHeader("Accept", "application/json");
    xhttp.setRequestHeader("Content-Type", "application/json");

    console.log(JSON.stringify(response))


    xhttp.send(JSON.stringify(response));


    var responseText = JSON.parse(xhttp.responseText);
    if ('error' in responseText) {
        alert(responseText.error.message);
    } else {
        alert("Response to request has been sent successfully");
        location.reload();
    }
};



var takeRequest = function(){
    let user_id = "producer_1";

    let productAmount = document.getElementById("ProductAmount");
    let priceField = document.getElementById("Price");
    let reqestIdField = document.getElementById("ReqestId");


    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "http://localhost:3000/api/Request", false);
    xhttp.setRequestHeader("Accept", "application/json");
    xhttp.send();

    var requests = JSON.parse(xhttp.responseText);

    for(let i = 0; i < requests.length; i++) {
        console.log(requests[i].seller);
        if (requests[i].seller == ("resource:ru.nsk.decentury.Producer#" + user_id)) {
            productAmount.textContent = requests[i]["amount"];
            priceField.textContent = requests[i]["price"];
            reqestIdField.textContent = requests[i]["id"];
            break;
        }
    }
};





getProducerInfo();
getStorageProducts();