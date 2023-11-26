const regBackBtn = document.getElementById("back");
const regSubmitBtn = document.getElementById("submit");
document.getElementById("logo").addEventListener("click", mainMenu);
document.getElementById("login").addEventListener("click", login);

function mainMenu() {
    window.location.href = "../templates/index.html";
}

function login() {
    window.location.href = "../templates/login.html";
}

if (regBackBtn) {
    regBackBtn.addEventListener("click", close);
} else {
    console.error("Element with id 'back' not found in the DOM");
}

if (regSubmitBtn) {
    regSubmitBtn.addEventListener("click", register);
} else {
    console.error("Element with id 'register' not found in the DOM");
}

function register() {
    const firstName = document.getElementById("first-name").value;
    const lastName = document.getElementById("last-name").value;
    const username = document.getElementById("username").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const passwordCheck = document.getElementById("password-check").value;

    let errors = doChecks(firstName, lastName, username, email, password, passwordCheck);

    if(errors === 0){
        const requestData = {
            firstName: firstName,
            lastName: lastName,
            username: username,
            email: email,
            password: password
        };

        console.log("Sending data:", requestData);

        fetch("http://localhost:8080/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(
                requestData
            )
        })
        .then(response => response.text())
        .then(data => console.log(data))
        .catch(error => console.error("Error", error));
    }
}

function close() {
    window.location.href = "../templates/index.html";
}

function doChecks(
    firstName,
    lastName,
    username,
    email,
    password,
    passwordCheck
     ) 
{

    let errors = 0;  

    // First name check

    let firstNameError = document.getElementById("first-name-error");

    if(firstName === ""){  
        let firstNameInput = document.getElementById("first-name");

        firstNameInput.style.borderBottomLeftRadius = 0;
        firstNameInput.style.borderBottomRightRadius = 0;

        firstNameError.style.display = "block";
        errors++;
    }else {
        firstNameError.style.display = "none";
    }

    //Last name check

    let lastNameError = document.getElementById("last-name-error");

    if(lastName === ""){
        let lastNameInput = document.getElementById("last-name");
        
        lastNameInput.style.borderBottomLeftRadius = 0;
        lastNameInput.style.borderBottomRightRadius = 0;

        lastNameError.style.display = "block";
        errors++;
    }else {
        lastNameError.style.display = "none";
    }

    // Username check

    let usernameError = document.getElementById("username-error");

    if(username === ""){
        let usernameInput = document.getElementById("username");
        
        usernameInput.style.borderBottomLeftRadius = 0;
        usernameInput.style.borderBottomRightRadius = 0;

        usernameError.style.display = "block";
        errors++;
    }else {
        usernameError.style.display = "none";
    }


    // Email check

    let emailErrMSG = "";

    if(email === ""){
        emailErrMSG = "Field should have value";

    }else if(email.indexOf("@") === -1){       
        emailErrMSG = "Invalid email address";
    }

    let emailError = document.getElementById("email-error");

    if(emailErrMSG !== "") {
        let emailInput = document.getElementById("email");

        emailInput.style.borderBottomLeftRadius = 0;
        emailInput.style.borderBottomRightRadius = 0;

        emailError.style.display = "block";
        errors++;
        emailError.textContent = emailErrMSG;
    }else {
        emailError.style.display = "none";
    }

    //Password check

 
    let passwordErrMSG = "";

    if(password === "") {  
        passwordErrMSG = "Field should have value";
    }else{
        console.log("checking size");
        if (password.length < 8){
            passwordErrMSG = " at least 8 symbols";
        }
        console.log("checking for symbol")
        if(!hasSpecialSymbol(password)){
            if(passwordErrMSG !== ""){
                passwordErrMSG += ", ";
            }
            passwordErrMSG += " at least 1 special symbol";
        }
        if(!hasNumber(password)){
            if(passwordErrMSG !== ""){
                passwordErrMSG += ", ";
            }
            passwordErrMSG += " at least 1 number";
        }
        if(!hasUppercaseLetter(password)){
            if(passwordErrMSG !== ""){
                passwordErrMSG += ", ";
            }
            passwordErrMSG += " at least 1 uppercase letter";
        }

        if(passwordErrMSG !== ""){
            passwordErrMSG = "Password should contain" + passwordErrMSG;
        }
    }

    let passwordError = document.getElementById("password-error");

    if(passwordErrMSG !== ""){
        let passwordInput = document.getElementById("password");
        
        passwordInput.style.borderBottomLeftRadius = 0;
        passwordInput.style.borderBottomRightRadius = 0;

        passwordError.style.display = "block";
        errors++;

        passwordError.textContent = passwordErrMSG;
    }else{
        passwordError.style.display = "none";
    }

    //Password comparison 

    let passwordCheckError = document.getElementById("password-check-error");

    if(password !== passwordCheck){
        let passwordCheckInput = document.getElementById("password-check");
        
        passwordCheckInput.style.borderBottomLeftRadius = 0;
        passwordCheckInput.style.borderBottomRightRadius = 0;

        passwordCheckError.style.display = "block";
        errors++;

    }else{
        passwordCheckError.style.display = "none";
    }


    return errors;
}

function hasSpecialSymbol(password) {
    var specialSymbolRegex = /[!@#$%^&*()_+{}\[\]:;<>,.?~\\/-]/;

    return specialSymbolRegex.test(password);
}

function hasNumber(password) {
    var numberRegex = /\d/;

    return numberRegex.test(password);
}

function hasUppercaseLetter(password) {
    var uppercaseRegex = /[A-Z]/;

    return uppercaseRegex.test(password);
  }