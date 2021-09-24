(function () {
    'use strict';

    angular.module('JahiaOAuthApp').controller('OktaMapperController', OktaMapperController);

    OktaMapperController.$inject = ['$routeParams', 'settingsService', 'helperService', 'i18nService'];

    function OktaMapperController($routeParams, settingsService, helperService, i18nService) {
        // must mach value in the plugin in pom.xml
        i18nService.addKey(sampleoauthi18n);

        var vm = this;

        vm.isActivate = false;
        vm.connectorProperties = [];
        vm.mapperProperties = [];
        vm.mapping = [];
        vm.selectedPropertyFromConnector = '';
        vm.selectedPropertyFromMapper = '';
        vm.expandedCard = false;

        vm.saveMapperSettings = () => {
            function _isNotMapped(field, key) {
                var isNotMapped = true;
                angular.forEach(vm.mapping, entry => {
                    if (entry[key] && entry[key].name === field) {
                        isNotMapped = false;
                    }
                });
                return isNotMapped;
            }

            var isMappingComplete = true;
            angular.forEach(vm.mapping, mapped => {
                if (!mapped.mapper || !mapped.connector) {
                    isMappingComplete = false;
                }
            });
            if (!isMappingComplete) {
                helperService.errorToast(i18nService.message('error.incompleteMapping'));
                return false;
            }

            var mandatoryPropertyAreMapped = true;
            angular.forEach(vm.mapperProperties, property => {
                if (property.mandatory) {
                    if (_isNotMapped(property.name, 'mapper')) {
                        mandatoryPropertyAreMapped = false
                    }
                }
            });
            if (vm.isActivate && !mandatoryPropertyAreMapped) {
                helperService.errorToast(i18nService.message('error.mandatoryPropertiesNotMapped'));
                return false;
            }

            settingsService.setMapperMapping({
                connectorServiceName: $routeParams.connectorServiceName,
                mapperServiceName: 'oktaOAuthMapper',
                nodeType: 'soauthnt:oktaOAuthMapperSettings',
                isActivate: vm.isActivate,
                mapping: vm.mapping
            }).success(() => {
                helperService.successToast(i18nService.message('label.saveSuccess'));
            }).error(data => {
                helperService.errorToast(`${i18nService.message('soauthnt_oktaOAuthMapperView')} ${data.error}`);
            });
        };

        vm.addMapping = () => {
            if (vm.selectedPropertyFromConnector) {
                vm.mapping.push({
                    connector: vm.selectedPropertyFromConnector
                });
                vm.selectedPropertyFromConnector = '';
            }
        }

        vm.removeMapping = index => {
            vm.mapping.splice(index, 1);
        }

        vm.getConnectorI18n = value => {
            return i18nService.message($routeParams.connectorServiceName + '.label.' + value);
        }

        vm.getMapperI18n = value => {
            return i18nService.message('OktaApi20.label.' + value.replace(':', '_'));
        }

        vm.toggleCard = () => {
            vm.expandedCard = !vm.expandedCard;
        }

        vm.orderByConnector = property => {
            return vm.getConnectorI18n(property.name);
        }

        vm.orderByMapper = property => {
            return vm.getMapperI18n(property.name);
        }

        settingsService.getMapperMapping({
            connectorServiceName: $routeParams.connectorServiceName,
            mapperServiceName: 'oktaOAuthMapper'
        }).success(data => {
            if (!angular.equals(data, {})) {
                vm.isActivate = data.isActivate;
                vm.mapping = data.mapping;
                vm.expandedCard = vm.isActivate;
            }
        }).error(data => {
            helperService.errorToast(`${i18nService.message('soauthnt_oktaOAuthMapperView')} ${data.error}`);
        });

        settingsService.getConnectorProperties({
            connectorServiceName: $routeParams.connectorServiceName
        }).success(data => {
            vm.connectorProperties = data.connectorProperties;
        }).error(data => {
            helperService.errorToast(`${i18nService.message('soauthnt_oktaOAuthMapperView')} ${data.error}`);
        });

        settingsService.getMapperProperties({
            mapperServiceName: 'oktaOAuthMapper'
        }).success(data => {
            vm.mapperProperties = data.mapperProperties;
        }).error(data => {
            helperService.errorToast(`${i18nService.message('soauthnt_oktaOAuthMapperView')} ${data.error}`);
        });
    }
})();
