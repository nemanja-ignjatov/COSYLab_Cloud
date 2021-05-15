angular.module('cosyApp').factory('SessionExpirationFactory', ['$log','$timeout','$rootScope','$filter','UIService','toastr','FE_APP_CONFIGURATION','MILLIS_IN_SECOND', 
                                                                   function($log,$timeout,$filter, $rootScope,UIService,toastr, FE_APP_CONFIGURATION,MILLIS_IN_SECOND) {
   
	
    	function clearSessionCountdownFunc() {
	    if($rootScope.sessionCountdownId != null){
		$timeout.cancel($rootScope.sessionCountdownId);
	    }
	}
	    
    	function checkSessionExpiration() {
	    var cookie = UIService.readSessionCookie(false);
	    if(cookie == null) {
		$log.warn("Session cookie expired");
		UIService.logout(true,"FE_APP_ERROR_UNAUTHORIZED_EXCEPTION");
	    }
	}
	    
    	function restartSessionCountdownFunc(){
    	    clearSessionCountdownFunc();
    	    //Schedule new check in tokenDuration + 1 sec, to be sure that token is not overriden in the meantime
	    $rootScope.sessionCountdownId = $timeout(checkSessionExpiration, FE_APP_CONFIGURATION.FE_APP_COOKIE_DURATION*MILLIS_IN_SECOND + MILLIS_IN_SECOND);
		
	}

    	return {
    	    restartSessionCountdown : restartSessionCountdownFunc,
    	    clearSessionCountdown : clearSessionCountdownFunc
    	}
}]);