document.addEventListener('DOMContentLoaded', () => {

    // verify JwtToken
    const token = localStorage.getItem('jwtToken');
    if (token) {
        verifyJwtToken(token);
    }else{
        document.body.style.display = 'block';
    }
    header();
    passwordEyes();
    const emailInput = document.querySelector('input[name="email"]');
    const passwordInput = document.querySelector('input[name="password"]');
    const checkPasswordInput = document.querySelector('input[name="checkPassword"]');
    const captchaInput = document.querySelector('input[name="captcha"]');
    const registerButton = document.querySelector(`#registerButton`);
    const formatWarning = document.querySelector(`#formatWarning`);
    const passwordFormatWarning = document.querySelector(`#passwordFormatWarning`);
    const passwordLengthWarning = document.querySelector(`#passwordLengthWarning`);
    const passwordMatchWarning = document.querySelector(`#passwordMatchWarning`);

    emailInput.addEventListener('input', updateButtonState);
    passwordInput.addEventListener('input', updateButtonState);
    checkPasswordInput.addEventListener('input', updateButtonState);
    captchaInput.addEventListener('input', updateButtonState);

    document.querySelector('.btnBox a').addEventListener('click', () => {
        //check button status
        if (registerButton.classList.contains('btnLdisable')) {
            return;
        }
        //save user input format status
        let hasError = false;
        // check email format
        if (!isValidEmail(emailInput.value)) {
            formatWarning.style.display = "block";
            hasError = true;
        } else {
            formatWarning.style.display = "none";
        }
        // check password length
        if (!isValidLengthPassword(passwordInput.value)) {
            passwordLengthWarning.style.display = "block";
            hasError = true;
            return;
        } else {
            passwordLengthWarning.style.display = "none";
        }
        //check password format
        if (!isValidFormatPassword(passwordInput.value)) {
            passwordFormatWarning.style.display = "block";
            hasError = true;
        } else {
            passwordFormatWarning.style.display = "none";
        }
        //Check if the passwords match
        if (!isPasswordMatch(passwordInput.value, checkPasswordInput.value)) {
            passwordMatchWarning.style.display = "block";
            hasError = true;
        } else {
            passwordMatchWarning.style.display = "none";
        }

        if (hasError) {
            return;
        }

        fetch('/api/1.0/user/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: emailInput.value,
                password: passwordInput.value,
                provider: "native",
                captcha: captchaInput.value
            })
        })
            .then(response => {
                if (response.ok) {
                    window.location.href ="/account_result.html";
                } else if (response.status === 403) {
                    showPopUp("帳號已存在，請重新輸入。",403);
                } else if (response.status === 400) {
                    showPopUp("驗證碼錯誤，請重新輸入。",400);
                }
            })
            .catch(error => {
                showPopUp("系統維護中，請稍後再試。",500);
            })
    })


    function updateButtonState() {
        if (emailInput.value.trim() !== '' && passwordInput.value.trim() !== '' && captchaInput.value.trim() !== '' && checkPasswordInput.value.trim() !== '') {
            registerButton.classList.remove("btnLdisable");
            registerButton.classList.add("btnLfill");
        } else {
            registerButton.classList.remove("btnLfill");
            registerButton.classList.add("btnLdisable");
        }
    }

    function isPasswordMatch(password, checkPassword) {
        return password === checkPassword;
    }

    function isValidEmail(email) {
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailPattern.test(email);
    }

    function isValidLengthPassword(password) {
        return password.length >= 8;
    }

    function isValidFormatPassword(password) {
        const passwordPattern = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]+$/;
        return passwordPattern.test(password);
    }

    function showPopUp(message,status){
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
        popupText.textContent = message;

        const closeButton = document.createElement('button');
        closeButton.id = 'closeButton';
        closeButton.textContent = '確 認';
        closeButton.style.marginTop = '20px';
        closeButton.style.fontWeight = `700`;
        closeButton.style.padding = '5px 15px';
        closeButton.style.cursor = 'pointer';
        closeButton.style.transition = 'background-color 0.5s ease';

        closeButton.addEventListener('click', function() {
            closePopUp(status);
        });

        popupContent.appendChild(popupText);
        popupContent.appendChild(closeButton);

        popupOverlay.appendChild(popupContent);

        document.body.appendChild(popupOverlay);
    }
    function closePopUp(status){
        const popupOverlay = document.getElementById('popupOverlay');
        if (popupOverlay) {
            document.body.removeChild(popupOverlay);
        }
        if(status !== 400){
            location.reload();
        }
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
                localStorage.setItem("userInfo",data.email)
                window.location.href = "../chat.html";
            })
            .catch(error => {
                localStorage.removeItem("userInfo");
                localStorage.removeItem("jwtToken");
                localStorage.removeItem("userId");
                document.body.style.display = 'block';
            })
    }

});