(function () {
    'use strict';

    angular.module('JahiaOAuthApp').controller('OAuthConnectorController', OAuthConnectorController);
    OAuthConnectorController.$inject = ['$location', 'settingsService', 'helperService', 'i18nService'];

    function OAuthConnectorController($location, settingsService, helperService, i18nService) {
        // must mach value in the plugin in pom.xml
        i18nService.addKey(sampleoauthi18n);

        const vm = this;

        vm.saveSettings = () => {
            try {
                angular.forEach(vm.properties, property => {
                    if (property.mandatory && property.name && vm[property.name] === undefined) {
                        throw new Error(i18nService.message('label.missingMandatoryProperties'));
                    }
                })
            } catch (e) {
                helperService.errorToast(e.message);
                return false;
            }

            // the node name here must be the same as the one in your spring file
            const properties = {};
            angular.forEach(vm.properties, property => {
                if (vm[property.name] !== undefined) {
                    properties[property.name] = vm[property.name];
                }
            })
            settingsService.setConnectorData({connectorServiceName: vm.connectorServiceName, properties})
                .success(() => {
                    vm.connectorHasSettings = true;
                    helperService.successToast(i18nService.message('label.saveSuccess'));
                }).error(data => helperService.errorToast(data.error));
        };

        vm.goToMappers = () => $location.path(`/mappers/${vm.connectorServiceName}`);

        vm.toggleCard = () => vm.expandedCard = !vm.expandedCard;

        vm.init = (connectorServiceName, properties) => {
            vm.connectorServiceName = connectorServiceName;
            vm.properties = [
                {name: 'enabled', mandatory: true},
                {name: 'isTechnical', mandatory: true},
                ...properties
            ];

            settingsService.getConnectorData(vm.connectorServiceName, vm.properties.map(item => item.name))
                .success(data => {
                    if (data && !angular.equals(data, {})) {
                        vm.connectorHasSettings = true;
                        vm.enabled = data.enabled;
                        angular.forEach(vm.properties, property => vm[property.name] = data[property.name] || property.defaultValue);
                        vm.isTechnical = data.isTechnical === 'true';
                        vm.expandedCard = true;
                    } else {
                        vm.connectorHasSettings = false;
                        vm.enabled = false;
                        vm.isTechnical = false;
                    }
                }).error(data => helperService.errorToast(data.error));
        };
    }
})();
