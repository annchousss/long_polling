let user = {};

function sendMessage(text) {

    $.ajax({
        type: 'post',
        url: '/messages',
        data: {"message": text},
        headers:{
            "Authorization" : localStorage.getItem("token")
        },
    }).done(function (data) {

    });
}
function receiveMessage() {
    $.ajax({
        url: "/messages",
        method: "GET",
        dataType: "json",
        contentType: "application/json",
        headers:{
            "Authorization" : localStorage.getItem("token")
        },
        success: function (response) {
            if(response != null)
                $('#messages').first().after('<li>' + response[0].username + ": " + response[0].message + '</li>')
            receiveMessage(pageId);
        }
    })
}

function authorize() {
    let username = document.getElementById("name").value;
    $.ajax({
        type: 'post',
        url: '/register',
        data: {"name": username}
    }).done(function (data) {
        user = data;

        localStorage.setItem('token', data.token);

        let div = "<h1>Ваш Id: " + data.token + "</h1>\n" +
        + "<h1>Ваш нэйм: " + data.username + "</h1>\n" +
            "<div>\n" +
            "    <input id=\"message\" placeholder=\"Ваше сообщение\">\n" +
            "    <button onclick=\"sendMessage(" +
            "            $('#message').val())\">Отправить</button>\n" +
            "</div>\n" +
            "<div>\n" +
            "    <ul id=\"messages\">\n" +
            "\n" +
            "    </ul>\n" +
            "</div>";

        document.getElementById("content").innerHTML = div;
        sendMessage("Hi");
        receiveMessage(data.token);

    }).fail(function () {
        alert("НЕ ОЧ");
    });


}
//
// $(document).ready(function () {
//    sendMessage(pageId, 'Hi');
//    receiveMessage(pageId);
// });