document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('jwtToken');
    //Verify whether the token is empty
    if(token){
        fetch("api/1.0/chat/routines",{
            method: 'GET',
                headers: {
                'Authorization': `Bearer ${token}`
            }
        })
            .then(response =>{
                if (response.ok){
                    //The screen will be displayed after confirming that the token is valid.
                    document.body.style.display = 'block';
                    return response.json();
                }else{
                    throw new Error("Token is invalid or an error occurred during the validation.");
                }
            })
            .then(data => {
                const response = data.data;
                if(Array.isArray(response) && response.length > 0){
                    response.forEach(item => {
                        const firstGroupButton = document.createElement('button');
                        firstGroupButton.className = 'btn';
                        firstGroupButton.style.width = '470px';
                        firstGroupButton.textContent = item.category;
                        firstGroupButton.setAttribute('dataId', item.id);
                        if( item.id === 4){
                            firstGroupButton.addEventListener('click',function () {
                                const buttons = document.querySelectorAll('.btn');
                                buttons.forEach(button => {
                                    button.style.display = 'none';
                                });
                                displayNormalQuestions();
                            });
                        }else{
                            firstGroupButton.addEventListener('click',function() {
                                const id = firstGroupButton.getAttribute('dataId');
                                fetch(`/api/1.0/chat/routines?category=${id}`,{
                                    method: 'GET',
                                    headers: {
                                        'Authorization': `Bearer ${token}`
                                    }
                                })
                                    .then(response => {
                                        if (response.ok){
                                            return response.json();
                                        }else{
                                            throw new Error("fail to get category info");
                                        }
                                    })
                                    .then(data => {
                                        const url = data.data;
                                        showPopUp(url);
                                    })
                                    .catch(error => {
                                        showErrorPopUp('很抱歉，該功能維護中，請稍後再試。');
                                    })
                            })
                        }
                        button_container.appendChild(firstGroupButton);
                    });
                    float.appendChild(float_content);
                }else{
                    float.style.display = "block";
                }
            })
            .catch(error =>{
                window.location.href = '../account_login.html';
            })
    }else {
        window.location.href = '../account_login.html';
    }

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
    addMessage(firstMessage,"default");

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
        button.style.border = 'none';
        button.style.backgroundColor = '#ff6600';
        button.style.color = '#fff';
        button.style.marginTop = '20px';
        button.style.fontWeight = '700';
        button.style.padding = '8px 20px';
        button.style.cursor = 'pointer';
        button.style.transition = 'background-color 0.3s ease';
        button.style.borderRadius = '4px';
        button.style.fontSize = '16px';
        button.style.width = 'auto';

        button.addEventListener('mouseover', () => {
            button.style.backgroundColor = '#e65c00';
        });

        button.addEventListener('mouseout', () => {
            button.style.backgroundColor = '#ff6600';
        });

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
            button.style.border = 'none';
            button.style.backgroundColor = '#ff6600';
            button.style.color = '#fff';
            button.style.marginTop = '20px';
            button.style.fontWeight = '700';
            button.style.padding = '8px 20px';
            button.style.cursor = 'pointer';
            button.style.transition = 'background-color 0.3s ease';
            button.style.borderRadius = '4px';
            button.style.fontSize = '16px';
            button.style.width = 'auto';

            button.addEventListener('mouseover', () => {
                button.style.backgroundColor = '#e65c00';
            });

            button.addEventListener('mouseout', () => {
                button.style.backgroundColor = '#ff6600';
            });

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


    function displayNormalQuestions() {
        fetch('/api/1.0/chat/routines?category=4', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error("fail to get category info");
                }
            })
            .then(data => {
                const response = data.data;
                const itemsPerPage = 8;
                let currentPage = 1;
                const totalPages = Math.ceil(response.length / itemsPerPage);

                function displayPage(page) {
                    button_container.innerHTML = '';
                    const start = (page - 1) * itemsPerPage;
                    const end = start + itemsPerPage;
                    const pageItems = response.slice(start, end);

                    pageItems.forEach(item => {
                        const button = document.createElement('button');
                        button.className = 'btn';
                        button.textContent = item.id + ". " + item.type_name;
                        button.style.width = '470px';
                        button.setAttribute('data-id', item.id);
                        button.addEventListener('click', function() {
                            const id = this.getAttribute('data-id');
                            floatBoxClose();
                            faqInfo(id);
                        });
                        button_container.appendChild(button);
                    });

                    addPaginationButtons();
                }

                function addPaginationButtons() {
                    const paginationContainer = document.createElement('div');
                    paginationContainer.className = 'pagination-container';

                    paginationContainer.style.display = 'flex';
                    paginationContainer.style.gap='10px';
                    paginationContainer.style.flexDirection = 'row';
                    paginationContainer.style.justifyContent = 'space-between';

                    if (currentPage > 1) {
                        const prevButton = document.createElement('button');
                        prevButton.className = 'btn';
                        prevButton.textContent = '上一頁';
                        prevButton.style.backgroundColor = '#ff6600';
                        prevButton.style.color = 'white';
                        prevButton.addEventListener('mouseover', () => {
                            prevButton.style.backgroundColor = '#e65c00';
                        });

                        prevButton.addEventListener('mouseout', () => {
                            prevButton.style.backgroundColor = '#ff6600';
                        });
                        prevButton.addEventListener('click', function() {
                            currentPage--;
                            displayPage(currentPage);
                        });
                        paginationContainer.appendChild(prevButton);
                    }

                    if (currentPage < totalPages) {
                        const nextButton = document.createElement('button');
                        nextButton.className = 'btn';
                        nextButton.style.backgroundColor = '#ff6600';
                        nextButton.style.color = 'white';
                        nextButton.textContent = '下一頁';
                        nextButton.addEventListener('mouseover', () => {
                            nextButton.style.backgroundColor = '#e65c00';
                        });

                        nextButton.addEventListener('mouseout', () => {
                            nextButton.style.backgroundColor = '#ff6600';
                        });
                        nextButton.addEventListener('click', function() {
                            currentPage++;
                            displayPage(currentPage);
                        });
                        paginationContainer.appendChild(nextButton);
                    }

                    button_container.appendChild(paginationContainer);
                }

                displayPage(currentPage);

                float.appendChild(float_content);
            })
            .catch(error => {
                prompt.textContent = "系統維護中，請點擊下方按鈕填寫客服表單。"
                const button = document.createElement('button');
                button.className = 'btn';
                button.textContent = "客服表單";
                button.addEventListener('click', function() {
                    window.location.href = 'https://twm-appworks.com/customer_service_form.html';
                });
                button_container.appendChild(button);
                float.appendChild(float_content);
            });
    }

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

    function addMessage(text, role,addTypingEffect = true) {
        const now = new Date();
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        const timeString = `${hours}:${minutes}`;
        const li = document.createElement('li');
        const dialog = document.createElement('div');
        dialog.className = "dialog";
        const content = document.createElement("div");
        content.className = "content";  // Clear any previous class
        const time = document.createElement("time");
        time.textContent = timeString;

        if (role === "user") {
            content.textContent = text;
            dialog.appendChild(content);
            dialog.appendChild(time);
            li.className = "msg msgRight";
            li.appendChild(dialog);
            messageContainer.appendChild(li);
        } else if (role === "chatgpt") {
            content.innerHTML = text;
            if (addTypingEffect) {
                content.classList.add("typing-effect");
            }
            dialog.appendChild(content);
            dialog.appendChild(time);
            li.className = "msg";
            const avatar = document.createElement("i");
            avatar.className = "avatar";
            li.appendChild(avatar);
            li.appendChild(dialog);
            messageContainer.appendChild(li);
            scrollDown();
            return content;
        } else if (role === "default") {
            content.innerHTML = text;
            dialog.appendChild(content);
            dialog.appendChild(time);
            li.className = "msg";
            const avatar = document.createElement("i");
            avatar.className = "avatar";
            li.appendChild(avatar);
            li.appendChild(dialog);
            messageContainer.appendChild(li);
        } else {
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
                linkButton.addEventListener("click", function () {
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
        fetch(`/api/1.0/chat/routines?type=${id}`,{
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
            .then(response => { return response.json(); })
            .then(data => {
                const response = data.data;
                addMessage(response,"robot");
            })
            .catch(error => {
                addMessage("系統異常，請稍後再試。","chatgpt",false);
            })
    }

    function faqAnswer(id){
        fetch(`/api/1.0/chat/routines?question=${id}`,{
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
            .then(response => { return response.json(); })
            .then(data => {
                const response = marked.parse(data.data);
                addMessage(response,"chatgpt",false);
            })
            .catch(error => {
                addMessage("系統異常，請稍後再試。","chatgpt",false);
            })
    }

    function sendRequest() {
        const userMessage = messageInput.value;
        if (userMessage === "" || userMessage === null) {
            return;
        }
        if (userMessage.length > 100){
            showErrorPopUp("已達提問字數上限，請重新輸入!");
            return;
        }
        addMessage(userMessage, "user");
        messageInput.value = "";

        const typingMessage = addMessage("", "chatgpt");

        // Send the message to the server
        fetch('/api/1.0/chat/agents', {
            method : 'POST',
            headers : {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body : JSON.stringify({
                userId : localStorage.getItem("userId"),
                sessionId : sessionId,
                question : userMessage
            })
        })
            .then(response => {
                if(response.ok){
                    return response.json();
                } else {
                    throw new Error("server error");
                }
            })
            .then(data => {
                const response = marked.parse(data.data);
                sessionId = data.sessionId;
                // Replace the "typing..." message with the actual response and remove typing effect
                typingMessage.innerHTML = response;
                typingMessage.classList.remove("typing-effect");
            })
            .catch(error => {
                console.error('Error:', error);
                typingMessage.innerHTML = "系統異常，請稍後再試。";
                typingMessage.classList.remove("typing-effect");
            });
    }

    function showPopUp(url) {
        const popupOverlay = document.createElement('div');
        popupOverlay.id = 'popupOverlay';
        popupOverlay.style.position = 'fixed';
        popupOverlay.style.top = '0';
        popupOverlay.style.left = '0';
        popupOverlay.style.width = '100%';
        popupOverlay.style.height = '100%';
        popupOverlay.style.backgroundColor = 'rgba(0, 0, 0, 0.5)';
        popupOverlay.style.display = 'flex';
        popupOverlay.style.justifyContent = 'center';
        popupOverlay.style.alignItems = 'center';
        popupOverlay.style.zIndex = '1000';

        const popupContent = document.createElement('div');
        popupContent.id = 'popupContent';
        popupContent.style.backgroundColor = 'white';
        popupContent.style.padding = '20px';
        popupContent.style.borderRadius = '8px';
        popupContent.style.boxShadow = '0 0 10px rgba(0, 0, 0, 0.25)';
        popupContent.style.maxWidth = '600px';
        popupContent.style.textAlign = 'center';

        const popupText = document.createElement('p');
        popupText.textContent = '即將前往 GeForce Now 官網';

        const trueButton = document.createElement('button');
        trueButton.id = 'trueButton';
        trueButton.textContent = '同 意';
        trueButton.style.border = 'none';
        trueButton.style.backgroundColor = '#ff6600';
        trueButton.style.color = '#fff';
        trueButton.style.margin = '10px 20px 0 auto';
        trueButton.style.fontWeight = '700';
        trueButton.style.padding = '8px 20px';
        trueButton.style.cursor = 'pointer';
        trueButton.style.transition = 'background-color 0.3s ease';
        trueButton.style.borderRadius = '4px';
        trueButton.style.fontSize = '16px';
        trueButton.style.width = 'auto';

        trueButton.addEventListener('mouseover', () => {
            trueButton.style.backgroundColor = '#e65c00';
        });

        trueButton.addEventListener('mouseout', () => {
            trueButton.style.backgroundColor = '#ff6600';
        });

        trueButton.addEventListener('click', function () {
            closePopUp(url, true);
        });

        const falseButton = document.createElement('button');
        falseButton.id = 'falseButton';
        falseButton.textContent = '不 同 意';
        falseButton.style.border = 'none';
        falseButton.style.backgroundColor = '#ff6600';
        falseButton.style.color = '#fff';
        falseButton.style.margin = '10px auto 0 auto';
        falseButton.style.fontWeight = '700';
        falseButton.style.padding = '8px 20px';
        falseButton.style.cursor = 'pointer';
        falseButton.style.transition = 'background-color 0.3s ease';
        falseButton.style.borderRadius = '4px';
        falseButton.style.fontSize = '16px';
        falseButton.style.width = 'auto';

        falseButton.addEventListener('mouseover', () => {
            falseButton.style.backgroundColor = '#e65c00';
        });

        falseButton.addEventListener('mouseout', () => {
            falseButton.style.backgroundColor = '#ff6600';
        });

        falseButton.addEventListener('click', function () {
            closePopUp(url, false);
        });

        popupContent.appendChild(popupText);
        popupContent.appendChild(trueButton);
        popupContent.appendChild(falseButton);

        popupOverlay.appendChild(popupContent);

        document.body.appendChild(popupOverlay);
    }

    function closePopUp(url, status) {
        const popupOverlay = document.getElementById('popupOverlay');
        if (popupOverlay) {
            document.body.removeChild(popupOverlay);
        }
        if (status) {
            console.log(url);
            window.location.href = url;
        }
    }

    function showErrorPopUp(errorMessage) {
        const popupOverlay = document.createElement('div');
        popupOverlay.id = 'popupOverlay';
        popupOverlay.style.position = 'fixed';
        popupOverlay.style.top = '0';
        popupOverlay.style.left = '0';
        popupOverlay.style.width = '100%';
        popupOverlay.style.height = '100%';
        popupOverlay.style.backgroundColor = 'rgba(0, 0, 0, 0.5)';
        popupOverlay.style.display = 'flex';
        popupOverlay.style.justifyContent = 'center';
        popupOverlay.style.alignItems = 'center';
        popupOverlay.style.zIndex = '1000';

        const popupContent = document.createElement('div');
        popupContent.id = 'popupContent';
        popupContent.style.backgroundColor = 'white';
        popupContent.style.padding = '20px';
        popupContent.style.borderRadius = '8px';
        popupContent.style.boxShadow = '0 0 10px rgba(0, 0, 0, 0.25)';
        popupContent.style.maxWidth = '600px';
        popupContent.style.textAlign = 'center';

        const popupText = document.createElement('p');
        popupText.textContent = errorMessage;

        const trueButton = document.createElement('button');
        trueButton.id = 'closeButton';
        trueButton.textContent = '確 認';
        trueButton.style.border = 'none';
        trueButton.style.backgroundColor = '#ff6600';
        trueButton.style.color = '#fff';
        trueButton.style.margin = '10px 10px 0 auto';
        trueButton.style.fontWeight = '700';
        trueButton.style.padding = '8px 20px';
        trueButton.style.cursor = 'pointer';
        trueButton.style.transition = 'background-color 0.3s ease';
        trueButton.style.borderRadius = '4px';
        trueButton.style.fontSize = '16px';
        trueButton.style.width = 'auto';

        trueButton.addEventListener('mouseover', () => {
            trueButton.style.backgroundColor = '#e65c00';
        });

        trueButton.addEventListener('mouseout', () => {
            trueButton.style.backgroundColor = '#ff6600';
        });

        trueButton.addEventListener('click', function () {
            closeErrorPopUp();
        });

        popupContent.appendChild(popupText);
        popupContent.appendChild(trueButton);
        popupOverlay.appendChild(popupContent);
        document.body.appendChild(popupOverlay);
    }

    function closeErrorPopUp() {
        const popupOverlay = document.getElementById('popupOverlay');
        if (popupOverlay) {
            document.body.removeChild(popupOverlay);
        }
    }

});