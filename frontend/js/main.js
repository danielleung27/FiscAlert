function onSubmit() {
    console.log("clicked");
};


const form = document.querySelector('#form');
console.log(form.textContent);
let name = document.querySelector('#name');
console.log(name.textContent);
const button = document.querySelector('#button');
console.log(button.textContent);
button.addEventListener('click', onSubmit);
const info = document.querySelector('#info');
 