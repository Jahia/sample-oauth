(function () {
    'use strict';
    angular.module('JahiaOAuthApp').controller('AzureADController', AzureADController);
    AzureADController.$inject = ['$location', 'settingsService', 'helperService', 'i18nService'];
    function AzureADController($location, settingsService, helperService, i18nService) {
        var vm = this;
        vm.expandedCard = false;
        vm.callbackUrl = '';
        vm.saveSettings = () => {
            // Value can't be empty
            if (!vm.apiKey || !vm.apiSecret || !vm.callbackUrl) {
                helperService.errorToast(i18nService.message('label.missingMandatoryProperties'));
                return false;
            }

            // the node name here must be the same as the one in your spring file
            settingsService.setConnectorData({
                connectorServiceName: 'AzureADApi20',
                properties: {
                    enabled: vm.enabled,
                    apiKey: vm.apiKey,
                    apiSecret: vm.apiSecret,
                    callbackUrl: vm.callbackUrl,
                    scope: vm.scope,
                    tenantID: vm.tenantID
                }
            }).success(() => {
                vm.connectorHasSettings = true;
                helperService.successToast(i18nService.message('label.saveSuccess'));
            }).error(data => {
                helperService.errorToast(i18nService.message('soauthnt_AzureADOAuthView') + ' ' + data.error);
                console.log(data);
            });
        };
        vm.goToMappers = () => {
            // the second part of the path must be the service name
            $location.path('/mappers/AzureADApi20');
        };
        vm.toggleCard = () => {
            vm.expandedCard = !vm.expandedCard;
        };

        // must mach value in the plugin in pom.xml
        i18nService.addKey(sampleoauthi18n);

        settingsService.getConnectorData('AzureADApi20', ['enabled', 'apiKey', 'apiSecret', 'callbackUrl','tenantID', 'scope']).success(data => {
            if (data && !angular.equals(data, {})) {
                vm.connectorHasSettings = true;
                vm.enabled = data.enabled;
                vm.apiKey = data.apiKey;
                vm.apiSecret = data.apiSecret;
                vm.callbackUrl = data.callbackUrl;
                vm.tenantID = data.tenantID;
                vm.scope = data.scope;
                vm.expandedCard = true;
            } else {
                vm.connectorHasSettings = false;
                vm.enabled = false;
            }
        }).error(data => {
            helperService.errorToast(i18nService.message('soauthnt_AzureADOAuthView') + ' ' + data.error);
        });
    }
})();
