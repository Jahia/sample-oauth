(function () {
    'use strict';

    angular.module('JahiaOAuthApp').controller('OAuthMapperController', OAuthMapperController);
    OAuthMapperController.$inject = ['$routeParams', 'settingsService', 'helperService', 'i18nService'];

    function OAuthMapperController($routeParams, settingsService, helperService, i18nService) {
        // must mach value in the plugin in pom.xml
        i18nService.addKey(sampleoauthi18n);

        const vm = this;

        vm.mapperName = '';
        vm.enabled = false;
        vm.connectorProperties = [];
        vm.mapperProperties = [];
        vm.mapping = [];
        vm.selectedPropertyFromConnector = '';
        vm.selectedPropertyFromMapper = '';
        vm.expandedCard = false;

        vm.saveMapperSettings = () => {
            const _isNotMapped = (field, key) => {
                let isNotMapped = true;
                angular.forEach(vm.mapping, entry => {
                    if (entry[key] && entry[key].name === field) {
                        isNotMapped = false;
                    }
                });
                return isNotMapped;
            };

            let isMappingComplete = true;
            angular.forEach(vm.mapping, mapped => {
                if (!mapped.mapper || !mapped.connector) {
                    isMappingComplete = false;
                }
            });
            if (!isMappingComplete) {
                helperService.errorToast(i18nService.message('error.incompleteMapping'));
                return false;
            }

            let mandatoryPropertyAreMapped = true;
            angular.forEach(vm.mapperProperties, property => {
                if (property.mandatory) {
                    if (_isNotMapped(property.name, 'mapper')) {
                        mandatoryPropertyAreMapped = false
                    }
                }
            });
            if (vm.enabled && !mandatoryPropertyAreMapped) {
                helperService.errorToast(i18nService.message('joant_jcrOAuthView.message.error.mandatoryPropertiesNotMapped'));
                return false;
            }

            settingsService.setMapperMapping({
                connectorServiceName: $routeParams.connectorServiceName,
                mapperServiceName: vm.mapperName,
                properties: {
                    enabled: vm.enabled,
                },
                enabled: vm.enabled,
                mapping: vm.mapping
            }).success(() => helperService.successToast(i18nService.message('label.saveSuccess')))
                .error(data => helperService.errorToast(data.error));
        };

        vm.addMapping = () => {
            if (vm.selectedPropertyFromConnector) {
                vm.mapping.push({
                    connector: vm.selectedPropertyFromConnector
                });
                vm.selectedPropertyFromConnector = '';
            }
        };

        vm.removeMapping = index => vm.mapping.splice(index, 1);

        vm.getConnectorI18n = value => i18nService.message(`${$routeParams.connectorServiceName}.label.${value}`);
        vm.getMapperI18n = value => i18nService.message(`${$routeParams.connectorServiceName}.label.${value.replace(':', '_')}`);

        vm.toggleCard = () => vm.expandedCard = !vm.expandedCard;

        vm.orderByConnector = property => vm.getConnectorI18n(property.name);
        vm.orderByMapper = property => vm.getMapperI18n(property.name);

        vm.init = mapperName => {
            vm.mapperName = mapperName;

            settingsService.getMapperMapping({
                connectorServiceName: $routeParams.connectorServiceName,
                mapperServiceName: vm.mapperName
            }).success(data => {
                if (!angular.equals(data, {})) {
                    vm.enabled = data.enabled;
                    vm.mapping = data.mapping;
                    vm.expandedCard = true;
                }
            }).error(data => helperService.errorToast(data.error));

            settingsService.getConnectorProperties({
                connectorServiceName: $routeParams.connectorServiceName
            }).success(data => vm.connectorProperties = data.connectorProperties)
                .error(data => helperService.errorToast(data.error));

            settingsService.getMapperProperties({
                mapperServiceName: vm.mapperName
            }).success(data => vm.mapperProperties = data.mapperProperties)
                .error(data => helperService.errorToast(data.error));
        };
    }
})();
