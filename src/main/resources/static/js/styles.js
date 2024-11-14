function addStyles() {
    let css = `
        body {
            background-color: #f2f2f2;
            font-family: Arial, sans-serif;
            padding: 0 15px;
            max-width: 1200px;
            margin: auto;
        }
        table {
            border-collapse: collapse;
            width: 100%;
            margin-top: 50px;
            box-shadow: 0px 0px 20px rgba(0,0,0,0.15);
            border-radius: 10px;
            overflow: hidden;
        }
        th, td {
            text-align: left;
            padding: 8px;
        }
        .actions {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .clear {
            color: #f44336;
        }
    `;

    let style = document.createElement('style');
    style.type = 'text/css';
    style.appendChild(document.createTextNode(css));
    document.head.appendChild(style);
}

addStyles();

let themeColors;
if (!window.enableSearchFeature) { // Swapped the condition to check for not enabled
    themeColors = {
        '--h1-color': window.searchFeatureColor || '#4CAF50',
        '--th-bg-color': '#2196F3',
        '--a-color': '#2196F3',
        '--tr-bg-color': '#c2e0fb',
    };
} else {
    themeColors = {
        '--h1-color': '#4CAF50',
        '--th-bg-color': '#4CAF50',
        '--a-color': '#4CAF50',
        '--tr-bg-color': '#fbfde3',
        '--button-color': '#4CAF50',
        '--button-hover-color': '#388E3C',
    };
}

// Apply the theme colors to the root of the document
Object.keys(themeColors).forEach((key) => {
    document.documentElement.style.setProperty(key, themeColors[key]);
});