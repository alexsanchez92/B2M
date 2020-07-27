var app = angular.module('clipApp', ['clip-two']);
app.factory("UserLoggedFactory", ["$window", "$rootScope", "$localStorage", "$http", function($window, $rootScope, $localStorage, $http) {
  angular.element($window).on('storage', function(event) {
    if (event.key === 'userLogged') {
      $rootScope.$apply();
    }
  });

  return {
    setData: function(val) {
        $rootScope.userLogged = $localStorage.userLogged = val;
        $localStorage.isLogged = true;
        return this;
    },
    setUser: function(user){
        $rootScope.userLogged = $localStorage.userLogged = angular.copy(user);
    },
    getData: function() {
        return $localStorage.userLogged;
    },
    getUser: function() {
        return $localStorage.userLogged;
    },
    isLogged: function(){
        return $localStorage.isLogged;
    },
    logout: function(){
        $localStorage.$reset({
            isLogged: false
        });
        $rootScope.userLogged = undefined;
    }
  };
}]);

app.run(['$rootScope', '$state', '$stateParams', '$filter',
function ($rootScope, $state, $stateParams, $filter) {

    // Attach Fastclick for eliminating the 300ms delay between a physical tap and the firing of a click event on mobile browsers
    FastClick.attach(document.body);

    // Set some reference to access them from any scope
    $rootScope.$state = $state;
    $rootScope.$stateParams = $stateParams;

    // GLOBAL APP SCOPE
    // set below basic information
    $rootScope.app = {
        name: 'Back To Me', // name of your project
        author: 'Alex', // author's name or company name
        description: 'Back To Me App', // brief description
        version: '2.0', // current version
        year: ((new Date()).getFullYear()), // automatic current year (for copyright information)
        isMobile: (function () {// true if the browser is a mobile device
            var check = false;
            if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
                check = true;
            };
            return check;
        })(),
        layout: {
            isNavbarFixed: true, //true if you want to initialize the template with fixed header
            isSidebarFixed: true, // true if you want to initialize the template with fixed sidebar
            isSidebarClosed: false, // true if you want to initialize the template with closed sidebar
            isFooterFixed: true, // true if you want to initialize the template with fixed footer
            theme: 'oopp', // indicate the theme chosen for your project
            logo: 'assets/images/logo.png', // relative path of the project logo
        }
    };

    $rootScope.formatDate = function(date){
        return $filter('date')(new Date(date),'dd/MM/yyyy');
    };
}]);
// translate config
app.config(['$translateProvider',
function ($translateProvider) {

    // prefix and suffix information  is required to specify a pattern
    // You can simply use the static-files loader with this pattern:
    $translateProvider.useStaticFilesLoader({
        prefix: 'assets/i18n/',
        suffix: '.json'
    });

    // Since you've now registered more then one translation table, angular-translate has to know which one to use.
    // This is where preferredLanguage(langKey) comes in.
    $translateProvider.preferredLanguage('es_ES');

    // Store the language in the local storage
    $translateProvider.useLocalStorage();
    
    // Enable sanitize
    $translateProvider.useSanitizeValueStrategy('sanitize');

}]);
// Angular-Loading-Bar
// configuration
app.config(['cfpLoadingBarProvider',
function (cfpLoadingBarProvider) {
    cfpLoadingBarProvider.includeBar = true;
    cfpLoadingBarProvider.includeSpinner = false;

}]);

app.factory("HeadersFactory", ['$localStorage', function($localStorage) {
  return {
    getHeaders: function(params) {

        // save settings to local storage
        if (angular.isDefined($localStorage.userLogged)) {
            var token = $localStorage.userLogged.token;
        }
        else{
            var token = null;
        }

        var headers = {
            'Content-Type': undefined,
            //'api-hash': CryptoJS.HmacSHA1(JSON.stringify(params), $localStorage.userLogged.token).toString(),
            //'api-date': new Date().getTime(),
            'api-key':  token
        }
        return headers;
    }
  };
}]);