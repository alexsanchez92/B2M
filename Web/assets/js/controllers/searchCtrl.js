'use strict';

/*app.controller('searchCtrl', ["$scope", "$rootScope", "$state", "$http", "$timeout", "$uibModal", "FoundService", "LostService", "toaster", function ($scope, $rootScope, $state, $http, $timeout, $uibModal, FoundService, LostService, Toaster) {

    $scope.search = {
        search: 'moto',
        type: 0,
        place: '',
        date: '',

        submit: function(){
            alert(JSON.stringify(this.search+ "   "+this.type));
            $state.go('^.search');
        }
    }

    $scope.loadSearch = function(style){

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

}]);*/

app.controller('searchCtrl', ["$scope", "$rootScope", "$state", "$http", "$timeout", "$uibModal", "SearchService", "toaster", function ($scope, $rootScope, $state, $http, $timeout, $uibModal, SearchService, Toaster) {

    $scope.showSearch = false;
    $scope.ldloading = {};

    $scope.changeShowSearch = function(){
        ($scope.showSearch)?$scope.showSearch = false:$scope.showSearch = true;
    }

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

    $scope.items = {
        data: [],
        count: 0,
        loading: false,
        loaded: false,
        page: 1,
        limit: 12
    }

    $scope.loadItems = function(style){

        $scope.ldloading[style.replace('-', '_')] = true;

        $scope.params = {search: $scope.search.search, type: $scope.search.type, page: $scope.items.page, limit: $scope.items.limit};

        if($scope.showSearch){
            if($scope.search.place)
                $scope.params.place = $scope.search.place;

            if($scope.search.date.start && $scope.search.date.end){
                $scope.params.startDate = $scope.search.date.start;
                $scope.params.endDate = $scope.search.date.end;
            }
        }

        SearchService.search($scope.params)
        .then(function(response){
            $scope.items.data = $scope.items.data.concat(response.data);
            $scope.items.count = response.count;
            $scope.items.loading = false;
            $scope.items.page++;
            $scope.ldloading[style.replace('-', '_')] = false;

            if($scope.items.data.length>=$scope.items.count)
                $scope.items.loaded = true;
        },
        function errorCallback(code) {
            $scope.items.loading = false;
            $scope.ldloading[style.replace('-', '_')] = false;
        });     
    }

    $scope.search = {
        search: '',
        type: 0,
        place: '',
        date: {
            start: '',
            end: ''
        },

        submit: function(){

            $scope.items.data = [];
            $scope.items.page = 1;
            $scope.items.limit = 12;
            $scope.items.loaded = false;

            $scope.params = {search: this.search, type: this.type, page: $scope.items.page, limit: $scope.items.limit};

            if($scope.showSearch){
                if(this.place)
                    $scope.params.place = this.place;

                if(this.date.start && this.date.end){
                    $scope.params.startDate = this.date.start;
                    $scope.params.endDate = this.date.end;
                }
            }

            SearchService.search($scope.params)
            .then(function(response){
                $scope.items.data = $scope.items.data.concat(response.data);
                $scope.items.count = response.count;
                $scope.items.loading = false;
                $scope.items.page++;;

                if($scope.items.data.length>=$scope.items.count)
                    $scope.items.loaded = true;
            },
            function errorCallback(code) {
                $scope.items.loading = false;
            });

        }
    }

}]);