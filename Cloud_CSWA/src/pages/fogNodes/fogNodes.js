'use strict';

angular.module('cosyApp').controller('fogNodesCtrl',
    ['$rootScope', '$scope', '$state', '$log', '$filter', '$uibModal', 'fogNodeManagementService', 'UIService', 'toastr', function ($rootScope, $scope, $state, $log, $filter, $uibModal, fogNodeManagementService, UIService, toastr) {

        $scope.filterName = "";
        $scope.isGodSubject = UIService.isGodSubject();

        fogNodeManagementService.getAllFogNodes()
            .then(function (data) {
                $scope.fogNodes = data.data;
            });


        $scope.registerFogNodeCredentials = function (fogNodeCredentials) {
            console.log(fogNodeCredentials);
            fogNodeManagementService.registerFogNodeCredentials(fogNodeCredentials)
                .then(function (data) {
                    toastr.success("Fog Node credentials entered.");
                });
        }

        $scope.openWhitelistFogNode = function () {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'whitelistFogNode.html',
                controller: 'whitelistFogNodeCtrl',
                resolve: {fn: {}}
            });

            modalInstance.result.then(function (fnToWhitelist) {
                $scope.registerFogNodeCredentials(fnToWhitelist);
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.revokedFogNode = function (fogNode) {
            fogNodeManagementService.revokeFogNodeCertificate(fogNode.id)
                .then(function (data) {
                    toastr.success("Fog Node revoked successfully");
                    fogNode.revokedAt = "Just now";
                });
        }
    }]);

angular.module('cosyApp').controller('whitelistFogNodeCtrl',['$uibModalInstance','$scope', 'fn', function ($uibModalInstance, $scope, fn) {

    $scope.fn = fn;
    $scope.ok = function () {
        $uibModalInstance.close($scope.fn);
    };
    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}]);
