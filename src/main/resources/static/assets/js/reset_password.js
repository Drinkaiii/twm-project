//===========================================stack process===========================================

// get the elements
const button = document.querySelector(".btnBox");
const input_password = document.querySelector(".origin input");
const input_password_check = document.querySelector(".check input");
const input_verify = document.querySelector(".veriCodeBox input");
const text_account = document.querySelector(".account div");

// get para
const urlParams = new URLSearchParams(window.location.search);
const token = urlParams.get("token");

// attach the functions
button.addEventListener("click", submit);
input_password.addEventListener("input", checkButtonState);
input_password_check.addEventListener("input", checkButtonState);
input_verify.addEventListener("input", checkButtonState);

//initial setting
switchButton(false);// button
document.querySelector(".check .warning").style.display = "none";//password formation wrong message
document.querySelector(".input .notice").style.display = "none";//password check formation wrong message
document.querySelector(".captcha .warning").style.display = "none";//captcha formation wrong message
fetch(`/api/1.0/user/solve-jwt?token=${token}`, {method: "GET"})
    .then(response => response.json())
    .then(data => {
        let [name, domain] = data.email.split("@");
        let maskedName = name.slice(0, 3) + "*****";
        let maskedDomain = domain.slice(-4);
        text_account.textContent = `${maskedName}@*****${maskedDomain}`;
    });

//===========================================function===========================================
// submit the data to back-end
function submit() {

    // get the password and captcha
    const password_value = input_password.value;
    const password_check_value = input_password_check.value;
    const captcha = input_verify.value;

    // check password and show error message
    if (!isValidPassword(password_value)) {
        document.querySelector(".input .notice").style.display = "block";
        return;
    } else {
        document.querySelector(".input .notice").style.display = "none";
    }
    if (password_value !== password_check_value) {
        document.querySelector(".check .warning").style.display = "block";
        console.log("hi");
        return;
    } else {
        document.querySelector(".check .warning").style.display = "none";
    }

    // disable button when button is clicked
    switchButton(false)

    // fetch api and check the captcha
    fetch("/api/1.0/user/reset-password", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({
            newPassword: password_value,
            resetPasswordToken: token,
            captcha: captcha
        })
    })
        .then(response => {
            if (response.ok) {
                window.location.href = `/result_reset_password.html`;
            } else {
                document.querySelector(".captcha .warning").style.display = "block";//captcha is wrong
            }
        })
        .catch(error => {
            console.error('Error:', error);
        })
        .finally(() => {
            switchButton(true)
        });
};

// check the submit button status and switch it
function checkButtonState() {
    if (input_password.value.trim() !== "" && input_password_check.value.trim() !== "" && input_verify.value.trim() !== "")
        switchButton(true)
    else
        switchButton(false)
}

// switch button status
function switchButton(isEnabled) {
    const button = document.querySelector(".btnBox a");
    if (isEnabled) {
        button.disabled = false;
        button.style.cursor = 'pointer';
        button.classList.remove("btnLdisable");
        button.classList.add("btnLfill");
    } else {
        button.disabled = true;
        button.style.cursor = 'not-allowed';
        button.classList.remove("btnLfill");
        button.classList.add("btnLdisable");
    }
}

// check password formation
function isValidPassword(password) {
    const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{8,}$/;
    return passwordPattern.test(password);
}