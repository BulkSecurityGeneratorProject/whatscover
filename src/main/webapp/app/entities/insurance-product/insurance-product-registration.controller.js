(function() {
    'use strict';

    angular
        .module('whatscoverApp')
        .controller('InsuranceProductRegistrationController', InsuranceProductRegistrationController);

    InsuranceProductRegistrationController.$inject = ['$timeout', '$scope', '$stateParams', 'entity', 'InsuranceProduct', 'InsuranceCompany', '$state', '$rootScope', 'InsuranceProductPremiumRate', 'InsuranceProductPremiumRateSearch', 'AlertService', 'paginationConstants', 'pagingParams', 'ParseLinks', 'ProductCategory', 'isReadOnly'];

    function InsuranceProductRegistrationController ($timeout, $scope, $stateParams, entity, InsuranceProduct, InsuranceCompany, $state, $rootScope, InsuranceProductPremiumRate, InsuranceProductPremiumRateSearch, AlertService, paginationConstants, pagingParams, ParseLinks, ProductCategory, isReadOnly) {
        var vm = this;
       
        vm.insuranceProduct = entity;
        vm.insuranceProductPremiumRates = [];
        vm.save = save;
        vm.childStateCompany = $state.current.parent + '.dialog-find-company';
        vm.childStateProductCategory = $state.current.parent + '.dialog-find-product-category';

        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.searchQuery = pagingParams.search;
        vm.currentSearch = pagingParams.search;
        
        vm.addRow = addRow;
        vm.removeRow = removeRow;
        vm.removedEntity = [];
        vm.onUpdateField = onUpdateField;
        vm.checkEmptyData = checkEmptyData;
        
        vm.cancel = cancel;
        vm.isReadOnly = isReadOnly;
        vm.isShowBtns = !isReadOnly;
        vm.isTab1 = $state.current.params.activeTabs.isTab1;
        vm.isTab2 = $state.current.params.activeTabs.isTab2;
        
        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
            if(vm.isTab1){
        		angular.element( document.querySelector( '#view-general' ) ).removeClass('hidden');
        		angular.element( document.querySelector( '#view-premium-rate' ) ).addClass('hidden');
        	}else{
        		angular.element( document.querySelector( '#view-general' ) ).addClass('hidden');
        		angular.element( document.querySelector( '#view-premium-rate' ) ).removeClass('hidden');
        	}
        });

        /**
         * save product
         */
        function save () {
         	vm.isValid = true;
        	vm.insuranceProductPremiumRates.every(function(data){
        		if(data.status === 'NEW'){
        			if(data.entryAge === null){
        				triggerEForm(data.id, data.index, "EntryAgeForm");
        				return false;
        			}else if(data.malePremiumRate === null){
        				triggerEForm(data.id, data.index, "MaleRateForm");
        				return false;
        			}else if(data.femalePremiumRate === null){
        				triggerEForm(data.id, data.index, "FemaleRateForm");
        				return false;
        			}
        		}
        		return true;
        	});
        	
        	if(vm.isValid){
        		vm.insuranceProductPremiumRates = $.grep(vm.insuranceProductPremiumRates, function(element, index){return (element.status !== "DELETE" || element.id !== null) && element.status !== "DEFAULT"});
        		console.log(vm.insuranceProductPremiumRates);
        		
        		vm.isSaving = true;
        		vm.insuranceProduct["premiumRates"] = vm.insuranceProductPremiumRates;
        		if (vm.insuranceProduct.id !== null) {
                    InsuranceProduct.updateInsuranceProduct(vm.insuranceProduct, onSaveSuccess, onSaveError);
                } else {
                    InsuranceProduct.saveInsuranceProduct(vm.insuranceProduct, onSaveSuccess, onSaveError);
                }
        	}
        	
        	/**
        	 * trigger EForm submit for xeditable input on premiumrate pages
        	 */
        	function triggerEForm(id, index, name){
        		vm.isValid = false;
        		$state.go($state.current.parent + '.premiumrate');
        		
        		$timeout(function() {
				    angular.element('#button'+ name + '-' + id + index).triggerHandler('click');
				}, 0);
				
        	}
        }

        /**
         * on Save Product success callback
         */
        function onSaveSuccess (result) {
            vm.isSaving = false;
            vm.cancel();
        }

        /**
         * On Save Product error callback
         */
        function onSaveError () {
            vm.isSaving = false;
        }
        
        /**
         * listen to company choosed 
         */
        var unsubscribeCompany = $rootScope.$on('whatscoverApp:insuranceProductCompanyUpdate', function(event, result) {
        	vm.insuranceProduct.insuranceCompanyId = result.id;
            vm.insuranceProduct.insuranceCompanyName = result.name;
        });
        $scope.$on('$destroy', unsubscribeCompany);
        
        /**
         * listen to product category choosed
         */
        var unsubscribeProductCategory = $rootScope.$on('whatscoverApp:insuranceProductCategoryUpdate', function(event, result) {
        	vm.insuranceProduct.productCategoryId = result.id;
            vm.insuranceProduct.productCategoryName = result.name;
        });
        $scope.$on('$destroy', unsubscribeProductCategory);

        /**
         * init scope
         */
        $scope.initialise = function() {

            $scope.go = function(state) {
              $state.go(state);
            };
            
            $scope.tabData   = [
              {
                heading: '<i>General</i>',
                route:   $state.current.parent + '.general'
              },
              {
                heading: '<i>Premium Rate</i>',
                route:   $state.current.parent + '.premiumrate'
              }
            ];
          };

          $scope.initialise();
          
          loadInsuranceProductPremiumRate();

          /**
           * load insurance premium rates
           */
          function loadInsuranceProductPremiumRate () {
        	  if(vm.insuranceProduct.id != null){
        		  InsuranceProductPremiumRateSearch.searchByProductId({
            		  query: vm.insuranceProduct.id,
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
                  vm.insuranceProductPremiumRates = data;
                  vm.page = pagingParams.page;
              }
              function onError(error) {
                  AlertService.error(error.data.message);
              }
          }
          
          /**
           * add Premium Rate Row
           */
          function addRow(){
        	  var newPremiumRate = {
    			  "id" : null,
    			  "entryAge" : null,
    			  "malePremiumRate" : 0,
    			  "femalePremiumRate" : 0,
    			  "plan" : null,
    			  "insuranceProductId" : null,
    			  "status" : "NEW",
    			  "createdBy" : null,
    			  "createdDate" : null,
    			  "lastModifiedBy" : null,
    			  "lastModifiedDate" : null,	
    			  "index" : vm.insuranceProductPremiumRates.length
        	  }
        	  vm.insuranceProductPremiumRates.push(newPremiumRate);
          }
          
          /**
           * delete Premium Rate Row
           */
          function removeRow(id, index){
        	  if(id != null){
        		  vm.insuranceProductPremiumRates[vm.insuranceProductPremiumRates.findIndex(el => el.id === id)].status = "DELETE";
        	  }else{
        		  vm.insuranceProductPremiumRates[index].status = "DELETE";
        	  }
        	  
          }
          
          /**
           * triggered when there is updates on premium rates field
           */
          function onUpdateField(id){
        	  if(id != null){
        		  vm.insuranceProductPremiumRates[vm.insuranceProductPremiumRates.findIndex(el => el.id === id)].status = "UPDATE";
        	  }
          };
          
          /**
           * validation for xeditable input premium rate
           */
          function checkEmptyData(data){
        	  if (data === '' || data === null || data === undefined ) {
        	      return "Please fill out this field";
        	  }
          };
          
          /**
           * back to parent state insurance-product
           */
          function cancel(){
        	  $state.go('insurance-product');
          }
    }
})();
