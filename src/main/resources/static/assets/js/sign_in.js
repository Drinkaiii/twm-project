document.addEventListener('DOMContentLoaded', () => {
    header();
    passwordEyes();
    const emailInput = document.querySelector('input[name="email"]');
    const passwordInput = document.querySelector('input[name="password"]');
    const captchaInput = document.querySelector('input[name="captcha"]');
    const loginButton = document.querySelector(`#loginButton`);
    const formatWarning = document.querySelector(`#formatWarning`);
    const passwordFormatWarning = document.querySelector(`#passwordFormatWarning`);
    const passwordLengthWarning = document.querySelector(`#passwordLengthWarning`);

    emailInput.addEventListener('input', () => {
        updateButtonState();
    });
    passwordInput.addEventListener('input', () => {
        updateButtonState();
    });
    captchaInput.addEventListener('input', () => {
        updateButtonState();
    });
    document.querySelector('.btnBox a').addEventListener('click',() => {

        if (loginButton.classList.contains('btnLdisable')) {
            return;
        }

        let hasError = false;

        // check email format
        if (!isValidEmail(emailInput.value)) {
            formatWarning.style.display = "block";
            hasError = true;
        }else{
            formatWarning.style.display = "none";
        }

        // check password length
        if (!isValidLengthPassword(passwordInput.value)) {
            passwordLengthWarning.style.display = "block";
            hasError = true;
            return;
        }else{
            passwordLengthWarning.style.display = "none";
        }

        //check password format
        if (!isValidFormatPassword(passwordInput.value)) {
            passwordFormatWarning.style.display = "block";
            hasError = true;
        }else{
            passwordFormatWarning.style.display = "none";
        }

        if (hasError) {
            return;
        }

        fetch('/api/1.0/user/signin',{
            method : 'POST',
            headers : {
                'Content-Type': 'application/json'
            },
            body : JSON.stringify({
                email : emailInput.value,
                password : passwordInput.value,
                provider : "native",
                captcha : captchaInput.value
            })
        })
            .then(response => {
                if (response.ok) {
                    window.location.href = '/chat.html';
                } else {
                    throw new Error('帳號、密碼或驗證碼錯誤');
                }
            })
            .catch( error => {
                showPopUp();
            })
    })

    function updateButtonState() {
        if (emailInput.value.trim() !== '' && passwordInput.value.trim() !== '' && captchaInput.value.trim() !== '') {
            loginButton.classList.remove("btnLdisable");
            loginButton.classList.add("btnLfill");
        } else {
            loginButton.classList.remove("btnLfill");
            loginButton.classList.add("btnLdisable");
        }
    }

    function isValidEmail(email) {
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailPattern.test(email);
    }

    function isValidLengthPassword(password) {
        return password.length > 8;
    }

    function isValidFormatPassword(password) {
        const passwordPattern = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]+$/;
        return passwordPattern.test(password);
    }

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
        popupText.textContent = '帳號、密碼或驗證碼錯誤';

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
        window.location.href = '/account_login.html';
    }
})