function onSubmit() {
    console.log("clicked");
};


const login = document.querySelector('#form');
console.log(login.textContent);
let name = document.querySelector('#name');
console.log(name.textContent);
const submit = document.querySelector('#button');
console.log(submit.textContent);
submit.addEventListener('onclick', onSubmit());
const info = document.querySelector('#info');
 