const fileInput = document.getElementById('fileInput');
const fileDisplayArea = document.getElementById('fileDisplayArea');

fileInput.addEventListener('change', function(e) {
    const file = fileInput.files[0];
    const textType = /text.*/;

    if (file.type.match(textType)) {
        const reader = new FileReader();

        reader.onload = function(e) {
            fileDisplayArea.textContent = reader.result;
        }

        reader.readAsText(file);	
    } else {
        fileDisplayArea.innerText = "File not supported!"
    }
});

