/**
 * @author Nemanja Ignjatov
 */
'use strict';

var webApp = angular.module('cosyApp');

webApp.config([ '$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('appHttpInterceptor');

} ]);

webApp.config([ '$logProvider','FE_APP_CONFIGURATION', function($logProvider,FE_APP_CONFIGURATION) {
    if(FE_APP_CONFIGURATION.FE_APP_ENVIRONMENT === 'prod'){
	    $logProvider.debugEnabled(false);
    }
} ]);


webApp.config(['localStorageServiceProvider', function(localStorageServiceProvider) {
    localStorageServiceProvider.setPrefix('cosyAppStorage');
} ]);

webApp.config(['toastrConfig',function(toastrConfig) {
    angular.extend(toastrConfig, {
      autoDismiss: false,
      containerId: 'toast-container',
      maxOpened: 3,    
      newestOnTop: true,
      positionClass: 'toast-top-right',
      target: 'body',
      closeButton: true,
      progressBar: true,
      timeOut: 3000,
    });
  }]);

webApp.config(['flowFactoryProvider','FE_APP_CONFIGURATION', function (flowFactoryProvider,FE_APP_CONFIGURATION) {
    flowFactoryProvider.defaults = {  
        permanentErrors: [404, 500, 501],
        maxChunkRetries: 1,
        chunkRetryInterval: 5000,
        simultaneousUploads: 4,
        singleFile: true
    };
    flowFactoryProvider.on('catchAll', function (event) {
	    if(FE_APP_CONFIGURATION.FE_APP_ENVIRONMENT != 'prod'){
		console.log('Pics upload event : '+ arguments);
	    }
    });
}]);