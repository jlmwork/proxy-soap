var page = require('webpage').create();
page.viewportSize = {
    width: 1100,
    height: 1000
};
page.open('http://localhost:8083/proxy-soap/ui', function() {
    page.render('example.png');
    phantom.exit();
});
