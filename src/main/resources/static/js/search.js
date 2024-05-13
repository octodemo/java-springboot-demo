// Debounce function to limit the rate at which a function can fire.
function debounce(func, wait, immediate) {
    var timeout;
    return function() {
        var context = this, args = arguments;
        var later = function() {
            timeout = null;
            if (!immediate) func.apply(context, args);
        };
        var callNow = immediate && !timeout;
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
        if (callNow) func.apply(context, args);
    };
};

// Function to handle the search input and update the search results dynamically.
function handleSearchInput() {
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
}

// Attach the debounced event listener to the search input field.
document.getElementById('search').addEventListener('input', debounce(handleSearchInput, 2000));
