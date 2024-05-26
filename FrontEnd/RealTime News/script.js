var urlEndpoint = 'http://localhost:8080/subscribe?identificador=1';
var eventSource;

function connectToServer() {
    eventSource = new EventSource(urlEndpoint);

    eventSource.addEventListener("NEW_FOLLOW", function (event) {
        var data = JSON.parse(event.data);
        exibirNovoFollow(data.username)
    })

    eventSource.addEventListener("NEW_LIKE", function (event) {
        var data = JSON.parse(event.data);
        exibirNovoLike(data.username)
    })

    eventSource.onerror = function () {
        eventSource.close();
        console.log("connection closed");

        setTimeout(connectToServer, 2000);
    };

    window.onbeforeunload = function () {
        eventSource.close();
    }
}

$(document).ready(function () {
    connectToServer();
});

function exibirNovoFollow(username) {
    var div = document.createElement("div");
    div.className = "new-follow";
    var h3 = document.createElement("h3");
    var t = document.createTextNode("Você possui um novo seguidor!");
    h3.appendChild(t);

    var p = document.createElement("p");
    p.innerHTML = "@" + username + " acabou de seguir você";

    div.appendChild(h3);
    div.appendChild(p);

    document.getElementById("notifications").appendChild(div);
}

function exibirNovoLike(username) {
    var div = document.createElement("div");
    div.className = "new-like";
    var h3 = document.createElement("h3");
    var t = document.createTextNode("Você possui uma nova curtida!");
    h3.appendChild(t);

    var p = document.createElement("p");
    p.innerHTML = "@" + username + " acabou de curtir seu post";

    div.appendChild(h3);
    div.appendChild(p);

    document.getElementById("notifications").appendChild(div);
}