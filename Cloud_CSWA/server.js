var express = require("express");
var path = require('path');

var feApp = express();

var APP_PORT = 801;

feApp.use('/', express.static(path.join(__dirname, 'build')));

feApp.listen(APP_PORT, function () {
    console.log('Web Application running on port ' + APP_PORT);
});