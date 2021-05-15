/**
 * @author Nemanja Ignjatov
 */
'use strict';

angular.module('cosyApp').directive('sidebarDir', function() {
    
    var sidebarController = ['$scope', '$rootScope', 'UIService','SessionExpirationFactory','EVENT_LESS_THAN_MD',
	  function ($scope, $rootScope, UIService,SessionExpirationFactory,EVENT_LESS_THAN_MD) {
		
		$rootScope.sidebarVisible = true;
		$rootScope.toggleSidebar = function(){
		    $rootScope.sidebarVisible = !$rootScope.sidebarVisible;
		}
		
		/* on screen size change */
		$rootScope.$on(EVENT_LESS_THAN_MD, function(event, data){
			if($rootScope.sidebarVisible == true) {
			    $rootScope.toggleSidebar();
			}
		});
		console.log(UIService.readSubjectCache())
	    $scope.isGodSubject = UIService.isGodSubject();
    }];
    

    
    return {
	controller: sidebarController,
        templateUrl: 'html/sidebar/sidebar.html'
    };
});