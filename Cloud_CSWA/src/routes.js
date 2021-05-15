'use strict';

var cosyApp = angular.module('cosyApp');

cosyApp.config(['$stateProvider', '$urlRouterProvider', 'LOGIN_STATE_NAME', 'REGISTER_STATE_NAME',
    'DEVICETYPE_STATE_NAME', 'FOG_NODES_STATE_NAME', 'FATAL_ERROR_PAGE_NAME',
    function ($stateProvider, $urlRouterProvider, LOGIN_STATE_NAME, REGISTER_STATE_NAME,
              DEVICETYPE_STATE_NAME, FOG_NODES_STATE_NAME, FATAL_ERROR_PAGE_NAME) {
			// For unmatched routes
        $urlRouterProvider.otherwise('/fogNodes');

			// Application routes
			$stateProvider.state(LOGIN_STATE_NAME, {
				url : '/login',
				views : buildPageWrapper("loginCtrl","html/login/login.html",false,false,true),
				restricted : false
			}).state(REGISTER_STATE_NAME, {
				url : '/register',
				views : buildPageWrapper("registerCtrl","html/register/register.html",false,false,true),
				restricted : false
			}).state(FATAL_ERROR_PAGE_NAME, {
				url : '/systemFailure',
				views : buildPageWrapper(null,"html/systemFailure/systemFailure.html",false,false,true),
				restricted : false
			}).state(DEVICETYPE_STATE_NAME, {
				url : '/deviceTypes',
				views : buildPageWrapper("deviceTypeCtrl","html/deviceTypes/deviceTypes.html",true,true,true),
				restricted : true
			}).state(FOG_NODES_STATE_NAME, {
				url : '/fogNodes',
				views : buildPageWrapper("fogNodesCtrl","html/fogNodes/fogNodes.html",true,true,true),
				restricted : true
			});
			
			// Builder of composite page, input controller and template for the page and if topbar,footer and sidebar should be included in page as directives
			function buildPageWrapper(pageController,pageTemplateUrl,addTopbar,addSidebar,addFooter) {
			    var retPageViews = {};
			    if(addTopbar == true){
				retPageViews["topbar"] = { template : '<div topbar-dir></div>' };
			    }
			    
			    if(addSidebar == true){
				retPageViews["sidebar"] = { template : '<div sidebar-dir></div>' };
			    }
			    
			    retPageViews["pageContent"] =  {
					controller : pageController,
					templateUrl : pageTemplateUrl,
				    };
			    
			    if(addFooter == true){
				retPageViews["footer"] = { template : '<div footer-dir></div>' };
			    }
			    return retPageViews;
			}
		}]);
