let user = {};

function sendMessage(text) {

    $.ajax({
        type: 'post',
        url: '/messages',
        data: {"message": text},
        headers: {
            "Authorization": localStorage.getItem("token")
        },
    }).done(function (data) {
        // let table = '';
        // for (let i = 0; i < data.length; i++)
        //      table +='<li>' + data[i].username + ": " + data[i].message + '</li>';
        //
        // document.getElementById("messages").innerHTML = table;

    });
}

function receiveMessage() {
    $.ajax({
        url: "/messages",
        method: "GET",
        dataType: "json",
        contentType: "application/json",
        headers: {
            "Authorization": localStorage.getItem("token")
        },
        success: function (data) {
            // let table = '';
            // for (let i = 0; i < data.length; i++)
            //     table +='<li>' + data[i].username + ": " + data[i].message + '</li>';
            //
            // let old = document.getElementById("messages").innerHTML;
            // document.getElementById("messages").innerHTML = old + table;
            $('#messages').first().after('<li>' + data[0]['username'] + ': ' + data[0]['message'] + '</li>');
            receiveMessage();
            // if (response != null)
            //     for (let i = 0; i < response.length; i++)
            //         $('#messages').first().after('<li>' + response[i].username + ": " + response[i].message + '</li>')
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
            +"<h1>Ваш нэйм: " + data.username + "</h1>\n" +
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

        $.ajax({
            url: "/messagesAll",
            method: "GET",
            dataType: "json",
            contentType: "application/json",
            headers: {
                "Authorization": localStorage.getItem("token")
            },
            success: function (data) {
                let table = '';
                for (let i = 0; i < data.length; i++)
                    table += '<li>' + data[i].username + ": " + data[i].message + '</li>';
                document.getElementById("messages").innerHTML = table;
            }});

        sendMessage("Hi");
        receiveMessage();

    }).fail(function () {
        alert("НЕ ОЧ");
    });


}

//
// $(document).ready(function () {
//    sendMessage(pageId, 'Hi');
//    receiveMessage(pageId);
// });