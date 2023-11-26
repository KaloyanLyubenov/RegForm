document.addEventListener("DOMContentLoaded", initPage);
let details;

function initPage() {
    let jwtToken = localStorage.getItem("jwtToken");

    let url = "http://localhost:8080/users/" + jwtToken; 

    fetch(url, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
    .then(response => response.text())
    .then(data => {
        let datas = data.split(";");

        document.getElementById("fnl").textContent = datas[0];
        document.getElementById("lnl").textContent = datas[1];
        document.getElementById("unl").textContent = datas[2];
        document.getElementById("el").textContent = datas[3];

        details = datas;
    })
    .catch(error => console.error("Error", error));
}


document.getElementById("fne").addEventListener("click", fnChange);

function fnChange() {
    document.getElementById("fnd").style.display = "none";
    let fni = document.getElementById("fni");
    
    fni.value = details[0];
    fni.style.display = "block";
}

document.getElementById("lne").addEventListener("click", lnChange);

function lnChange() {
    document.getElementById("lnd").style.display = "none";
    let lni = document.getElementById("lni");
    
    lni.value = details[1];
    lni.style.display = "block";
}

document.getElementById("une").addEventListener("click", unChange);

function unChange() {
    document.getElementById("und").style.display = "none";
    let uni = document.getElementById("uni");
    
    uni.value = details[2];
    uni.style.display = "block";
}

document.getElementById("ee").addEventListener("click", eChange);

function eChange() {
    document.getElementById("ed").style.display = "none";
    let ei = document.getElementById("ei");
    
    ei.value = details[3];
    ei.style.display = "block";
}

document.getElementById("apply").addEventListener("click", applyChanges);

function applyChanges() {
    let fn = document.getElementById("fni").value;
    let ln = document.getElementById("lni").value;
    let un = document.getElementById("uni").value;
    let e = document.getElementById("ei").value;
    let p = document.getElementById("pi").value;

    const requestData = {
        jwtToken: localStorage.getItem("jwtToken"),
        firstName: fn,
        lastName: ln,
        username: un,
        email: e,
        password: p 
    };

    fetch("http://localhost:8080/users", {
        method: "PUT",
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
        if(data !== "Internal Error" && data !== "Expired JWT token"){
            localStorage.setItem("jwtToken", data);
        }
    })
    .catch(error => console.error("Error", error));
}

document.getElementById("delete").addEventListener("click", deleteAccount);

function deleteAccount() {

    let jwtToken = localStorage.getItem("jwtToken");

    let url = "http://localhost:8080/users/" + jwtToken; 

    fetch(url, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json"
        }
    })
    .then(response => response.text())
    .then(data => {
        console.log(data)
        if(data === "Delete complete"){
            localStorage.clear;
            window.location.href = "../templates/index.html";
        }
    })
    .catch(error => console.error("Error", error));
}

document.getElementById("logout").addEventListener("click", logout);

function logout() {
    localStorage.clear;
    window.location.href = "../templates/index.html";
}