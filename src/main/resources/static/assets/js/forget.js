//===========================================stack process===========================================

// get the elements
const button = document.querySelector("#sendEmailButton");
const input_email = document.querySelector(".input input");
const input_verify = document.querySelector(".inputHalf input");

// attach functions
button.addEventListener("click", submit);
input_email.addEventListener("input", checkButtonState);
input_verify.addEventListener("input", checkButtonState);

//initial setting
switchButton(false);// button
document.querySelector(".error .warning").style.display = "none";//email formation wrong message

//===========================================function===========================================

// submit the data to back-end
function submit() {

    if (button.classList.contains('btnLdisable')) {
        return;
    }

    // get user's email and captcha
    const email = input_email.value;
    const captcha = input_verify.value;

    // check email and show error message
    if (!isValidEmail(email)) {
        document.querySelector(".input .warning").style.display = "block";
        return;
    } else {
        document.querySelector(".input .warning").style.display = "none";
    }

    // disable button when button is clicked
    switchButton(false)

    // fetch api and check the captcha
    fetch(`/api/1.0/user/email/reset-password?email=${email}&captcha=${captcha}`, {method: "GET"})
        .then(response => {
            if (response.ok) {
                window.location.href = `/result_mail.html?result=${true}`;
            } else {
                document.querySelector(".error .warning").style.display = "block";//captcha is wrong
            }
        })
        .catch(error => {
            window.location.href = `/result_mail.html?result=${false}`;
        })
        .finally(() => {
            switchButton(true)
        });
};

// check the submit button status and switch it
function checkButtonState() {
    if (input_email.value.trim() !== "" && input_verify.value.trim() !== "")
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

// check email formation
function isValidEmail(email) {
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailPattern.test(email);
}

