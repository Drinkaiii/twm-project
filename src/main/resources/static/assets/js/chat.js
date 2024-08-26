document.addEventListener('DOMContentLoaded', () => {
    const sendButton = document.querySelector('.textareaBox button');
    const messageInput = document.querySelector('.textareaBox textarea');
    const messageContainer = document.querySelector('.chat')
    const firstMessage = "您好，歡迎使用台灣大哥大智能客服，我是小麥，很高興為您服務!";
    const float = document.querySelector(".float");
    const float_content= document.createElement("div");
    const prompt = document.createElement("p");
    prompt.textContent = "小麥猜你想知道";
    prompt.className = "prompt";
    const button_container = document.createElement("div");
    button_container.className = "button-container";
    float_content.appendChild(prompt);
    float_content.appendChild(button_container);
    let sessionId = null;
    addMessage(firstMessage,"chatgpt");
    fetch('/api/1.0/chat/routines')
        .then(response => { return response.json(); })
        .then(data => {
            const response = data.data;
            response.forEach(item => {
                console.log(`ID: ${item.id}, Type Name: ${item.type_name}`);
                const button = document.createElement('button');
                button.className = 'btn';
                button.textContent = item.id+". "+item.type_name;
                button.setAttribute('data-id', item.id);
                button.addEventListener('click',function() {
                    const id = this.getAttribute('data-id');
                    floatBoxClose();
                    faqInfo(id);
                })
                button_container.appendChild(button);
                float.appendChild(float_content);
            });
        })
        .catch(error => {
            console.error('Error:', error);
            prompt.textContent = "系統異常，請點擊下方按鈕轉接至真人客服。"
            const button = document.createElement('button');
            button.className = 'btn';
            button.textContent = "前往真人客服頁面";
            button.addEventListener('click', function() {
                window.location.href = '#';
            });
            button_container.appendChild(button);
            float.appendChild(float_content);
        })

    messageInput.addEventListener('input', () => {
        if (messageInput.value.trim().length > 0){
            floatBoxClose();
        }
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
                sessionId = data.sessionId;
                console.error('Error:', error);
                addMessage("系統出現問題，請稍後再嘗試使用。","chatgpt");
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
        const time = document.createElement("time");
        time.textContent = timeString;
        if(role === "user"){
            content.textContent = text;
            dialog.appendChild(content);
            dialog.appendChild(time);
            li.className = "msg msgRight";
            li.appendChild(dialog);
            messageContainer.appendChild(li);
        }else if(role === "chatgpt"){
            content.textContent = text;
            dialog.appendChild(content);
            dialog.appendChild(time);
            li.className = "msg";
            const avatar = document.createElement("i");
            avatar.className = "avatar";
            li.appendChild(avatar);
            li.appendChild(dialog);
            messageContainer.appendChild(li);
        }else{
            li.className = "msg";
            const avatar = document.createElement("i");
            avatar.className = "avatar";
            const guidance = document.createElement("p");
            guidance.textContent = "請選擇以下選項：";
            content.appendChild(guidance);
            text.forEach(item => {
                const linkButton = document.createElement("button");
                linkButton.textContent = item.question;
                linkButton.className = "btn";
                linkButton.style.textAlign = "left";
                linkButton.setAttribute('data-id', item.id);
                linkButton.addEventListener("click",function (){
                    const id = this.getAttribute('data-id');
                    faqAnswer(id);
                })
                content.appendChild(linkButton);
            })
            dialog.appendChild(content);
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

    function faqInfo(id){
        fetch(`/api/1.0/chat/routines?type=${id}`)
            .then(response => { return response.json(); })
            .then(data => {
                const response = data.data;
                addMessage(response,"robot");
            })
            .catch(error => {
                console.error('Error:', error);
            })
    }
    function faqAnswer(id){
        fetch(`/api/1.0/chat/routines?question=${id}`)
            .then(response => { return response.json(); })
            .then(data => {
                const response = data.data;
                addMessage(response,"chatgpt");
            })
            .catch(error => {
                console.error('Error:', error);
            })
    }

});