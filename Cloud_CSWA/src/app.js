'use strict';

// Declare app level module which depends on views, and components
var cosyApp = angular.module('cosyApp', ['ui.bootstrap', 'ui.router', 'ui.select', 'ngSanitize',
                                                     'ngLodash','LocalStorageModule','cosyApp.buildConstants','toastr','ngMessages','flow','pageslide-directive', 'angular-md5']);

// filters used in application
cosyApp.filter('html', function ($sce) {
    return function (input) {
        return $sce.trustAsHtml(input);
    }
}).filter('slice', function () {
    return function (arr, start, end) {
        return arr.slice(start, end);
    };
});


