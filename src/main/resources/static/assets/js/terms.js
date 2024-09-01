$(document).ready(function () {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        verifyJwtToken(token);
    }else{
        window.history.back();
        return;
    }
    document.body.style.display = 'block';
    // Scroll到底部自動勾選checkbox、button可用
    $(".content").scroll(function () {
        maxScrollHeight = $(".content").prop("scrollHeight") - $(".content").height() - 0;
        if (maxScrollHeight - $(this).scrollTop() <= 50) {
            $("input#checkbox").prop("checked", true);
            $(".btn.disabled").removeClass("disabled");
        }
    });
    // 手動勾選checkbox，button可用
    $("input#checkbox").click(function () {
        if ($(this).is(":checked")) {
            $(".btn.disabled").removeClass("disabled");
        } else {
            $(".btn").addClass("disabled");
        }
    });

    const nextButton = document.querySelector(".termsBox a");
    const userId = localStorage.getItem('userId');

    nextButton.addEventListener('click',function (){
        if(!nextButton.classList.contains('disabled')){
            fetch(`api/1.0/user/update-authTime?userId=${userId}`)
                .then(response => {
                    if(response.ok){
                        window.location.href = '../chat.html';
                    }else{
                        throw new error("fail to update authTime");
                    }
                })
                .catch(error => {
                    localStorage.removeItem("jwtToken");
                    localStorage.removeItem("userEmail");
                    localStorage.removeItem("userId");
                    showPopUp();
                })
        }

    })

    function showPopUp(){
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
        popupText.textContent = '系統維護中，即將返回登入頁面，請稍後再試。';

        const closeButton = document.createElement('button');
        closeButton.id = 'closeButton';
        closeButton.textContent = '確 認';
        closeButton.style.marginTop = '20px';
        closeButton.style.fontWeight = `700`;
        closeButton.style.padding = '5px 15px';
        closeButton.style.cursor = 'pointer';
        closeButton.style.transition = 'background-color 0.5s ease';

        closeButton.addEventListener('click', closePopUp);

        popupContent.appendChild(popupText);
        popupContent.appendChild(closeButton);

        popupOverlay.appendChild(popupContent);

        document.body.appendChild(popupOverlay);
    }

    function closePopUp(){
        const popupOverlay = document.getElementById('popupOverlay');
        if (popupOverlay) {
            document.body.removeChild(popupOverlay);
        }
        window.location.href = '../account_login.html';
    }

    function verifyJwtToken(token){
        fetch(`/api/1.0/user/solve-jwt?token=${token}`, {
            method: 'GET'
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error("Token is invalid or an error occurred during the validation.");
                }
            })
            .then(data => {
                if (data.authTime === null || data.authTime === "") {
                    return;
                }
                window.history.back();
            })
            .catch(error => {
                localStorage.removeItem("userInfo");
                localStorage.removeItem("jwtToken");
                localStorage.removeItem("userId");
                window.history.back();
            })
    }

});