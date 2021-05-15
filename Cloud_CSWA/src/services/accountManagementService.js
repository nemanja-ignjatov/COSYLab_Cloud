'use strict';

var webApp = angular.module('cosyApp');

webApp.service('accountManagementService', ["$rootScope", "$log", "$filter", "$q", "$http", "lodash", "toastr", "md5", "localStorageService", "FE_APP_CONFIGURATION", "COOKIE_KEY",
    function ($rootScope, $log, $filter, $q, $http, lodash, toastr, md5, localStorageService, FE_APP_CONFIGURATION, COOKIE_KEY) {

        this.register = function (accountName, password) {
            var pwHash = md5.createHash(password);
            return $http.post("/acam/account/register", {
                "accountName": accountName,
                "password": pwHash
            });
        }

	} ]);
