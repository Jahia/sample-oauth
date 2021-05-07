(function () {
    'use strict';
    angular.module('JahiaOAuthApp').controller('SalesforceController', SalesforceController);
    SalesforceController.$inject = ['$location', 'settingsService', 'helperService', 'i18nService'];
    function SalesforceController($location, settingsService, helperService, i18nService) {
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
                connectorServiceName: 'SalesforceApi',
                properties: {
                    enabled: vm.enabled,
                    apiKey: vm.apiKey,
                    apiSecret: vm.apiSecret,
                    callbackUrl: vm.callbackUrl,
                    sandbox: vm.sandbox
                }
            }).success(() => {
                vm.connectorHasSettings = true;
                helperService.successToast(i18nService.message('label.saveSuccess'));
            }).error(data => {
                helperService.errorToast(i18nService.message('soauthnt_salesforceOAuthView') + ' ' + data.error);
                console.log(data);
            });
        };
        vm.goToMappers = () => {
            // the second part of the path must be the service name
            $location.path('/mappers/SalesforceApi');
        };
        vm.toggleCard = () => {
            vm.expandedCard = !vm.expandedCard;
        };

        // must mach value in the plugin in pom.xml
        i18nService.addKey(sampleoauthi18n);

        settingsService.getConnectorData('SalesforceApi', ['enabled', 'apiKey', 'apiSecret', 'callbackUrl', 'sandbox']).success
        (data => {
            if (data && !angular.equals(data, {})) {
                vm.connectorHasSettings = true;
                vm.enabled = data.enabled;
                vm.apiKey = data.apiKey;
                vm.sandbox = (data.sandbox == 'true');
                vm.apiSecret = data.apiSecret;
                vm.callbackUrl = data.callbackUrl;
                vm.expandedCard = true;
            } else {
                vm.connectorHasSettings = false;
                vm.enabled = false;
                vm.sandbox = false;
            }
        }).error(data => {
            helperService.errorToast(i18nService.message('soauthnt_salesforceOAuthView') + ' ' + data.error);
        });
    }
})();
