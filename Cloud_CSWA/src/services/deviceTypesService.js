'use strict';

var webApp = angular.module('cosyApp');

webApp.service('deviceTypesService', ["$rootScope", "$log", "$filter", "$q", "$http", "lodash", "toastr", "localStorageService", "FE_APP_CONFIGURATION", "COOKIE_KEY",
    function ($rootScope, $log, $filter, $q, $http, lodash, toastr, localStorageService, FE_APP_CONFIGURATION, COOKIE_KEY) {

        this.getAllDeviceTypes = function () {
            return $http.get("/acam/devicetype/list");
        }

        this.addDeviceType = function (deviceTypeName, serviceProvider, functionalities, versionData) {
            return $http.post("/acam/devicetype/create", {
                "deviceTypeName": deviceTypeName,
                "serviceProvider": serviceProvider,
                "functionalities": functionalities,
                "currentVersion": versionData
            });
        }

        this.removeDeviceType = function (deviceTypeId) {
            return $http.delete("/acam/devicetype/delete/" + deviceTypeId);
        }

        this.updateDeviceType = function (deviceTypeId, typeName, serviceProvider, functionalities, versionData) {
            return $http.post("/acam/devicetype/update", {
                "deviceTypeId": deviceTypeId,
                "typeName": typeName,
                "serviceProvider": serviceProvider,
                "functionalities": functionalities,
                "currentVersion": versionData
            });
        }

    }]);
