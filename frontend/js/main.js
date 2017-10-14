function onSubmit() {
    console.log("clicked");
    name = name.textContent;
    console.log(name);
    login.classList.add('hidden');

}

const login = document.querySelector('loginForm')
let name = document.querySelector('#name');
const submit = document.querySelector('#finish');
submit.addEventListener('onclick', onSubmit());
const info = document.querySelector('#info');
 