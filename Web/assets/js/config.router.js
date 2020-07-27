'use strict';

/**
 * Config for the router
 */
app.config(['$stateProvider', '$urlRouterProvider', '$controllerProvider', '$compileProvider', '$filterProvider', '$provide', '$ocLazyLoadProvider', 'JS_REQUIRES',
function ($stateProvider, $urlRouterProvider, $controllerProvider, $compileProvider, $filterProvider, $provide, $ocLazyLoadProvider, jsRequires) {

    app.controller = $controllerProvider.register;
    app.directive = $compileProvider.directive;
    app.filter = $filterProvider.register;
    app.factory = $provide.factory;
    app.service = $provide.service;
    app.constant = $provide.constant;
    app.value = $provide.value;

    // LAZY MODULES

    $ocLazyLoadProvider.config({
        debug: false,
        events: true,
        modules: jsRequires.modules
    });

    // APPLICATION ROUTES
    // -----------------------------------
    // For any unmatched url, redirect to /b2m/index
    $urlRouterProvider.otherwise("/index");
    //
    // Set up the states
    $stateProvider.state('app', {
        url: "",
        templateUrl: "assets/views/index/app.html",
        resolve: loadSequence('modernizr', 'moment', 'angularMoment', 'uiSwitch', 'perfect-scrollbar-plugin', 'toaster', 'ngAside', 'vAccordion', 'sweet-alert', 'chartjs', 'tc.chartjs', 'oitozero.ngSweetAlert', 'chatCtrl', 'truncate', 'htmlToPlaintext', 'angular-notification-icons', 'getServices', 'loginCtrl', 'searchCtrl'),
        abstract: true
    }).state('app.index', {
        url: "/index",
        templateUrl: "assets/views/main.html",
        title: 'B2M',
        resolve: loadSequence('indexCtrl', 'spin', 'ladda', 'angular-ladda' )
    }).state('app.search', {
        url: "/search",
        templateUrl: "assets/views/search.html",
        title: 'B2M',
        resolve: loadSequence('indexCtrl', 'spin', 'ladda', 'angular-ladda' )
    }).state('app.found', {
        url: "/found/{id:int}",
        templateUrl: "assets/views/main.html",
        title: 'Found',
        resolve: loadSequence('indexCtrl', 'spin', 'ladda', 'angular-ladda' ),   
        controller: function($stateParams){
          $stateParams.id  //*** Exists! ***//
        },      
        resolve: {
          id: ['$stateParams', function($stateParams){
              return $stateParams.id;
          }]
        }
    }).state('app.lost', {
        url: "/lost/{id:int}",
        templateUrl: "assets/views/main.html",
        title: 'Lost',
        resolve: loadSequence('indexCtrl', 'spin', 'ladda', 'angular-ladda' ),   
        controller: function($stateParams){
          $stateParams.id  //*** Exists! ***//
        },      
        resolve: {
          id: ['$stateParams', function($stateParams){
              return $stateParams.id;
          }]
        }
    })
    
    .state('app.cookies', {
        url: "/politics/cookies",
        templateUrl: "assets/views/cookies.html",
        title: 'Cookies'
    })
    .state('app.privacy', {
        url: "/politics/privacy",
        templateUrl: "assets/views/privacy.html",
        title: 'Privacy'
    })


    $stateProvider.state('user', {
        url: "/user",
        templateUrl: "assets/views/user/app.html",
        resolve: loadSequence('modernizr', 'moment', 'angularMoment', 'uiSwitch', 'perfect-scrollbar-plugin', 'toaster', 'ngAside', 'vAccordion', 'sweet-alert', 'chartjs', 'tc.chartjs', 'oitozero.ngSweetAlert', 'chatCtrl', 'truncate', 'htmlToPlaintext', 'angular-notification-icons', 'ui.select', 'getServices', 'loginCtrl', 'searchCtrl'),
        abstract: true
    }).state('user.index', {
        url: "/index",
        templateUrl: "assets/views/main.html",
        title: 'B2M',
        resolve: loadSequence('indexCtrl', 'spin', 'ladda', 'angular-ladda' )
    }).state('user.objects', {
        url: "/my-objects",
        templateUrl: "assets/views/my-objects.html",
        title: 'B2M',
        resolve: loadSequence('myObjectsCtrl', 'spin', 'ladda', 'angular-ladda' )
    }).state('user.search', {
        url: "/search",
        templateUrl: "assets/views/search.html",
        title: 'B2M',
        resolve: loadSequence('indexCtrl', 'spin', 'ladda', 'angular-ladda' )
    }).state('user.edit', {
        url: "/edit",
        templateUrl: "assets/views/user/edit.html",
        title: 'B2M',
        resolve: loadSequence('editCtrl', 'spin', 'ladda', 'angular-ladda' )
    })

    // Login routes
	.state('login', {
	    url: '/login',
	    template: '<div ui-view class="fade-in-right-big smooth"></div>',
	    abstract: true
	}).state('login.signin', {
	    url: '/signin',
	    templateUrl: "assets/views/login_login.html"
	}).state('login.forgot', {
	    url: '/forgot',
	    templateUrl: "assets/views/login_forgot.html"
	}).state('login.registration', {
	    url: '/registration',
	    templateUrl: "assets/views/login_registration.html"
	}).state('login.lockscreen', {
	    url: '/lock',
	    templateUrl: "assets/views/login_lock_screen.html"
	});

    // Generates a resolve object previously configured in constant.JS_REQUIRES (config.constant.js)
    function loadSequence() {
        var _args = arguments;
        return {
            deps: ['$ocLazyLoad', '$q',
			function ($ocLL, $q) {
			    var promise = $q.when(1);
			    for (var i = 0, len = _args.length; i < len; i++) {
			        promise = promiseThen(_args[i]);
			    }
			    return promise;

			    function promiseThen(_arg) {
			        if (typeof _arg == 'function')
			            return promise.then(_arg);
			        else
			            return promise.then(function () {
			                var nowLoad = requiredData(_arg);
			                if (!nowLoad)
			                    return $.error('Route resolve: Bad resource name [' + _arg + ']');
			                return $ocLL.load(nowLoad);
			            });
			    }

			    function requiredData(name) {
			        if (jsRequires.modules)
			            for (var m in jsRequires.modules)
			                if (jsRequires.modules[m].name && jsRequires.modules[m].name === name)
			                    return jsRequires.modules[m];
			        return jsRequires.scripts && jsRequires.scripts[name];
			    }
			}]
        };
    }
}]);