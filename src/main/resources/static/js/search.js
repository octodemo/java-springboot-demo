document.getElementById('searchForm').addEventListener('submit', function(e) {
    e.preventDefault();

    var input = document.getElementById('search').value;
    var resultsDiv = document.getElementById('searchResults');
    resultsDiv.innerHTML = 'Loading...';

    fetch('/search?q=' + encodeURIComponent(input))
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        resultsDiv.innerHTML = '';
        for (var i = 0; i < data.length; i++) {
            var item = data[i];
            var highlightedItem = item.replace(new RegExp(input, 'gi'), function(match) {
                return '<strong>' + match + '</strong>';
            });
            resultsDiv.innerHTML += '<p>' + highlightedItem + '</p>';
        }
    })
    .catch(error => {
        console.error('Error:', error);
        resultsDiv.innerHTML = 'An error occurred while performing the search.';
    });
});