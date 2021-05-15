'use strict';

angular.module('cosyApp').controller('registerCtrl',
    ['$rootScope', '$scope', '$log', '$filter', 'accountManagementService', 'toastr', function ($rootScope, $scope, $log, $filter, accountManagementService, toastr) {
        var mediumRegex = new RegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{6,})");

			$scope.register = function() {
				if ($scope.password != $scope.password2) {
					toastr.error("Password and Password repeat are not the same!");
					return;
				}
                if (!mediumRegex.test($scope.password)) {
                    toastr.error("Password is not strong enough! Required is minimum 6 characters, 1 capital, 1 small letter and 1 number!");
                } else {

                    accountManagementService.register($scope.username, $scope.password)
                        .then(function () {
                            toastr.success("successfully registered!");
                        })
                        .catch(function () {
                            toastr.error("Registration failed!");
                        })
                }
			};
			
		} ]);