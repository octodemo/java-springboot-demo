document.getElementById('csvFile').addEventListener('change', function() {
    if (this.value) {
        document.getElementById('uploadForm').style.display = 'block';
    } else {
        document.getElementById('uploadForm').style.display = 'none';
    }
});

document.getElementById('uploadForm').addEventListener('submit', function(e) {
    e.preventDefault();

    var formData = new FormData();
    formData.append('file', document.getElementById('csvFile').files[0]);

    fetch('/import', {
        method: 'POST',
        body: formData
    }).then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.text();
    }).then(data => {
        console.log('File uploaded successfully: ' + data);
        // clear the file input and hide the form
        document.getElementById('csvFile').value = '';
        document.getElementById('uploadForm').style.display = 'none';

        // Wait for 1 second before reloading the page
        setTimeout(function() {
            location.reload();
        }, 1000);
    }).catch(error => {
        console.error('There has been a problem with your fetch operation: ', error);
        document.getElementById('errorMessage').textContent = 'Error: ' + error.message;
    });
});