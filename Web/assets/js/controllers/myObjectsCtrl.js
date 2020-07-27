'use strict';

app.controller('myObjectsCtrl', ["$scope", "$rootScope", "$http", "$timeout", "$uibModal", "FoundService", "LostService", "toaster", function ($scope, $rootScope, $http, $timeout, $uibModal, FoundService, LostService, Toaster) {

    $scope.view = 'found';
    
    $scope.showModal = function(item){

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

    $scope.recover = function(item){

        FoundService.recover(item.id)
        .then(function(response){
            Toaster.success(item.title, 'Recuperado');
            item.status = 'recovered';
        },
        function errorCallback(code) {
            Toaster.error(item.title, 'Error');
        });
    }

    $scope.lost = {
        data: [],
        count: 0,
        loading: false,
        loaded: false,
        page: 1,
        limit: 12
    }
    $scope.recovered = {
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

    FoundService.getUserFounds($rootScope.userLogged.id, $scope.found.page, $scope.found.limit)
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

    LostService.getUserLosts($rootScope.userLogged.id, $scope.lost.page, $scope.lost.limit)
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

    LostService.getUserRecovered($rootScope.userLogged.id, $scope.recovered.page, $scope.recovered.limit)
    .then(function(response){
        $scope.recovered.data = $scope.recovered.data.concat(response.data);
        $scope.recovered.count = response.count;
        $scope.recovered.loading = false;
        $scope.recovered.page++;

        if($scope.recovered.data.length>=$scope.recovered.count)
            $scope.recovered.loaded = true;
    },
    function errorCallback(code) {
        $scope.lost.loading = false;
    });


    $scope.ldloading = {};

    $scope.loadFounds = function(style){

        $scope.ldloading[style.replace('-', '_')] = true;

        FoundService.getUserFounds($rootScope.userLogged.id, $scope.found.page, $scope.found.limit)
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

        LostService.getUserLosts($rootScope.userLogged.id, $scope.lost.page, $scope.lost.limit)
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

    $scope.loadRecovered = function(style){

        $scope.ldloading[style.replace('-', '_')] = true;

        LostService.getUserLosts($rootScope.userLogged.id, $scope.recovered.page, $scope.recovered.limit)
        .then(function(response){
            $scope.recovered.data = $scope.recovered.data.concat(response.data);
            $scope.recovered.count = response.count;
            $scope.recovered.loading = false;
            $scope.recovered.page++;
            $scope.ldloading[style.replace('-', '_')] = false;

            if($scope.recovered.data.length>=$scope.recovered.count)
                $scope.recovered.loaded = true;
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