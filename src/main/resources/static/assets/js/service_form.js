document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        verifyJwtToken(token);
    }else{
        window.location.href = '../account_login.html';
        return;
    }

    document.querySelector('.contact-form').addEventListener('submit', function(e) {
        e.preventDefault();
    });

    document.querySelector('button[type="submit"]').addEventListener('click',()=>{
        const name = document.querySelector('input[name="name"]').value;
        const email = document.querySelector('input[name="email"]').value;
        const description =  document.querySelector('textarea[name="message"]').value;

        if (!name || !email || !description) {
            return;
        }

        if (!isValidEmail(email)) {
            return;
        }
        fetch('/api/1.0/user/support-request', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                name: name,
                email: email,
                description:description
            })
        })
            .then(response => {
                if(response.ok){
                    showPopUp("感謝您的填寫與詢問，本公司將盡速請客服人員回覆您。")
                }else{
                    throw new Error("failed to send support request");
                }
            })
            .catch(error => {
                showPopUp("系統維護中，請稍後再試。")
            })
    })

    function isValidEmail(email) {
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailPattern.test(email);
    }

    function showPopUp(message) {
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
        popupContent.style.boxSizing = 'border-box';
        popupContent.style.display = 'flex';
        popupContent.style.flexDirection = 'column';
        popupContent.style.alignItems = 'center';

        const popupText = document.createElement('p');
        popupText.textContent = message;
        popupText.style.margin = '0 0 10px 0';

        const closeButton = document.createElement('button');
        closeButton.id = 'closeButton';
        closeButton.textContent = '確 認';
        closeButton.style.border = 'none';
        closeButton.style.backgroundColor = '#ff6600';
        closeButton.style.color = '#fff';
        closeButton.style.margin = '10px auto 0 auto';
        closeButton.style.fontWeight = '700';
        closeButton.style.padding = '8px 20px';
        closeButton.style.cursor = 'pointer';
        closeButton.style.transition = 'background-color 0.3s ease';
        closeButton.style.borderRadius = '4px';
        closeButton.style.fontSize = '16px';
        closeButton.style.width = 'auto';

        closeButton.addEventListener('mouseover', () => {
            closeButton.style.backgroundColor = '#e65c00';
        });

        closeButton.addEventListener('mouseout', () => {
            closeButton.style.backgroundColor = '#ff6600';
        });
        closeButton.addEventListener('click', closePopUp);

        popupContent.appendChild(popupText);
        popupContent.appendChild(closeButton);
        popupOverlay.appendChild(popupContent);
        document.body.appendChild(popupOverlay);
    }

    function closePopUp() {
        const popupOverlay = document.getElementById('popupOverlay');
        if (popupOverlay) {
            document.body.removeChild(popupOverlay);
        }
        location.reload();
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
                localStorage.setItem("userInfo",data.email);
                document.body.style.display = 'block';
            })
            .catch(error => {
                localStorage.removeItem("userInfo");
                localStorage.removeItem("jwtToken");
                localStorage.removeItem("userId");
                window.location.href = '../account_login.html';
            })
    }
});