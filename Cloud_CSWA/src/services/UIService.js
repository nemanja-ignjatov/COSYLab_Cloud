/**
 * @author Nemanja Ignjatov
 */
'use strict';

var webApp = angular.module('cosyApp');

webApp.service('UIService', [ "$rootScope","$log","$filter","$q","lodash","toastr","localStorageService","FE_APP_CONFIGURATION","COOKIE_KEY","MILLIS_IN_SECOND",
    "LOGIN_STATE_NAME", "FOG_NODES_STATE_NAME", "RESOURCE_FOLDER", "AUTHORIZATION_HEADER", "SUBJECT_CACHE_KEY",
	function($rootScope, $log, $filter, $q, lodash, toastr, localStorageService, FE_APP_CONFIGURATION, COOKIE_KEY, MILLIS_IN_SECOND,
             LOGIN_STATE_NAME, FOG_NODES_STATE_NAME, RESOURCE_FOLDER, AUTHORIZATION_HEADER, SUBJECT_CACHE_KEY) {

	    this.saveItemToLocalStorage = function(key, val) {
	    	localStorageService.set(key, val);
	    }
	    
	    this.getItemFromLocalStorage = function(key) {
		return localStorageService.get(key);
	    }
	    
	    this.removeItemFromLocalStorage = function(key) {
		return localStorageService.remove(key);
	    }
	   
	    this._setSessionCookie = function(token){
			var cookie = {
				token : token,
				expirationMoment : new Date().getTime() + FE_APP_CONFIGURATION.FE_APP_COOKIE_DURATION*MILLIS_IN_SECOND
			};
			
			this.saveItemToLocalStorage(COOKIE_KEY,cookie);
			
		}

	    this._clearSessionCookie = function(){
	    	this.removeItemFromLocalStorage(COOKIE_KEY);
	    	this.removeItemFromLocalStorage(SUBJECT_CACHE_KEY);
	    }
	    
	    this.readSessionCookie = function(shouldRefresh) {
	    	var cookie = this.getItemFromLocalStorage(COOKIE_KEY);
			var retValue = null;
			if(cookie != null){
			    if(cookie.expirationMoment >= new Date().getTime()){//Cookie hasn't expired
				if(shouldRefresh){//If new expiration should be set
				    cookie.expirationMoment = new Date().getTime() + FE_APP_CONFIGURATION.FE_APP_COOKIE_DURATION*MILLIS_IN_SECOND;
				    this.saveItemToLocalStorage(COOKIE_KEY,cookie);
				}
				retValue = cookie.token;
			    } else {
				this.removeItemFromLocalStorage(COOKIE_KEY);
				this.removeItemFromLocalStorage(SUBJECT_CACHE_KEY);
			    } 
			}
			return retValue;
	    }
	    
	    this.cacheSubject = function(subject) {
	    	this.saveItemToLocalStorage(SUBJECT_CACHE_KEY,subject);
	    }
		
	    this.readSubjectCache = function() {
	    	return this.getItemFromLocalStorage(SUBJECT_CACHE_KEY);
	    }
	    
	    this.isGodSubject = function() {
	    	var subject = this.readSubjectCache();
	    	console.log(subject);
            if (subject != null && subject.accountRole == "GOD") {
                return true;
            }
	    	return false;
	    }
	    
	    this.loginSuccessful = function(token){
	    	this._setSessionCookie(token);
            $rootScope.goToState(FOG_NODES_STATE_NAME);
	    }
	    
	    this.logout = function(showToast,message){
            this.removeItemFromLocalStorage(COOKIE_KEY);
            this.removeItemFromLocalStorage(SUBJECT_CACHE_KEY);
            $rootScope.appOpportunityConfiguration = null;
            $rootScope.sessionData = null;
            $rootScope.authzPermissions = null;

            $rootScope.goToState(LOGIN_STATE_NAME);
            if (showToast == true) {
                toastr.error(message);
            }
	    }
	    
	    this.cleanSessionData = function (){
            this.removeItemFromLocalStorage(COOKIE_KEY);
            this.removeItemFromLocalStorage(SUBJECT_CACHE_KEY);

            $rootScope.sessionData = null;
	    }
	    
	    this.shouldRender = function(viewName,divName){
	    	//if name of the div is listed to render in buildConstants.json for a specific view, show div on the page
			if($rootScope.viewsConfiguration[viewName].indexOf(divName) >= 0){
				return true;
			}
			return false;
	    }

    } ]);
