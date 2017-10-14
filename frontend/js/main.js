function onSubmit() {
    console.log("clicked");
    console.log(name.value);
    name = name.value;
    console.log(name);
    form.classList.add("hidden");
    createInfo();
};

function createInfo() {
    console.log("Creating info");
    const hello = document.querySelector('#hello');
    const helloMessage = document.createElement('p');
    helloMessage.textContent = "Hello, " + name
        + ". Here is your financial security status";
    hello.appendChild(helloMessage);
    info.classList.remove('hidden');


}


const form = document.querySelector('#form');
let name = document.querySelector('#name');
const button = document.querySelector('#button');
button.addEventListener('click', onSubmit);
const info = document.querySelector('#info');

 