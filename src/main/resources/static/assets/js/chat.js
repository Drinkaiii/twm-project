document.addEventListener('DOMContentLoaded', () => {

    let isUserConnected = true;
    let reconnectTimer = null;
    let disconnectTime = null;
    let currentOverlay = null;

    function showConnectionPopup(message, isReconnect = false) {
        if (currentOverlay) {
            document.body.removeChild(currentOverlay);
            currentOverlay = null;
        }

        const overlay = document.createElement('div');
        overlay.style.position = 'fixed';
        overlay.style.top = '0';
        overlay.style.left = '0';
        overlay.style.width = '100%';
        overlay.style.height = '100%';
        overlay.style.backgroundColor = 'rgba(0, 0, 0, 0.7)';
        overlay.style.display = 'flex';
        overlay.style.justifyContent = 'center';
        overlay.style.alignItems = 'center';
        overlay.style.zIndex = '1000';

        const modal = document.createElement('div');
        modal.style.backgroundColor = 'white';
        modal.style.padding = '20px';
        modal.style.borderRadius = '5px';
        modal.style.textAlign = 'center';
        modal.style.boxShadow = '0 2px 10px rgba(0, 0, 0, 0.1)';

        const messageParagraph = document.createElement('p');
        messageParagraph.textContent = message;
        modal.appendChild(messageParagraph);

        const button = document.createElement('button');
        button.textContent = '確 認';
        button.style.marginTop = '20px';
        button.style.fontWeight = "700";
        button.style.padding = '7px 20px';
        button.onclick = function() {
            closeConnectionPopup(overlay);
            if (isReconnect) {
                resetChatSystem();
            }
        };
        modal.appendChild(button);

        overlay.appendChild(modal);
        document.body.appendChild(overlay);

        currentOverlay = overlay;
    }

    function closeConnectionPopup(overlay) {
        document.body.removeChild(overlay);
        if (overlay === currentOverlay) {
            currentOverlay = null;
        }
    }

    function handleOnline() {
        if (disconnectTime && (Date.now() - disconnectTime <= 15 * 60 * 1000)) {
            showConnectionPopup("您已重新連線，請繼續對話。", false);
            clearTimeout(reconnectTimer);
            reconnectTimer = null;
        } else {
            showConnectionPopup("您已重新連線，因斷線時間過長，請重新開始對話。");
        }
        isUserConnected = true;
        disconnectTime = null;
    }

    function handleOffline() {
        if (isUserConnected) {
            showConnectionPopup("目前網路連線不穩定，請稍後再嘗試。");
            disconnectTime = Date.now();
            isUserConnected = false;

            reconnectTimer = setTimeout(function() {
                showConnectionPopup("服務已結束，由於您超過 15 分鐘未連線，請重新啟動對話。", true);
            }, 15 * 60 * 1000);
        }
    }

    function resetChatSystem() {
        window.location.reload();
    }

    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);

    if (!navigator.onLine) {
        handleOffline();
    }


    let inactivityTime = function () {
        let timeout;

        function resetTimer() {
            clearTimeout(timeout);
            timeout = setTimeout(showPopup, 15 * 60 * 1000);
        }

        function showPopup() {
            const overlay = document.createElement('div');
            overlay.style.position = 'fixed';
            overlay.style.top = '0';
            overlay.style.left = '0';
            overlay.style.width = '100%';
            overlay.style.height = '100%';
            overlay.style.backgroundColor = 'rgba(0, 0, 0, 0.7)';
            overlay.style.display = 'flex';
            overlay.style.justifyContent = 'center';
            overlay.style.alignItems = 'center';
            overlay.style.zIndex = '1000';

            const modal = document.createElement('div');
            modal.style.backgroundColor = 'white';
            modal.style.padding = '20px';
            modal.style.borderRadius = '5px';
            modal.style.textAlign = 'center';
            modal.style.boxShadow = '0 2px 10px rgba(0, 0, 0, 0.1)';

            const message = document.createElement('p');
            message.textContent = '服務已結束，由於您超過 15 分鐘未操作，請重新啟動對話。';
            modal.appendChild(message);

            const button = document.createElement('button');
            button.textContent = '確 認';
            button.style.marginTop = '20px';
            button.style.fontWeight = "700";
            button.style.padding = '7px 20px';
            button.onclick = function() {
                closePopup(overlay);
            };
            modal.appendChild(button);

            overlay.appendChild(modal);

            document.body.appendChild(overlay);

        }

        function closePopup(overlay) {
            document.body.removeChild(overlay);
            resetSystem();
        }

        function resetSystem() {
            window.location.reload();
        }

        window.onload = resetTimer;
        document.onmousemove = resetTimer;
        document.onkeyup = resetTimer;
        document.onkeydown = resetTimer;
        document.onclick = resetTimer;
        document.onscroll = resetTimer;
    };
    inactivityTime();

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
    fetch('/api/1.0/chat/routines?category=4')
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
        sendRequest();
    });

    messageInput.addEventListener('keydown',function(event){
        if(event.key === 'Enter'){
            event.preventDefault();
            sendRequest();
        }
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

    function sendRequest(){
        const userMessage = messageInput.value;
        if (userMessage === "" || userMessage === null) {
            return;
        }
        if (userMessage.length > 100){
            alert("已達提問字數上限，請重新輸入!")
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
    }



});