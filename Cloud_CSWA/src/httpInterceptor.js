'use strict';

var webApp = angular.module('cosyApp');

webApp.factory('appHttpInterceptor', [
	'$q',
	'$rootScope',
	'$log',
	'$filter',
	'UIService',
	'SessionExpirationFactory',
	'toastr',
	'FE_APP_CONFIGURATION',
	'SET_AUTHORIZATION_HEADER',
	'AUTHORIZATION_HEADER',
	function($q, $rootScope, $log, $filter, UIService,SessionExpirationFactory, toastr,
		FE_APP_CONFIGURATION, SET_AUTHORIZATION_HEADER,
		AUTHORIZATION_HEADER) {
	    return {

		// optional method
		'request' : function(config) {
		    // do something on success
		    if (!/.html/.test(config.url)
			    && !/.Pagination/.test(config.url)
			    && !/.json/.test(config.url)
			    && !/.pem/.test(config.url)
			    && !/http./.test(config.url)
			    && !/www./.test(config.url)) {
			config.url = $rootScope.RestURL
				+ config.url;
			var cookie = UIService.readSessionCookie(true);
			if (cookie) {
			    config.headers[AUTHORIZATION_HEADER] = cookie;
			    SessionExpirationFactory.restartSessionCountdown();
			}
			$log.debug("Intercepted URL " + config.url);
		    }
		    return config;
		},

		// optional method
		'response' : function(response) {
		    // do something on success
		    if (response.headers(SET_AUTHORIZATION_HEADER)) {
                UIService.cacheSubject(response.data);
		    	UIService.loginSuccessful(response
		    			.headers(SET_AUTHORIZATION_HEADER));
		    }
		    
		    return response;
		},

		// optional method
		'responseError' : function(rejection) {
		    if (rejection.status == 401) {
			// Removed restriction for development purposes,should
			// be enabled in prod environment
			var showToast = false;
			if(UIService.readSessionCookie(false) != null) {
			    showToast = true;
			}
			SessionExpirationFactory.clearSessionCountdown();
			UIService.logout(showToast,rejection.data.errorMessage);
		    } else {
			if (rejection.data) {
				toastr.error(rejection.data.errorMessage);
			}
		    }

		    return $q.reject(rejection);
		}
	    };
	} ]);
