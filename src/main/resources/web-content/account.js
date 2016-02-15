
    function retrieveBalance(balanceConsumer) {
          var xhttp = new XMLHttpRequest();
          xhttp.onreadystatechange = function() {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                balanceConsumer(xhttp.responseText);
            }
          };
          xhttp.open("GET", "account/balance", true);
          xhttp.send();
    }

    function buy(amount, description, callback) {
        var transaction = {
                'amount' : -amount,
                'description' : description
            };
        var xhr = new XMLHttpRequest();
        xhr.open('post', 'account/transactions');
        xhr.onload = function(e) {  callback() };
        xhr.send(JSON.stringify(transaction));
        return false;
    }

    function retrieveTransactions(callback) {
          var xhttp = new XMLHttpRequest();
          xhttp.onreadystatechange = function() {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                callback(JSON.parse(xhttp.responseText));
            }
          };
          xhttp.open("GET", "account/transactions", true);
          xhttp.send();
    }