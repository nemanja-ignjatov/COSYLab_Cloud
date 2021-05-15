/**
 * @author Nemanja Ignjatov
 * 
 * File for defining global functions needed in project which will be accessible via $rootScope
 */

'use strict';
var webApp = angular.module('cosyApp');

webApp.run(['$rootScope','$log','$state','$q','$window','$location','lodash','toastr','UIService',
    'FE_APP_CONFIGURATION', "LOGIN_STATE_NAME", "FOG_NODES_STATE_NAME", "FATAL_ERROR_PAGE_NAME", "ENDPOINT_IP_KEY", "RESOURCE_FOLDER",
        "EVENT_LESS_THAN_MD","EVENT_MORE_THAN_MD","MIN_MD_SIZE","SUBJECT_CACHE_KEY",
    function($rootScope, $log, $state, $q, $window, $location, lodash,toastr, UIService, FE_APP_CONFIGURATION,
             LOGIN_STATE_NAME, FOG_NODES_STATE_NAME, FATAL_ERROR_PAGE_NAME, ENDPOINT_IP_KEY, RESOURCE_FOLDER, EVENT_LESS_THAN_MD,
             EVENT_MORE_THAN_MD, MIN_MD_SIZE, SUBJECT_CACHE_KEY) {

	console.log($location.host());
	// Global variables	
	$rootScope.language = {};
	
	$rootScope.sessionData = null;
	
	$rootScope.authzPermissions = null;
	
	$rootScope.failedConfigurationLoad = 0;
	if(FE_APP_CONFIGURATION.FE_APP_ENDPOINT.FE_APP_SERVER_IP.length == 0){
        $rootScope.RestURL = "";
	} else {
        $rootScope.RestURL = FE_APP_CONFIGURATION.FE_APP_ENDPOINT.FE_APP_REST_ENDPOINT.replace(ENDPOINT_IP_KEY, FE_APP_CONFIGURATION.FE_APP_ENDPOINT.FE_APP_SERVER_IP );
	}

	// Global functions
        $rootScope.saveItemToLocalStorage = function(key,val){
        	UIService.saveItemToLocalStorage(key,val);
        }
        
        $rootScope.printJsonObject = function(msg,obj){
        	$log.debug(msg + "\n " +JSON.stringify(obj,null,4));
        }
        
        $rootScope.goToState = function(stateName){
    	    $state.go(stateName);
        }
        
        
	// Global watchers and listeners
	$rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
		$log.info("Transition to state " + toState.name);
		
		var cookie = UIService.readSessionCookie(false);
		
		// Check if user has rights to access to that page
		verifyPageAccessRights(toState,toParams,event,cookie);


	});
   
	
	
	function verifyPageAccessRights(toState,toParams,event,cookie) {
	    var redirected = false;
	    if((toState.restricted == true) && ((cookie == null) || (UIService.isGodSubject() == false))){
		//If user wants to go to protected page and has no cookie, disable that
   		$log.warn("Redirecting to login");
   		event.preventDefault();
   		UIService.cleanSessionData();
   		if(UIService.isGodSubject() == false){
            toastr.error("Only Gods can access this application!");
    	}
   		$state.go(LOGIN_STATE_NAME);
   		redirected =  true;
	    } else if((toState.restricted == false) && (cookie != null) && (UIService.isGodSubject() == true)){
		//If user wants to go to login and has cookie, disable that
		$log.warn("Redirecting to homepage");
		event.preventDefault();
		$state.go(FOG_NODES_STATE_NAME);
		redirected =  true;
	    } else if(toState.name != FATAL_ERROR_PAGE_NAME && $rootScope.failedConfigurationLoad > FE_APP_CONFIGURATION.FE_APP_CONFIGURATION_LOAD_FAIL_LIMIT){
		$log.error("Redirecting to fatal error page");
		//if user wants to go to some other page, but system failure is detected, disable that
		event.preventDefault();
		UIService.cleanSessionData();
		$state.go(FATAL_ERROR_PAGE_NAME);
		redirected =  true;
	    }
	    return redirected;
	}

	$rootScope.screenWidth = $window.outerWidth;
	function handleScreenChange(){
	    if($rootScope.screenWidth < MIN_MD_SIZE) {
		$rootScope.$broadcast(EVENT_LESS_THAN_MD);
	    } else {
		$rootScope.$broadcast(EVENT_MORE_THAN_MD);
	    }
	}

	angular.element($window).bind('resize', function () {
	    $rootScope.screenWidth = $window.outerWidth;
	    handleScreenChange();
	});
	

}]);
