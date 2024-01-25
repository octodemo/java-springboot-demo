var h1Color, thBgColor, aColor, trBgColor;
var toggleButton = document.getElementById('toggle');

if (window.enableSearchFeature) {
    h1Color = '#2196F3';
    thBgColor = '#2196F3';
    aColor = '#2196F3';
    trBgColor = '#c2e0fb';
} else {
    h1Color = '#4CAF50';
    thBgColor = '#4CAF50';
    aColor = '#4CAF50';
    trBgColor = '#fbfde3';
}

document.write('<style>');
document.write(`
    table {
        border-collapse: collapse;
        width: 100%;
        color: #444; /* Add color to the text in the table */
    }
    h1 { 
        color: ${h1Color}; 
        text-shadow: 2px 2px #ccc; 
        text-align: center; 
        margin-top: 50px; 
    }
    th { 
        background-color: ${thBgColor}; 
        color: #fff; 
        padding: 10px;
    }
    td {
        padding: 10px;
    }
    a { 
        text-decoration: none; 
        color: ${aColor}; 
        padding: 5px 10px; 
        border-radius: 5px; 
        border: 1px solid ${aColor}; 
        box-shadow: 3px 3px 5px rgba(0, 0, 0, 0.3); // Add a box shadow
        transition: all 0.3s ease;
    }

    a:hover { 
        color: #fff; 
        background-color: ${aColor}; 
        box-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3); // Reduce the box shadow on hover
    }
    tr:nth-child(even) { 
        background-color: ${trBgColor}; 
    }
    @keyframes rowFadeIn {
        from { opacity: 0; }
        to { opacity: 1; }
    }
    tr {
        animation: rowFadeIn 1s ease;
    }
    th, td {
        border: 1px solid #ddd; // Add a border
        padding: 10px;
    }
    table {
        border-collapse: collapse; // Collapse the borders
        width: 100%; // Make the table full width
    }
    tr:hover {
        box-shadow: inset 0 0 20px rgba(0, 0, 0, 0.2); // Increase the spread radius and opacity
        transition: box-shadow 0.3s ease;
    }

    .main-table {
        width: 100%; // Set the width of the main table to take up the full width
        max-height: 6em; // Set a maximum height for the main table to match the height of 5 rows
        overflow-y: auto; // Add a scrollbar if the content exceeds the maximum height
        order: 1; // Make the main table appear first
    }

    .sales-table {
        width: 40%; // Set the width of the sales table
        max-height: 6em; // Set a maximum height for the sales table
        overflow-y: auto; // Add a scrollbar if the content exceeds the maximum height
        margin-top: 20px; // Add some margin to separate the sales table from the main table
        order: 2; // Make the sales table appear second
    }

    .hide {
        display: none;
    }
`);
document.write('</style>');