function getForm(){
    initial.classList.add("hidden");
    form.classList.remove("hidden");
}

function onSubmit() {
    console.log("clicked");
    console.log(name.value);
    name = name.value;
    console.log(name);
    form.classList.add("hidden");
    fileOption();
};

function fileOption() {
    fileOpt.classList.remove('hidden');
    fileInput.addEventListener('click', createInfo);
}

function createInfo() {
    console.log("Creating info");
    const hello = document.querySelector('#hello');
    hello.innerHTML = '';
    const helloMessage = document.createElement('p');
    helloMessage.textContent = "Hello, " + name
        + ". Here is your financial security status:";
    hello.appendChild(helloMessage);

    fetch("../../output.txt")
    .then(function(response) {
        return response.text()
    }).then(function(body) {
            fileDisplayArea.textContent = body 
    });
    info.classList.remove('hidden');
}

const initial = document.querySelector('#initial');

initial.addEventListener('click', getForm);
const form = document.querySelector('#form');
let name = document.querySelector('#name');
const button = document.querySelector('#button');
button.addEventListener('click', onSubmit);
const fileOpt = document.querySelector('#fileOption');
const info = document.querySelector('#info');
let outcome;
const fileInput = document.getElementById('fileInput');
const fileDisplayArea = document.getElementById('fileDisplayArea');

 