document.addEventListener('DOMContentLoaded', () => {
    const sendButton = document.querySelector('.textareaBox button');
    const messageInput = document.querySelector('.textareaBox textarea');
    const messageContainer = document.querySelector('.chat')
    const firstMessage = "您好，歡迎使用台灣大哥大智能客服，我是小麥，很高興為您服務!";
    let sessionId = null;
    addMessage(firstMessage,"chatgpt");
    messageInput.addEventListener('input', () => {
        if (messageInput.value.trim().length > 0){
            floatBoxClose();
        }
    });
    $(".float .btn").on("click", function () {
        floatBoxClose();
    });
    sendButton.addEventListener('click', function(event) {
        event.preventDefault();
        const userMessage = messageInput.value;
        if (userMessage === "" || userMessage === null) {
            return;
        }
        addMessage(userMessage,"user");
        messageInput.value = "";

        // Send the message to the server
        fetch('/api/1.0/chat/agents',{
            method : 'POST',
            headers : {
                'Content-Type': 'application/json'
            },
            body : JSON.stringify({
                userId : '2',
                sessionId : sessionId,
                question : userMessage
            })
        })
            .then(response => { return response.json(); })
            .then(data => {
                const response = data.data;
                sessionId = data.sessionId;
                addMessage(response,"chatgpt");
            })
            .catch(error => {
                console.error('Error:', error);
                //loadingMessage.textContent = '很抱歉，系統維護中，將為您轉接至真人客服。';
            })


    });

    function addMessage(text,role){
        const now = new Date();
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        const timeString = `${hours}:${minutes}`;
        const li = document.createElement('li');
        const dialog = document.createElement('div');
        dialog.className = "dialog";
        const content = document.createElement("div");
        content.className = "content";
        content.textContent = text;
        const time = document.createElement("time");
        time.textContent = timeString;
        dialog.appendChild(content);
        dialog.appendChild(time);
        if(role === "user"){
            li.className = "msg msgRight";
            li.appendChild(dialog);
            messageContainer.appendChild(li);
        }else{
            li.className = "msg";
            const avatar = document.createElement("i");
            avatar.className = "avatar";
            li.appendChild(avatar);
            li.appendChild(dialog);
            messageContainer.appendChild(li);
        }
        scrollDown();
    }

    function scrollDown() {
        $("main").scrollTop($("main").prop("scrollHeight"));
    }

    function floatBoxClose() {
        $(".float").slideUp(600);
    }

});