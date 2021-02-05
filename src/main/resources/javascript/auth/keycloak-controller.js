(function () {
    'use strict';
    angular.module('JahiaOAuthApp').controller('KeycloakController', KeycloakController);
    KeycloakController.$inject = ['$location', 'settingsService', 'helperService', 'i18nService'];
    function KeycloakController($location, settingsService, helperService, i18nService) {
        var vm = this;
        vm.isActivate = false;
        vm.expandedCard = false;
        vm.callbackUrls = [];
        vm.callbackUrl = '';
        vm.saveSettings = () => {
            // Value can't be empty
            if (!vm.apiKey || !vm.apiSecret || vm.callbackUrls.length === 0) {
                helperService.errorToast(i18nService.message('label.missingMandatoryProperties'));
                return false;
            }

            // the node name here must be the same as the one in your spring file
            settingsService.setConnectorData({
                connectorServiceName: 'KeycloakApi20',
                nodeType: 'soauthnt:keycloakOAuthSettings',
                properties: {
                    isActivate: vm.isActivate,
                    apiKey: vm.apiKey,
                    apiSecret: vm.apiSecret,
                    baseUrl: vm.baseUrl,
                    realm: vm.realm,
                    callbackUrls: vm.callbackUrls,
                    scope: vm.scope
                }
            }).success(() => {
                vm.connectorHasSettings = true;
                helperService.successToast(i18nService.message('label.saveSuccess'));
            }).error(data => {
                helperService.errorToast(i18nService.message('soauthnt_keycloakOAuthView') + ' ' + data.error);
                console.log(data);
            });
        };
        vm.goToMappers = () => {
            // the second part of the path must be the service name
            $location.path('/mappers/KeycloakApi20');
        };
        vm.toggleCard = () => {
            vm.expandedCard = !vm.expandedCard;
        };

        vm.addUrl = (isValidUrl) => {
            if (isValidUrl && vm.callbackUrl !== '') {
                vm.callbackUrls.push(vm.callbackUrl);
                vm.callbackUrl = '';
            } else if (vm.callbackUrl !== '' && !isValidUrl) {
                helperService.errorToast(i18nService.message('joant_googleOAuthView.error.callbackURL.notAValidURL'))
            }
        };

        vm.removeUrl = (index) => {
            vm.callbackUrls.splice(index, 1);
        };

        // must mach value in the plugin in pom.xml
        i18nService.addKey(sampleoauthi18n);

        settingsService.getConnectorData('KeycloakApi20', ['isActivate', 'apiKey', 'apiSecret', 'baseUrl', 'realm', 'callbackUrls', 'scope']).success(data => {
            if (data && !angular.equals(data, {})) {
                vm.connectorHasSettings = true;
                vm.isActivate = data.isActivate;
                vm.apiKey = data.apiKey;
                vm.apiSecret = data.apiSecret;
                vm.baseUrl = data.baseUrl;
                vm.realm = data.realm;
                vm.callbackUrls = data.callbackUrls;
                vm.scope = data.scope
                vm.expandedCard = true;
            } else {
                vm.connectorHasSettings = false;
                vm.isActivate = false;
            }
        }).error(data => {
            helperService.errorToast(i18nService.message('soauthnt_keycloakOAuthView') + ' ' + data.error);
        });
    }
})();
