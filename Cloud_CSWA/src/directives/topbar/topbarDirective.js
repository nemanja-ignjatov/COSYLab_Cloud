/**
 * @author Nemanja Ignjatov
 */
'use strict';

angular.module('cosyApp').directive('topbarDir', function() {

    var topbarController = ['$scope', '$rootScope', 'UIService', 'authService', 'SessionExpirationFactory',
        function ($scope, $rootScope, UIService, authService, SessionExpirationFactory) {
		
		$scope.logout = function(){
            authService.logout().then(function () {
                UIService.logout();
            });
		}

            $scope.fullName = UIService.readSubjectCache().accountName;
    }];
    	
    	
    
    return {
	controller: topbarController,
        templateUrl: 'html/topbar/topbar.html'
    };
});