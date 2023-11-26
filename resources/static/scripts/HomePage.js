const logBtn = document.getElementById("login-opt");
const regBtn = document.getElementById("register-opt");

document.getElementById("logo").addEventListener("click", mainMenu);
document.getElementById("register").addEventListener("click", register);
document.getElementById("login").addEventListener("click", login);

function mainMenu() {
    window.location.href = "../templates/index.html";
}

function register() {
    window.location.href = "../templates/register.html";
}

function login() {
    window.location.href = "../templates/login.html";
}

logBtn.addEventListener("click", function() {
    window.location.href = "../templates/login.html";
});

regBtn.addEventListener("click", function() {
    window.location.href = "../templates/register.html";
});


