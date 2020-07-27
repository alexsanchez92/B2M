'use strict';

app.controller('indexCtrl', ["$scope", "$http", "$timeout", "$uibModal", "LoginService", "FoundService", "LostService", function ($scope, $http, $timeout, $uibModal, LoginService, FoundService, LostService) {

    $scope.view = 'found';
    
    $scope.showModal = function(item, type){

        $scope.typeModal = type;

        var modalInstance = $uibModal.open({
            templateUrl: 'itemModal.html',
            controller: 'itemModalInsCtrl',
            size: 'lg',
            resolve: {
                item: function () {
                    return item;
                }
            }
        });

        /*modalInstance.result.then(function (selectedItem) {
            $scope.selected = selectedItem;
        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });*/
    }

    $scope.lost = {
        data: [],
        count: 0,
        loading: false,
        loaded: false,
        page: 1,
        limit: 12
    }
    $scope.found = {
        data: [],
        count: 0,
        loading: false,
        loaded: false,
        page: 1,
        limit: 12
    }


    FoundService.getFounds($scope.found.page, $scope.found.limit)
    .then(function(response){
        $scope.found.data = $scope.found.data.concat(response.data);
        $scope.found.count = response.count;
        $scope.found.loading = false;
        $scope.found.page++;

        if($scope.found.data.length>=$scope.found.count)
            $scope.found.loaded = true;
    },
    function errorCallback(code) {
        $scope.found.loading = false;
    });

    LostService.getLosts($scope.lost.page, $scope.lost.limit)
    .then(function(response){
        $scope.lost.data = $scope.lost.data.concat(response.data);
        $scope.lost.count = response.count;
        $scope.lost.loading = false;
        $scope.lost.page++;

        if($scope.lost.data.length>=$scope.lost.count)
            $scope.lost.loaded = true;
    },
    function errorCallback(code) {
        $scope.lost.loading = false;
    });


    $scope.ldloading = {};

    $scope.loadFounds = function(style){

        $scope.ldloading[style.replace('-', '_')] = true;

        FoundService.getFounds($scope.found.page, $scope.found.limit)
        .then(function(response){
            $scope.found.data = $scope.found.data.concat(response.data);
            $scope.found.count = response.count;
            $scope.found.loading = false;
            $scope.found.page++;
            $scope.ldloading[style.replace('-', '_')] = false;

            if($scope.found.data.length>=$scope.found.count)
                $scope.found.loaded = true;
        },
        function errorCallback(code) {
            $scope.found.loading = false;
            $scope.ldloading[style.replace('-', '_')] = false;
        });      
    }

    $scope.loadLosts = function(style){

        $scope.ldloading[style.replace('-', '_')] = true;

        LostService.getLosts($scope.lost.page, $scope.lost.limit)
        .then(function(response){
            $scope.lost.data = $scope.lost.data.concat(response.data);
            $scope.lost.count = response.count;
            $scope.lost.loading = false;
            $scope.lost.page++;
            $scope.ldloading[style.replace('-', '_')] = false;

            if($scope.lost.data.length>=$scope.lost.count)
                $scope.lost.loaded = true;
        },
        function errorCallback(code) {
            $scope.lost.loading = false;
            $scope.ldloading[style.replace('-', '_')] = false;
        });        
    }

}]);

app.controller('itemModalInsCtrl', ["$scope", "$uibModalInstance", "item", function ($scope, $uibModalInstance, item) {

    $scope.item = item;
    /*$scope.items = items;
    $scope.selected = {
        item: $scope.items[0]
    };*/

    $scope.ok = function () {
        //$uibModalInstance.close($scope.selected.item);
        alert(JSON.stringify(item))
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}]);

app.controller('cookiesCtrl', ["$scope", "$rootScope", "$localStorage", function ($scope, $rootScope, $localStorage) {

    if (angular.isDefined($localStorage.cookies))
        $scope.cookies=$localStorage.cookies;
    else
        $scope.cookies=false;
    
    $scope.acceptCookies= function () {
        $localStorage.cookies=true;
        $scope.cookies=true;
    };


}]);