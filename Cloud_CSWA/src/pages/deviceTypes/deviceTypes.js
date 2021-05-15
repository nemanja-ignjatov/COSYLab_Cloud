'use strict';

angular.module('cosyApp').controller('deviceTypeCtrl',
    ['$rootScope', '$scope', '$state', '$log', '$filter', '$uibModal', 'deviceTypesService', 'UIService', 'toastr', function ($rootScope, $scope, $state, $log, $filter, $uibModal, deviceTypesService, UIService, toastr) {

        deviceTypesService.getAllDeviceTypes()
            .then(function (data) {
                $scope.deviceTypes = data.data.deviceTypes;
                console.log($scope.deviceTypes);
            });


        $scope.addDeviceType = function (deviceTypeName, serviceProvider, functionalities, version) {
            deviceTypesService.addDeviceType(deviceTypeName, serviceProvider, functionalities, version)
                .then(function (data) {
                    var deviceType = data.data;
                    $scope.deviceTypes.push(deviceType);
                    toastr.success("Device Type added successfully");
                });
        }


        $scope.removeDeviceType = function (deviceType) {
            deviceTypesService.removeDeviceType(deviceType.id)
                .then(function (data) {
                    toastr.success("Device Type removed successfully");
                    var idx = $scope.deviceTypes.indexOf(deviceType);
                    if (idx >= 0) {
                        $scope.deviceTypes.splice(idx, 1);
                    }
                });
        }

        $scope.updateDeviceType = function (deviceType) {
            console.dir(deviceType);
            deviceTypesService.updateDeviceType(deviceType.id, deviceType.typeName, deviceType.serviceProvider, deviceType.functionalities, deviceType.currentVersion)
                .then(function (data) {
                    var dt = $scope.deviceTypes.find(dt => dt.id === deviceType.id);
                    $scope.deviceTypes[$scope.deviceTypes.indexOf(dt)] = data.data;
                    toastr.success("Device Type updated successfully");
                });
        }

        $scope.openUpdateDeviceType = function (deviceType) {
            var modalInstance = $uibModal.open({
                animation: true,
                templateUrl: 'addDeviceType.html',
                controller: 'DeviceTypeModalCtrl',
                size: 'lg',
                resolve: {deviceType: deviceType}
            });

            modalInstance.result.then(function (deviceType) {
                $scope.updateDeviceType(deviceType);
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.openAddDeviceType = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                templateUrl: 'addDeviceType.html',
                size: 'lg',
                controller: 'DeviceTypeModalCtrl',
                resolve: {deviceType: {functionalities: [], currentVersion: {}}}
            });

            modalInstance.result.then(function (data) {
                console.dir(data);
                $scope.addDeviceType(data.typeName, data.serviceProvider, data.functionalities, data.currentVersion);
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };
        $scope.isGodSubject = UIService.isGodSubject();
    }]);

angular.module('cosyApp').controller('DeviceTypeModalCtrl', ['$scope', '$uibModalInstance', 'deviceType', function ($scope, $uibModalInstance, deviceType) {
    console.log(deviceType);
    $scope.deviceType = JSON.parse(JSON.stringify(deviceType));
    $scope.function = {};


    $scope.addDevFunctionality = function () {
        console.log($scope.function);
        $scope.deviceType.functionalities.push($scope.function);
        $scope.function = {};
    }

    $scope.removeDevFunctionality = function (func) {
        $scope.deviceType.functionalities.splice($scope.deviceType.functionalities.indexOf(func), 1);
    }

    $scope.ok = function () {
        $uibModalInstance.close($scope.deviceType);
    };
    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}]);