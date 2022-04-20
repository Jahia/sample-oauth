(function () {
    'use strict';
    angular.module('JahiaOAuthApp').controller('KeycloakController', KeycloakController);
    KeycloakController.$inject = ['$location', 'settingsService', 'helperService', 'i18nService'];

    const CONNECTOR_NAME = 'KeycloakApi20';
    const DEFAULT_SECRET = CONNECTOR_NAME;

    function KeycloakController($location, settingsService, helperService, i18nService) {
        var vm = this;
        vm.expandedCard = false;
        vm.callbackUrl = '';
        vm.saveSettings = () => {
            // Value can't be empty
            if (!vm.realm || !vm.apiKey || !vm.scope || !vm.baseUrl || !vm.callbackUrl) {
                helperService.errorToast(i18nService.message('soauthnt_keycloakOAuthView.message.error.missingMandatoryProperties'));
                return false;
            }

            // the node name here must be the same as the one in your spring file
            settingsService.setConnectorData({
                connectorServiceName: CONNECTOR_NAME,
                properties: {
                    enabled: vm.enabled,
                    realm: vm.realm,
                    apiKey: vm.apiKey,
                    // WARN: Mandatory for jahia-authentication module configuration
                    apiSecret: DEFAULT_SECRET,
                    scope: vm.scope,
                    baseUrl: vm.baseUrl,
                    callbackUrl: vm.callbackUrl
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
            $location.path(`/mappers/${CONNECTOR_NAME}`);
        };
        vm.toggleCard = () => {
            vm.expandedCard = !vm.expandedCard;
        };

        // must mach value in the plugin in pom.xml
        i18nService.addKey(sampleoauthi18n);

        settingsService.getConnectorData(CONNECTOR_NAME, ['enabled', 'apiKey', 'baseUrl', 'realm', 'callbackUrl', 'scope']).success(data => {
            if (data && !angular.equals(data, {})) {
                vm.connectorHasSettings = true;
                vm.enabled = data.enabled;
                vm.realm = data.realm;
                vm.apiKey = data.apiKey;
                vm.scope = data.scope
                vm.baseUrl = data.baseUrl;
                vm.callbackUrl = data.callbackUrl;
                vm.expandedCard = true;
            } else {
                vm.connectorHasSettings = false;
                vm.enabled = false;
                vm.callbackUrl = 'http://localhost:8080/sites/digitall/home.keycloakOAuthCallbackAction.do';
            }
        }).error(data => {
            helperService.errorToast(i18nService.message('soauthnt_keycloakOAuthView') + ' ' + data.error);
        });
    }
})();
