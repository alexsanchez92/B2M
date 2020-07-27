'use strict';
/**
 * Clip-Two Main Controller
 */
app.controller('AppCtrl', ['$rootScope', '$scope', '$state', '$translate', '$localStorage', '$window', '$document', '$timeout', 'cfpLoadingBar', 'UserLoggedFactory',
function($rootScope, $scope, $state, $translate, $localStorage, $window, $document, $timeout, cfpLoadingBar, UserLoggedFactory) {

	// Loading bar transition
	// -----------------------------------
	var $win = $($window);

	$scope.$watch(UserLoggedFactory.isLogged, function (value, oldValue) {
		if (value===oldValue){}
	    else{
			//If Session is closed, redirect to login
	    	if(!UserLoggedFactory.isLogged()){
	    		$state.go('app.index');
	    	}
	    }
	}, true);

	$rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
		//start loading bar on stateChangeStart
		cfpLoadingBar.start();

	});
	$rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams) {

		//stop loading bar on stateChangeSuccess
		event.targetScope.$watch("$viewContentLoaded", function() {

			cfpLoadingBar.complete();
		});

		//If Session is closed, redirect to login
        if (!UserLoggedFactory.isLogged() && !$state.includes("app")) {
	    	$state.go('app.index');
        }

        //If user write an url from other profile, redirect to its profile
        /*if (UserLoggedFactory.isLogged()) {
        	switch($rootScope.userLogged.type){
        		case 'mall':
        			if(!$state.includes("mall"))
	    				$state.go('mall.dashboard');
        		break;
        		case 'store':
        			if(!$state.includes("stores"))
	    				$state.go('stores.dashboard');
        		break;
        		case 'retailer':
        			if(!$state.includes("retailers"))
	    				$state.go('retailers.dashboard');
        		break;
        		case 'our':
        			if(!$state.includes("admin"))
	    				$state.go('admin.dashboard');
        		break;
        	}
        }
        */

		// scroll top the page on change state
		$('#app .main-content').css({
			position : 'relative',
			top : 'auto'
		});
		
		$('footer').show();
		
		window.scrollTo(0, 0);

		if (angular.element('.email-reader').length) {
			angular.element('.email-reader').animate({
				scrollTop : 0
			}, 0);
		}

		// Save the route title
		$rootScope.currTitle = $state.current.title;
	});

	// State not found
	$rootScope.$on('$stateNotFound', function(event, unfoundState, fromState, fromParams) {
		//$rootScope.loading = false;
		console.log(unfoundState.to);
		// "lazy.state"
		console.log(unfoundState.toParams);
		// {a:1, b:2}
		console.log(unfoundState.options);
		// {inherit:false} + default options
	});

	$rootScope.pageTitle = function() {
		return $rootScope.app.name + ' - ' + ($rootScope.currTitle || $rootScope.app.description);
	};

	// save settings to local storage
	if (angular.isDefined($localStorage.layout)) {
		$scope.app.layout = $localStorage.layout;
	} else {
		$localStorage.layout = $scope.app.layout;
	}
	$scope.$watch('app.layout', function() {
		// save to local storage
		$localStorage.layout = $scope.app.layout;
	}, true);

	// save settings to local storage
	if (angular.isDefined($localStorage.userLogged)) {
		$rootScope.userLogged = $localStorage.userLogged;
	}

	//global function to scroll page up
	$scope.toTheTop = function() {

		$document.scrollTopAnimated(0, 600);

	};

	// angular translate
	// ----------------------

	$scope.language = {
		// Handles language dropdown
		listIsOpen : false,
		// list of available languages
		available : {
			'es_ES' : 'Español',
			'en_EN' : 'English',
			'it_IT' : 'Italiano',
			'de_DE' : 'Deutsch'
		},
		// display always the current ui language
		init : function() {
			var proposedLanguage = $translate.proposedLanguage() || $translate.use();
			var preferredLanguage = $translate.preferredLanguage();
			// we know we have set a preferred one in app.config
			$scope.language.selected = $scope.language.available[(proposedLanguage || preferredLanguage)];
		},
		set : function(localeId, ev) {
			$translate.use(localeId);
			$scope.language.selected = $scope.language.available[localeId];
			$scope.language.listIsOpen = !$scope.language.listIsOpen;
		}
	};

	$scope.language.init();

	// Function that find the exact height and width of the viewport in a cross-browser way
	var viewport = function() {
		var e = window, a = 'inner';
		if (!('innerWidth' in window)) {
			a = 'client';
			e = document.documentElement || document.body;
		}
		return {
			width : e[a + 'Width'],
			height : e[a + 'Height']
		};
	};
	// function that adds information in a scope of the height and width of the page
	$scope.getWindowDimensions = function() {
		return {
			'h' : viewport().height,
			'w' : viewport().width
		};
	};
	// Detect when window is resized and set some variables
	$scope.$watch($scope.getWindowDimensions, function(newValue, oldValue) {
		$scope.windowHeight = newValue.h;
		$scope.windowWidth = newValue.w;
		
		if (newValue.w >= 992) {
			$scope.isLargeDevice = true;
		} else {
			$scope.isLargeDevice = false;
		}
		if (newValue.w < 992) {
			$scope.isSmallDevice = true;
		} else {
			$scope.isSmallDevice = false;
		}
		if (newValue.w <= 768) {
			$scope.isMobileDevice = true;
		} else {
			$scope.isMobileDevice = false;
		}
	}, true);
	// Apply on resize
	$win.on('resize', function() {
		
		$scope.$apply();
		if ($scope.isLargeDevice) {
			$('#app .main-content').css({
				position : 'relative',
				top : 'auto',
				width: 'auto'
			});
			$('footer').show();
		}
	});
}]);
