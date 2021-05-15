'use strict';

angular.module('cosyApp').controller('loginCtrl',
		[ '$rootScope', '$scope', '$log','$filter', 'authService', 'toastr',function($rootScope, $scope, $log,$filter, authService, toastr) {
			$scope.login = function() {

				authService.login($scope.username, $scope.password)
                    .then(function () {
                    })
				.catch(function() {
					toastr.error("Username or Password are wrong!");
				})
				

			};
			

		} ]);