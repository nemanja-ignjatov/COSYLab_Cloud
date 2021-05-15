'use strict';

var webApp = angular.module('cosyApp');

webApp.service('fogNodeManagementService', ["$rootScope", "$log", "$filter", "$q", "$http", "lodash", "toastr", "md5", "localStorageService", "FE_APP_CONFIGURATION", "COOKIE_KEY",
    function ($rootScope, $log, $filter, $q, $http, lodash, toastr, md5, localStorageService, FE_APP_CONFIGURATION, COOKIE_KEY) {

        this.getAllFogNodes = function () {
            return $http.get("/tnta/fog/list");
        }

        this.registerFogNodeCredentials = function (request) {
            return $http.post("/tnta/fog/credentials/enter", [request]);
        }

        this.revokeFogNodeCertificate = function (fogNodeId) {
            return $http.post("/tnta/fog/certificate/revoke/"+fogNodeId, {});
        }

    }]);
