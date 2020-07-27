'use strict';

//Development
app.constant("urlApi","http://localhost/b2m/api/");
//Live
//app.constant("urlApi","http://localhost/apiop/");



//**************
//**************
//**************
//**************
//**************   LOSTS
//**************
//**************
//**************
//**************

app.service("LoginService", ['$q', '$http', '$filter', '$rootScope', "HeadersFactory", 'urlApi', function($q, $http, $filter, $rootScope, HeadersFactory, urlApi){

  var service = {
    login: function(email, password){
        var defered = $q.defer();
        var promise = defered.promise;

        var formData = new FormData();
        formData.append("email", email);
        formData.append("password", password);

        $http({
            method: 'POST', url: urlApi + 'login', data: formData, headers: HeadersFactory.getHeaders(null)/*, data: formData*/
        }).then(function successCallback(response) {
            if(response.status==200){
                defered.resolve(response.data);
            }else{
                defered.reject(response.status);
            }
        }, function errorCallback(response) {
            defered.reject(response.status);
        });
        return promise;
    },    
    logOut: function(){
        var defered = $q.defer();
        var promise = defered.promise;

        $http({
            method: 'POST', url: urlApi + 'logout', headers: HeadersFactory.getHeaders(null)/*, data: formData*/
        }).then(function successCallback(response) {
            if(response.status==200){
                defered.resolve(response.data);
            }else{
                defered.reject(response.status);
            }
        }, function errorCallback(response) {
            defered.reject(response.status);
        });
        return promise;
    }
  };
  return service;  
}]);

//**************
//**************
//**************
//**************
//**************   FOUNDS
//**************
//**************
//**************
//**************

app.service("FoundService", ['$q', '$http', '$filter', '$rootScope', 'urlApi', 'HeadersFactory', function($q, $http, $filter, $rootScope, urlApi, HeadersFactory){

  var service = {
    /*create: function(name, ids, status){
        var defered = $q.defer(); var promise = defered.promise;
        var formData = new FormData();
        formData.append("type", $rootScope.userLogged.type);

        switch($rootScope.userLogged.type){
            case 'mall':
                formData.append("type_id", $rootScope.userLogged.mall_id);
            break;
            case 'store':
                formData.append("type_id", $rootScope.userLogged.store_id);
            break;
        }

        formData.append("role_name", name);
        formData.append("ids", JSON.stringify(ids));
        formData.append("status", JSON.stringify(status));

        $http({
            method: 'POST', url: urlApi + 'save_role', headers: HeadersFactory.getHeaders(formData), data: formData
        }).then(function successCallback(resp) {
            defered.resolve(resp.data.success);
        }, function errorCallback(response) {
            defered.reject(response);
        });
        return promise;
    },
    edit: function(roleId, name, ids, status){
        var defered = $q.defer(); var promise = defered.promise;
        var formData = new FormData();
        formData.append("role_id", roleId);
        formData.append("type", $rootScope.userLogged.type);
        formData.append("type_id", $rootScope.userLogged.mall_id);
        formData.append("role_name", name);
        formData.append("ids", JSON.stringify(ids));
        formData.append("status", JSON.stringify(status));

        $http({
            method: 'POST', url: urlApi + 'save_role', headers: HeadersFactory.getHeaders(formData), data: formData
        }).then(function successCallback(response) {
            (response.data.success)? defered.resolve(response) : defered.reject(response);
        }, function errorCallback(response) {
            defered.reject(response);
        });
        return promise;
    },
    delete: function(roleId){
        var defered = $q.defer(); var promise = defered.promise;
        var formData = new FormData();
        formData.append("role_id", roleId);

        $http({
            method: 'POST', url: urlApi + 'delete_role', headers: HeadersFactory.getHeaders(formData), data: formData
        }).then(function successCallback(resp) {
            (resp.data.success)? defered.resolve(resp.data.success) : defered.reject(resp.data.data.error_msg);
        }, function errorCallback(response) {
            defered.reject("Error deleting role");
        });
        return promise;
    },*/
    getFound: function(id){
        var defered = $q.defer();
        var promise = defered.promise;

        $http({
            method: 'GET', url: urlApi + 'found/'+id/*, headers: HeadersFactory.getHeaders(formData), data: formData*/
        }).then(function successCallback(response) {
            if(response.data.success){
                defered.resolve(response.data);
            }else{
                defered.reject(response);
            }
        }, function errorCallback(response) {
            defered.reject(response);
        });
        return promise;
    },
    getFounds: function(page, limit){
        var defered = $q.defer();
        var promise = defered.promise;

        $http({
            method: 'GET', url: urlApi + 'found', params: {page: page, limit: limit}/*, headers: HeadersFactory.getHeaders(formData), data: formData*/
        }).then(function successCallback(response) {
            if(response.data.success){
                defered.resolve(response.data);
            }else{
                defered.reject(response);
            }
        }, function errorCallback(response) {
            defered.reject(response);
        });
        return promise;
    },
    getUserFounds: function(id, page, limit){
        var defered = $q.defer();
        var promise = defered.promise;

        $http({
            method: 'GET', url: urlApi + 'users/'+id+'/found', params: {page: page, limit: limit}/*, headers: HeadersFactory.getHeaders(formData), data: formData*/
        }).then(function successCallback(response) {
            if(response.data.success){
                defered.resolve(response.data);
            }else{
                defered.reject(response);
            }
        }, function errorCallback(response) {
            defered.reject(response);
        });
        return promise;
    },
    recover: function(id){
        var defered = $q.defer();
        var promise = defered.promise;

        $http({
            method: 'PUT', url: urlApi +'found/'+ id +'/recover', headers: HeadersFactory.getHeaders(null)/*, data: formData*/
        }).then(function successCallback(response) {
            if(response.status==200){
                defered.resolve(response.data);
            }else{
                defered.reject(response.status);
            }
        }, function errorCallback(response) {
            defered.reject(response.status);
        });
        return promise;
    }
  };
  return service;  
}]);

//**************
//**************
//**************
//**************
//**************   LOSTS
//**************
//**************
//**************
//**************

app.service("LostService", ['$q', '$http', '$filter', '$rootScope', 'urlApi', function($q, $http, $filter, $rootScope, urlApi){

  var service = {
    getLost: function(id){
        var defered = $q.defer();
        var promise = defered.promise;

        $http({
            method: 'GET', url: urlApi + 'lost/'+id/*, headers: HeadersFactory.getHeaders(formData), data: formData*/
        }).then(function successCallback(response) {
            if(response.data.success){
                defered.resolve(response.data);
            }else{
                defered.reject(response);
            }
        }, function errorCallback(response) {
            defered.reject(response);
        });
        return promise;
    },
    getLosts: function(page, limit){
        var defered = $q.defer();
        var promise = defered.promise;

        $http({
            method: 'GET', url: urlApi + 'lost', params: {page: page, limit: limit}/*, headers: HeadersFactory.getHeaders(formData), data: formData*/
        }).then(function successCallback(response) {
            if(response.data.success){
                defered.resolve(response.data);
            }else{
                defered.reject(response);
            }
        }, function errorCallback(response) {
            defered.reject(response);
        });
        return promise;
    },
    getUserLosts: function(id, page, limit){
        var defered = $q.defer();
        var promise = defered.promise;

        $http({
            method: 'GET', url: urlApi + 'users/'+id+'/lost', params: {page: page, limit: limit}/*, headers: HeadersFactory.getHeaders(formData), data: formData*/
        }).then(function successCallback(response) {
            if(response.data.success){
                defered.resolve(response.data);
            }else{
                defered.reject(response);
            }
        }, function errorCallback(response) {
            defered.reject(response);
        });
        return promise;
    },
    getUserRecovered: function(id, page, limit){
        var defered = $q.defer();
        var promise = defered.promise;

        $http({
            method: 'GET', url: urlApi + 'users/'+id+'/recover', params: {page: page, limit: limit}/*, headers: HeadersFactory.getHeaders(formData), data: formData*/
        }).then(function successCallback(response) {
            if(response.data.success){
                defered.resolve(response.data);
            }else{
                defered.reject(response);
            }
        }, function errorCallback(response) {
            defered.reject(response);
        });
        return promise;
    }
  };
  return service;  
}]);


//**************
//**************
//**************
//**************
//**************   SEARCH
//**************
//**************
//**************
//**************

app.service("SearchService", ['$q', '$http', '$filter', '$rootScope', 'urlApi', 'HeadersFactory', function($q, $http, $filter, $rootScope, urlApi, HeadersFactory){
  
  var service = {
    search: function(params){
        var defered = $q.defer();
        var promise = defered.promise;

        $http({
            method: 'GET', url: urlApi + 'search', params: params/*, headers: HeadersFactory.getHeaders(formData), data: formData*/
        }).then(function successCallback(response) {
            if(response.data.success){
                defered.resolve(response.data);
            }else{
                defered.reject(response);
            }
        }, function errorCallback(response) {
            defered.reject(response);
        });
        return promise;
    }
  };
  return service;  
}]);


//**************
//**************
//**************
//**************
//**************   MY ACCOUNT
//**************
//**************
//**************
//**************

app.service("MyAccountService", ['$q', '$http', '$filter', '$rootScope', 'urlApi', 'HeadersFactory', 'toaster', function($q, $http, $filter, $rootScope, urlApi, HeadersFactory, Toaster){
  
  var service = {
    editUser: function(email, prefix, phone, name){
        var defered = $q.defer(); var promise = defered.promise;

        var data = 'email='+email+'&prefix='+prefix+'&phone='+phone+'&name='+name;

        $http({
            method: 'PUT', url: urlApi + 'users/'+$rootScope.userLogged.id, headers: HeadersFactory.getHeaders(data), data: data
        }).then(function successCallback(response) {
            //Toaster.success($translate.instant('success'), $rootScope.userLogged.user.first_name+" "+$rootScope.userLogged.user.last_name+" updated"); 
            Toaster.success('Correcto', 'Usuario cambiado');
            (response.status==200)?defered.resolve(response):defered.reject(response.status);
        }, function errorCallback(response) {

            switch(response.status){
                case 404:
                    Toaster.error('Mal', 'El usuario no existe');
                break;
                case 409:
                    Toaster.error('Mal', 'Email y/o teléfono ya existe');
                break;
                default:
                    Toaster.error('Mal', 'Algo ocurrió mal');
                break;
            }
                   
            defered.reject(response.status);
        });
        return promise;
    },
    changePassword: function(currentPassword, newPass){
        var defered = $q.defer(); var promise = defered.promise;

        var data = 'password='+newPass+'&currentPassword='+currentPassword;

        $http({
            method: 'PUT', url: urlApi + 'users/'+$rootScope.userLogged.id+'/password', headers: HeadersFactory.getHeaders(data), data: data
        }).then(function successCallback(response) {
            //Toaster.success($translate.instant('success'), $rootScope.userLogged.user.first_name+" "+$rootScope.userLogged.user.last_name+" updated"); 
            Toaster.success('Correcto', 'contraseña cambiada');
            (response.status==200)?defered.resolve(response):defered.reject(response.status);
        }, function errorCallback(response) {

            switch(response.status){
                case 403:
                    Toaster.error('Mal', 'Contraseña actual mal');
                break;
                case 404:
                    Toaster.error('Mal', 'El usuario no existe');
                break;
                default:
                    Toaster.error('Mal', 'Algo ocurrió mal');
                break;
            }
                   
            defered.reject(response.status);
        });
        return promise;
    }
  };
  return service;  
}]);

//**************
//**************
//**************
//**************
//**************
//**************
//**************
//**************
//**************
app.service("CountryService", ['$q', '$http', '$filter', '$rootScope', "HeadersFactory", 'urlApi', function($q, $http, $filter, $rootScope, HeadersFactory, urlApi){

  var service = {
    getCountries: function(email, password){
        var defered = $q.defer();
        var promise = defered.promise;

        $http({
            method: 'GET', url: urlApi + 'countries', headers: HeadersFactory.getHeaders(null)/*, data: formData*/
        }).then(function successCallback(response) {
            if(response.status==200){
                defered.resolve(response.data);
            }else{
                defered.reject(response.status);
            }
        }, function errorCallback(response) {
            defered.reject(response.status);
        });
        return promise;
    }
  };
  return service;  
}]);

app.controller('DatepickerCtrl', ["$scope", "$log", function ($scope, $log) {
    $scope.today = function () {
        $scope.dt = new Date();
    };
    $scope.today();

    $scope.clear = function () {
        $scope.dt = null;
    };

    // Disable weekend selection
    $scope.disabled = function (date, mode) {
        return (mode === 'day' && (date.getDay() === 0 || date.getDay() === 6));
    };

    $scope.toggleMin = function () {
        $scope.minDate = $scope.minDate ? null : new Date();
    };
    $scope.toggleMin();
    $scope.maxDate = new Date(2030, 5, 22);
    $scope.maxToday = new Date();
    $scope.open = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();

        $scope.opened = !$scope.opened;
    };
    $scope.endOpen = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.startOpened = false;
        $scope.endOpened = !$scope.endOpened;
    };
    $scope.startOpen = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.endOpened = false;
        $scope.startOpened = !$scope.startOpened;
    };

    $scope.dateOptions = {
        formatYear: 'yy',
        startingDay: 1
    };

    $scope.formats = ['dd/MM/yyyy', 'yyyy-MM-dd', 'dd/MM', 'dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
    //$scope.format = $scope.formats[0];

    $scope.hstep = 1;
    $scope.mstep = 30;

    // Time Picker
    $scope.options = {
        hstep: [1, 2, 3],
        mstep: [1, 5, 10, 15, 25, 30]
    };

    $scope.ismeridian = true;
    $scope.toggleMode = function () {
        $scope.ismeridian = !$scope.ismeridian;
    };

    $scope.update = function () {
        var d = new Date();
        d.setHours(14);
        d.setMinutes(0);
        $scope.dt = d;
    };

    $scope.changed = function () {
        $log.log('Time changed to: ' + $scope.dt);
    };

    $scope.clear = function () {
        $scope.dt = null;
    };

}])