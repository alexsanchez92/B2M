

app.controller('editCtrl', ["$scope", "$rootScope", "$translate", "UserLoggedFactory", "MyAccountService", "CountryService", "toaster", function ($scope, $rootScope, $translate, UserLoggedFactory, MyAccountService, CountryService, Toaster) {
    
    $scope.submitted = false;

    $scope.account = {
        submitted: false,
        user: angular.copy($rootScope.userLogged),
        submit: function (form) {
            this.submitted = true;

            var firstError=null;if(form.$invalid){var field=null,firstError=null;for(field in form)"$"!=field[0]&&(null!==firstError||form[field].$valid||(firstError=form[field].$name),form[field].$pristine&&(form[field].$dirty=!0));angular.element(".ng-invalid[name="+firstError+"]").focus();return;
            } 
            else {

                MyAccountService.editUser(this.user.email, this.user.prefix.id, this.user.phone, this.user.name)
                .then(function(response){
                    form.$setPristine(true);
                    $scope.account.submitted=false;
                    UserLoggedFactory.setUser($scope.account.user);
                }),
                function errorCallback(code) {
                };
            }
        },
        cancel: function(){
            this.user = angular.copy($rootScope.userLogged);
        }
    };

    $scope.password = {
        submitted: false,
        current: '',
        new: '',
        repeat: '',
        submit: function (form) {
            this.submitted = true;

            var firstError=null;if(form.$invalid){var field=null,firstError=null;for(field in form)"$"!=field[0]&&(null!==firstError||form[field].$valid||(firstError=form[field].$name),form[field].$pristine&&(form[field].$dirty=!0));angular.element(".ng-invalid[name="+firstError+"]").focus();return;
            } 
            else {

                MyAccountService.changePassword(CryptoJS.MD5(this.current).toString(), CryptoJS.MD5(this.new).toString())
                .then(function(response){
                    form.$setPristine(true);
                    $scope.password.submitted=false;
                    $scope.password.current='';
                    $scope.password.new='';
                    $scope.password.repeat='';
                }),
                function errorCallback(code) {
                };
            }
        },
        cancel: function(){
            this.submitted=false;
            this.current='';
            this.new='';
            this.repeat='';
        }
    };
                
    CountryService.getCountries()
    .then(function(resp){
        $scope.countries = resp.data;
    }),
    function errorCallback(response) {

    };
    
}]);