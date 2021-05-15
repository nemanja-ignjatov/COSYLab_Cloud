'use strict';

var webApp = angular.module('cosyApp');

webApp.service('authService', ["$rootScope", "$log", "$filter", "$q", "$http", "md5", "lodash", "toastr", "localStorageService", "FE_APP_CONFIGURATION", "COOKIE_KEY",
    function ($rootScope, $log, $filter, $q, $http, md5, lodash, toastr, localStorageService, FE_APP_CONFIGURATION, COOKIE_KEY) {

        this.login = function (userName, password) {
            return $http.post("/acam/auth/login", {"accountName": userName, "password": password});
        }

        this.logout = function () {
            return $http.get("/acam/auth/logout", {});
        }

    }]);
