'use strict';
/**
 * @author Nemanja Ignjatov
 * 
 * File for defining constants needed in project
 */

var webApp = angular.module('cosyApp');

webApp.constant("COOKIE_KEY","cosyAppCookie");
webApp.constant("SUBJECT_CACHE_KEY","subjectCookie");
webApp.constant("MILLIS_IN_SECOND",1000);
webApp.constant("SECONDS_IN_MINUTE",60);
webApp.constant("MINUTES_IN_HOUR",60);
webApp.constant("SET_AUTHORIZATION_HEADER","Set-Authorization");
webApp.constant("AUTHORIZATION_HEADER","Authorization");

webApp.constant("LOGIN_STATE_NAME", "login");
webApp.constant("REGISTER_STATE_NAME", "register");
webApp.constant("DEVICETYPE_STATE_NAME", "deviceTypeState");

webApp.constant("FOG_NODES_STATE_NAME", "fogNodes");
webApp.constant("FATAL_ERROR_PAGE_NAME", "systemFailure");

webApp.constant("TRANSLATE_EVENT", "event:translate");
webApp.constant("MIN_MD_SIZE",768);
webApp.constant("EVENT_LESS_THAN_MD", "event:screen_less_than_md");
webApp.constant("EVENT_MORE_THAN_MD", "event:screen_more_than_md");

webApp.constant("RESOURCE_FOLDER", "resources/img/");

webApp.constant("ENDPOINT_IP_KEY","%%SRVIP%%");

