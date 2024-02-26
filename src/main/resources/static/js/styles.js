let themeColors;
if (window.enableSearchFeature) {
    themeColors = {
        '--h1-color': '#2196F3',
        '--th-bg-color': '#2196F3',
        '--a-color': '#2196F3',
        '--tr-bg-color': '#c2e0fb',
        '--a-color': '#2196F3',
    };
} else {
    themeColors = {
        '--h1-color': '#4CAF50',
        '--th-bg-color': '#4CAF50',
        '--a-color': '#4CAF50',
        '--tr-bg-color': '#fbfde3',
        '--button-color': '#4CAF50',
        '--button-hover-color': '#388E3C',
        '--a-color': '#4CAF50',
    };
}

// Apply the theme colors to the root of the document
Object.keys(themeColors).forEach((key) => {
    document.documentElement.style.setProperty(key, themeColors[key]);
});