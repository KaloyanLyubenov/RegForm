const logBackBtn = document.getElementById("back");
const logSubmitBtn = document.getElementById("submit");

document.getElementById("logo").addEventListener("click", mainMenu);
document.getElementById("register").addEventListener("click", register);

function mainMenu() {
    window.location.href = "../templates/index.html";
}

function register() {
    window.location.href = "../templates/register.html";
}

logBackBtn.addEventListener("click", close);

logSubmitBtn.addEventListener("click", login);

function login() {
    const emailOrUsername = document.getElementById("email-or-username").value;
    const password = document.getElementById("password").value;

    if(doChecks(emailOrUsername, password) === 0){
    
    const requestData = {
        emailOrUsername: emailOrUsername,
        password: password
    };



    console.log("Sending data:", requestData);

    fetch("http://localhost:8080/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(
            requestData
        )
    })
    .then(response => response.text())
    .then(data => {
        console.log(data);
        if(data === "Wrong credentials"){
            alert(data);
        }else{
            localStorage.setItem("jwtToken", data);
            window.location.href = "../templates/accountPage.html";
        }
    })
    .catch(error => console.error("Error", error));

    document.getElementById("email-or-username").value = " ";
    document.getElementById("password").value = "";
}
}

function close() {
    window.location.href = "../templates/index.html";
}

function doChecks(
    emailOrUsername,
    password
     ) 
{

    let errors = 0;

    let emailOrUsernameErr = document.getElementById("email-or-username-error");

    if(emailOrUsername === ""){  
        emailOrUsernameErr.style.display = "block";
        errors++;
    }else {
        emailOrUsernameErr.style.display = "none";
    }

    let passwordErr = document.getElementById("password-error");

    if(password === ""){  
        passwordErr.style.display = "block";
        errors++;
    }else {
        passwordErr.style.display = "none";
    }

    return errors;
}