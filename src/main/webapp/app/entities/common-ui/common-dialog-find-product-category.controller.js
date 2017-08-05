(function() {
    'use strict';

    angular
        .module('whatscoverApp')
        .controller('CommonDialogFindProductCategoryController', CommonDialogFindProductCategoryController);

    CommonDialogFindProductCategoryController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'InsuranceProduct', 'ProductCategory', 'ProductCategorySearch', 'paginationConstants', 'pagingParams', 'ParseLinks', '$state', 'AlertService', 'emitName'];

    function CommonDialogFindProductCategoryController ($timeout, $scope, $stateParams, $uibModalInstance, entity, InsuranceProduct, ProductCategory, ProductCategorySearch, paginationConstants, pagingParams, ParseLinks, $state, AlertService, emitName) {
        var vm = this;
        vm.loadPage = loadPage;
        vm.clear = clear;
        vm.choose = choose;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.search = search;
        vm.loadProductCategories = loadProductCategories;
        vm.searchQuery = pagingParams.search;
        vm.currentSearch = pagingParams.search;
        vm.cancel = cancel;
        
        loadProductCategories();

        function loadProductCategories () {
        	if (pagingParams.search) {
                ProductCategorySearch.query({
                    query: pagingParams.search,
                    page: pagingParams.page - 1,
                    size: vm.itemsPerPage,
                    sort: sort()
                }, onSuccess, onError);
            } else {
                ProductCategory.query({
                    page: pagingParams.page - 1,
                    size: vm.itemsPerPage,
                    sort: sort()
                }, onSuccess, onError);
            }
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.productCategories = data;
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }
        
        function transition() {
        	pagingParams.page = vm.page;
        	pagingParams.sort = vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc');
        	pagingParams.search = vm.currentSearch;
        	loadProductCategories();
        }

        function search(searchQuery) {
            if (!searchQuery){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = '_score';
            vm.reverse = false;
            vm.currentSearch = searchQuery;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'id';
            vm.reverse = true;
            vm.currentSearch = null;
            vm.transition();
        }
        
        function cancel(){
        	$uibModalInstance.dismiss('cancel');
        }

        function choose (id) {
        	ProductCategory.get({id: id},function (result) {
		 		$scope.$emit('whatscoverApp:' + emitName, result);
		 		$uibModalInstance.dismiss('cancel');
            });
        }

    }
})();
