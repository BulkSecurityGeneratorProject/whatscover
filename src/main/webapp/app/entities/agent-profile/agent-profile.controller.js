(function() {
    'use strict';

    angular
        .module('whatscoverApp')
        .controller('AgentProfileController', AgentProfileController);

    AgentProfileController.$inject = ['$state', 'AgentProfile', 'AgentProfileSearch', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function AgentProfileController($state, AgentProfile, AgentProfileSearch, ParseLinks, AlertService, paginationConstants, pagingParams) {

        var vm = this;

        vm.agentProfiles = [];
        vm.queryData = [];
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.page = 0;
        vm.links = {
            last: 0
        };
        vm.predicate = 'id';
        vm.reset = reset;
        vm.reverse = true;
        vm.clear = clear;
        vm.loadAll = loadAll;
        vm.search = search;

        loadAll();

        function loadAll () {
            if (vm.queryData && vm.queryData.length == 3) 
            {
                AgentProfileSearch.query({
                    queryData: vm.queryData,
                    page: pagingParams.page - 1,
                    size: vm.itemsPerPage,
                    sort: sort()
                }, onSuccess, onError);
            } else {
                AgentProfile.query({
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
                for (var i = 0; i < data.length; i++) {
                    vm.agentProfiles.push(data[i]);
                }
	    	vm.page = pagingParams.page;
            }

            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function reset () {
            vm.page = 0;
            vm.agentProfiles = [];
            loadAll();
        }

        function loadPage(page) {
            vm.page = page;
            vm.transition();
            loadAll();
        }
        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }
        function pushData(searchQueryByName, searchQueryByCompanyName, searchQueryByAgentName) 
        {
        	if (!searchQueryByName) { searchQueryByName = ""; } 
        	if (!searchQueryByCompanyName) { searchQueryByCompanyName = ""; }
        	if (!searchQueryByAgentName) { searchQueryByAgentName = ""; } 
        	
        	vm.queryData = [];
        	vm.queryData.push(searchQueryByName, searchQueryByCompanyName, searchQueryByAgentName);
    	}
        


        function search (searchQueryByName, searchQueryByCompanyName, searchQueryByAgentName) {
            if (!searchQueryByName && !searchQueryByCompanyName && !searchQueryByAgentName){
                return vm.clear();
            } 
            vm.agentProfiles = [];
            vm.links = {
                last: 0
            };
            vm.page = 0;
            vm.predicate = '_score';
            vm.reverse = false;
            pushData(searchQueryByName, searchQueryByCompanyName, searchQueryByAgentName);
	    vm.transition();
            vm.loadAll();
        }
        function clear () {
            vm.agentProfiles = [];
            vm.links = {
                last: 0
            };
            vm.page = 0;
            vm.predicate = 'id';
            vm.reverse = true;
            vm.queryData = [];
            vm.loadAll();
        }
    }
})();
