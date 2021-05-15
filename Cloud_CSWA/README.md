# Frontend Application

This project represents implementation of a management tool for web site for COSY Projects at UNIVIE,Austria.
It is developed using AngularJS, while build and configuration scripts are supported via Gulp and NPM.
Also, e2e testing is provided using Protractor and Jasmine. 

## Getting Started

To get you started you can simply checkout project from GIT and install the dependencies. 

Before installing dependencies make sure you are on the root path of the project in command prompt.

### Install Dependencies

We have two kinds of dependencies in this project: tools and angular framework code.  The tools help
us manage and test the application.

* We get the tools we depend upon via `npm` - Node Package Manager.
* We get the angular code and libraries via `bower`, - client-side code package manager.

In order to install required tools, run :
```
npm install
```

This procedure may take some time. Once the tools are installed, run :
```
bower install
```
to install required libraries.

If your installation of tools and libraries was successful, following folders should be created in project :
* `node_modules` - contains the npm packages for the tools we need
* `app/bower_components` - contains the angular framework files

*Note that the `bower_components` folder would normally be installed in the root folder but
this location through the `.bowerrc` file.*

### Configuring and building the Application
File `buildConstants.json` is located on the root path of the project. All build and environment variables for application should be defined in that file, e.g. FE_APP_ENVIRONMENT,FE_APP_COOKIE_DURATION etc...These constants will be used during build and copied to the source, in order to be accessible from source code during development.

In order to build development version of the source code use value `dev` in `FE_APP_ENVIRONMENT`. This setting will produce source code "as is" in browser and you will be able to debug the code.Value `prod` will product minified and uglified code, which is intended for production deployment of the application.

Once you set up your build setting run 
```
gulp build 
```

This command will create folder `build` and deploy all needed packages and resource for running application in web server.

### Running the Application

There are two options for running application.
In the development mode, it is recommended to run application using command:
```
gulp 
```
This command will build the latest code, deploy it to build folder, setup watcher on file changes and start web server.
Watchers on file changes will automatically invoke build of application's parts once developer make changes and reload content of the application in browser.

Once you execute gulp, open browser and go to the URL : `http://localhost:9999`.

For the integration testing and production deployment, run the application with command :

```
npm run serve 
```

This command will start web server and serve content on port defined in `server.js` file in the variable `APP_PORT`. Default value is `801`. Once you start server, open browser and go to the URL : `http://localhost:801`


### Running tests

We have preconfigured the project with a protractor end to end testing framework. 
In order to run tests, open two command prompt windows and
* Start application in first window by executing
```
npm run serve
```
* Start tests execution by executing
```
npm run test
```
This command will start installation of the required dependencies for Protractor if they are not already installed, including webdriver. If you are behind corporate proxy, in order to install web driver, you will have to define proxy as environment variable for that terminal by invoking :
```
set HTTP_PROXY=http://10.128.1.37:8080;set HTTPS_PROXY=http://10.128.1.37:8080;
```
Protractor will automatically start browser and perform tests and generate report in terminal after the tests' execution.

## Source directory Layout

```
src/                            --> all of the source files for the application
    bower_components/           --> all bower libraries
    directives/                 --> angular directives
    pages/                      --> application views
        view1/
            view1.js
            view1.html
        view2/
            view2.js
            view2.html
        ...
        viewN/                  --> folder for a specific view
            viewN.html          --> HTML file for view
            viewN.js            --> View's controller
    less/                       --> folder for less files
    services/                   --> application services
    resources/                  --> application resource (images,fonts, i18n)
        font-awesome/           --> font-awesome library for glyphicons
        i18n/                   --> internationalization files for languages
        img/                    --> images
            flags/              --> state flags
            logos/              --> logos of the bank state departments
    app.js                      --> main application file
    config.js                   --> libraries and application configuration
    globals.js                  --> declaration of global variable and functions
    httpInterceptor.js          --> handling of the HTTP packages
    routes.js                   --> declaration of states and pages in the application
    index.html                  --> entry HTML file of the application
testReports/					--> folder for protractor test reports 
	htmlTestResults/			--> folder where protractor test results are generated in HTML format
		screenshots/			--> folder where are captured screenshots after each executed protractor test case
	xmlTestResults				--> folder where protractor test results are generated in XML format
tests/                          --> folder for protractor tests description and configuration
    protractor.conf.js          --> protractor configuration file
    view1/
        view1.pageObject.js   
        view1.test.js
    view2/
        view2.pageObject.js
        view2.test.js
    ...
    viewN/                      --> Folder for tests for a specific view
        viewN.pageObject.js     --> Description of HTML elements for testing
        viewN.test.js           --> test cases definition
```

##### src/directives/
This folder is intended for defining directives that will be used in project. Directive should be used to wrap certain components and it's functionality in order to avoid source code duplication across views.
##### src/pages/
This folder should contain all used view in the application. Every view should have it's own folder and content of that folder should be one HTML file for view and one JS file for view's controller.
##### src/less/
This folder should contain less files which will be compiled to CSS and executed in browser.
##### src/services/
This folder should contains JS files describing services used across views and controllers in the application.
##### src/resources/
This folder mustn't contain source code. It is a placeholder for all the resource used in project - i18n files, images, fonts, etc.


### Source directory maintenance
While developing, do not change structure of folder, unless it is agreed on the level of the team. This is important since build system is based on this folder structure, so any change in folders or folder names could cause serious malfunction of the application.
Exceptions from these rules are `src/pages` folder and `tests` folder, which are designed and integrated with build system in a way that allows new folder creation, renaming, deletion in those folders.
Make sure that Angular startup and configuration files (`app.js`,`config.js`,`globals.js`,`httpInterceptor.js`,`routes.js` are used as described and that functionalities don't mix between files, e.g. defining application constant in a file other than `globals.js`.
Every team member should use same code formatter, in order to avoid unnecessary conflicts.Please use built-in Eclipse JS code formatter, unless it is agreed to use other one.


If you have any complaint or comments, please refer to Nemanja - nemanja.ignjatov@univie.a.at

### Rules for development, building and committing source code
Please make sure that your version of source code is properly synchronized with GIT - updated and committed once needed. This will make your life easier.
Before committing, build application in a *production* mode a execute protractor tests. All tests should pass. If not, check if your changes caused any errors and consult other team member if needed.
Please commit only formatted code.
buildConstants.json file is designed to configure application on a specific server and therefore shouldn't be committed to SVN unless new configuration field is defined or any field is removed from file.

##### Enjoy your work!





