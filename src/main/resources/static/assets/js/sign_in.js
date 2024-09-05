document.addEventListener('DOMContentLoaded', () => {
    // To determine whether an OAuth login redirection has occurred by checking if the URL contains a "code" parameter, and to load a loading animation accordingly.
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const code = urlParams.get('code');
    if (code !== null) {
        loading();
    }

    // verify JwtToken
    const token = localStorage.getItem('jwtToken');
    if (token) {
        verifyJwtToken(token);
    }else{
        document.body.style.display = 'block';
    }

    loadCaptchaImage();
    const twmLoginButton = document.querySelector(`#twmLoginButton`);
    twmLoginButton.addEventListener('click', () => {
        const redirectUri = "https://gfchats.com/account_login.html";
        window.location.href = `https://stage.oauth.taiwanmobile.com/MemberOAuth/authPageLogin?response_type=code&client_id=appworks&redirect_uri=${redirectUri}&state=appstate&prompt=&showLoginPage=Y`;
    })

    if (code !== null) {
        OAuthSignIn();
    }
    window.onload = loadUserEmail;
    const emailInput = document.querySelector('input[name="email"]');
    const passwordInput = document.querySelector('input[name="password"]');
    const captchaInput = document.querySelector('input[name="captcha"]');
    const loginButton = document.querySelector(`#loginButton`);
    const formatWarning = document.querySelector(`#formatWarning`);
    const passwordFormatWarning = document.querySelector(`#passwordFormatWarning`);
    const passwordLengthWarning = document.querySelector(`#passwordLengthWarning`);
    const captchaWarning = document.querySelector(`#captchaWarning`);

    function updateButtonState() {
        const isEmailValid = isValidEmail(emailInput.value);
        const isPasswordValid = isValidLengthPassword(passwordInput.value) && isValidFormatPassword(passwordInput.value);
        const isCaptchaValid = captchaInput.value.trim().length === 6;

        if (isEmailValid && isPasswordValid && isCaptchaValid) {
            loginButton.classList.remove("btnLdisable");
            loginButton.classList.add("btnLfill");
            loginButton.style.pointerEvents = 'auto';
        } else {
            loginButton.classList.remove("btnLfill");
            loginButton.classList.add("btnLdisable");
            loginButton.style.pointerEvents = 'none';
        }

    }

    emailInput.addEventListener('input', updateButtonState);
    passwordInput.addEventListener('input', updateButtonState);
    captchaInput.addEventListener('input', updateButtonState);

    updateButtonState();

    loginButton.addEventListener('click', () => {
        //check button status
        if (loginButton.classList.contains('btnLdisable')) {
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

        if (hasError) {
            return;
        }

        fetch('/api/1.0/user/signin', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: emailInput.value,
                password: passwordInput.value,
                provider: "native",
                captcha: captchaInput.value,
                captchaId: document.getElementById('captchaId').value
            })
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else if (response.status === 403) {
                    throw new Error('Forbidden');
                } else if (response.status === 400) {
                    throw new Error('BadRequest');
                } else {
                    throw new Error('LoginFailed');
                }
            })
            .then(data => {
                localStorage.setItem('jwtToken', data.accessToken);
                localStorage.setItem('userId', data.user.id);
                localStorage.setItem("userInfo",data.user.email);
                rememberEmail();
                if (data.user.authTime === null || data.user.authTime === "") {
                    window.location.href = '../terms.html';
                    return;
                }
                window.location.href = '../chat.html';
            })
            .catch(error => {
                if (error.message === "BadRequest") {
                    captchaWarning.style.display = "block";
                } else {
                    console.log(error.message);
                    showPopUp('帳號或密碼有誤，請重新輸入');
                }
            })
    })

    header();
    passwordEyes();
    updateButtonState();

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

    function OAuthSignIn(){
        fetch('/api/1.0/user/signin', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                accessToken: code,
                provider: "twm"
            })
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new error("fail to get access token");
                }
            })
            .then(data => {
                const jwtToken = data.accessToken;
                localStorage.setItem('jwtToken', jwtToken);
                const userId = data.user.id;
                localStorage.setItem('userId', userId);
                localStorage.setItem("userInfo",data.user.email);
                if (data.user.authTime === null || data.user.authTime === "") {
                    window.location.href = '../terms.html';
                    return;
                }
                window.location.href = '../chat.html';
            })
            .catch(error => {
                showPopUp('登入失敗，請重新登入。');
            })
    }

    function loading(){
        const overlay = document.createElement('div');
        overlay.style.position = 'fixed';
        overlay.style.top = '0';
        overlay.style.left = '0';
        overlay.style.width = '100%';
        overlay.style.height = '100%';
        overlay.style.backgroundColor = 'white';
        overlay.style.zIndex = '1000';
        overlay.style.display = 'flex';
        overlay.style.justifyContent = 'center';
        overlay.style.alignItems = 'center';
        overlay.innerHTML = '<img class="loading" src="./assets/commonElement/img/Icon_Loading.svg">';
        document.body.appendChild(overlay);
    }

    function rememberEmail() {
        const rememberMe = document.getElementById('checkbox').checked;
        if (rememberMe) {
            localStorage.removeItem('userEmail', emailInput.value);
            localStorage.setItem('userEmail', emailInput.value);
        } else {
            localStorage.removeItem('userEmail');
        }
    }

    function loadUserEmail() {
        const userEmail = localStorage.getItem('userEmail');
        if (userEmail) {
            emailInput.value = userEmail;
            document.getElementById('checkbox').checked = true;
        }
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

        const popupText = document.createElement('p');
        popupText.textContent = message;

        const closeButton = document.createElement('button');
        closeButton.id = 'closeButton';
        closeButton.textContent = '確 認';
        closeButton.style.border = 'none';
        closeButton.style.backgroundColor = '#ff6600';
        closeButton.style.color = '#fff';
        closeButton.style.marginTop = '20px';
        closeButton.style.fontWeight = '700';
        closeButton.style.padding = '8px 20px';
        closeButton.style.cursor = 'pointer';
        closeButton.style.transition = 'background-color 0.3s ease';
        closeButton.style.borderRadius = '4px';
        closeButton.style.fontSize = '16px';
        closeButton.style.width = 'auto';

        closeButton.addEventListener('click', closePopUp);

        closeButton.addEventListener('mouseenter', function() {
            closeButton.style.backgroundColor = '#e65c00';
        });

        closeButton.addEventListener('mouseleave', function() {
            closeButton.style.backgroundColor = '#ff6600';
        });

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
        window.location.href = '../account_login.html';
    }

    function loadCaptchaImage(){
        fetch('/captcha/image')
            .then(response =>{
                return response.json();
            })
            .then(data => {
                document.getElementById('captchaImage').src = data.captchaImage;
                document.getElementById('captchaId').value = data.captchaId;
            })
            .catch(error => {
                console.error('Error loading captcha:', error);
            });
    }
})
