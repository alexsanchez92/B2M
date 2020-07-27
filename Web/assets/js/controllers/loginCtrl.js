'use strict';

app.controller('loginCtrl', ["$scope", "$state", "LoginService", "UserLoggedFactory", function ($scope, $state, LoginService, UserLoggedFactory) {

    $scope.user = {
        email: 'elena@elena.es',
        password: '123' 
    }

    $scope.login = {
        error:{
            notFound: false,
            error: false
        },
        submit: function(form){
                        
            this.error.notFound = false;
            this.error.error = false;

            LoginService.login($scope.user.email, CryptoJS.MD5($scope.user.password).toString())
            .then(function(response){
                UserLoggedFactory.setData(response.data);
                $state.go('user.index');
            },
            function errorCallback(code) {
                switch(code){
                    case 404:
                        $scope.login.error.notFound = true;
                    break;
                    default:
                        $scope.login.error.error = true;
                    break;
                }
            });
        }
    }

}]);

app.controller('logOutCtrl', ["$scope", "$state", "LoginService", "UserLoggedFactory", function ($scope, $state, LoginService, UserLoggedFactory) {

    $scope.logOut = {
        error:{
            notFound: false,
            error: false
        },
        logOut: function(form){

            this.error.notFound = false;
            this.error.error = false;

            LoginService.logOut()
            .then(function(response){
                UserLoggedFactory.logout();
                $state.go('app.index');
            },
            function errorCallback(code) {
                switch(code){
                    case 404:
                        $scope.logOut.error.notFound = true;
                    break;
                    default:
                        $scope.logOut.error.error = true;
                    break;
                }
            });
        }
    }

}]);