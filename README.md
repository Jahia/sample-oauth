# Sample OAuth
This module provides implementation samples and should not be used as is.
This module is not supported.

## Maven dependencies

* Edit `pom.xml`
    * Add `jahia-oauth` dependency
        ```xml
        <dependency>
            <groupId>org.jahia.modules</groupId>
            <artifactId>jahia-oauth</artifactId>
            <version>3.3.0-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>
        ```
    * Add `jahia-oauth` dependency
        ```xml
        <dependency>
            <groupId>org.jahia.modules</groupId>
            <artifactId>jahia-authentication</artifactId>
            <version>1.1.0</version>
            <scope>provided</scope>
        </dependency>
        ```
    * Add `com.github.scribejava` dependency
        ```xml
        <dependency>
            <groupId>com.github.scribejava</groupId>
            <artifactId>scribejava-apis</artifactId>
            <version>6.8.1</version>
            <scope>provided</scope>
        </dependency>
        ```
    * Be carefeul to set the `<scope>` to **provided**

## Jahia dependencies

* Add `jahia-oauth` & `jahia-authentication` dependency
    * Edit `pom.xml`
        ```xml
        <jahia-depends>default,jahia-authentication,jahia-oauth</jahia-depends>
        ```
    * Edit `repository.xml`
        ```xml
        <sample-oauth j:dependencies="default jahia-authentication jahia-oauth" />
        ```
* Enable modules on the site
    * Go to Administration > Modules and extensions > Modules
    * Enable module `jahia-oauth`
    * Enable a module mapper, for instance JCR Authentication Provider `jcr-auth-provider`

## OAuth Implementations

* Create OSGI Service Component to extend `jahia-authentication` services
    * Implement `ConnectorService`:
        * Annotate the service component
        ```java
        @Component(service = {KeycloakConnectorImpl.class, OAuthConnectorService.class, ConnectorService.class}, property = {JahiaAuthConstants.CONNECTOR_SERVICE_NAME + "=" + KeycloakConnectorImpl.KEY}, immediate = true)
        ```
        * Add the custom connector on activation
        * Remove the custom connector on deactivation
        * Specifiy the protetedResourceURL (URL to get the user info)
        * Specify the properties in the JSON result to enable OAuth Data Mapping
    * Implement 2 Jahia Actions
        * `ConnectToOAuthProvider`: used by the UI component (usually a login button)
        * `OAuthCallback`: set up in the site settings components form
        * Inject `JahiaOAuthService` and `SettingsService`
        ```java
        @Reference(service = JahiaOAuthService.class)
        private void refJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
            this.jahiaOAuthService = jahiaOAuthService;
        }

        @Reference(service = SettingsService.class)
        private void refSettingsService(SettingsService settingsService) {
            this.settingsService = settingsService;
        }
        ```

    * Edit `pom.xml` as JAVA classes for service component are annotated with `@Component`
    ```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.felix</groupId>
                <artifactId>maven-bundle-plugin</artifactId>
                <extensions>true</extensions>
                <configuration>
                    <instructions>
                        <_dsannotations>*</_dsannotations>
                    </instructions>
                </configuration>
            </plugin>
        </plugins>
    </build>
    ```

* Add site settings component
    * Edit `definitions.cnd`
    ```cnd
    [soauthnt:keycloakOAuthView] > jnt:content, jmix:authConnectorSettingView
    ```
    * Create a default view
        * Include your Angular Controller
        ```html
        <template:addResources type="javascript" resources="auth/keycloak-controller.js"/>

        <md-card ng-controller="KeycloakController as keycloak">
        ```
    * Edit `repository.xml`
    ```xml
    <keycloak-oauth-view j:defaultTemplate="false"
                         j:hiddenTemplate="true"
                         j:invertCondition="false"
                         j:requireLoggedUser="false"
                         j:requirePrivilegedUser="false"
                         jcr:primaryType="jnt:contentTemplate">
        <pagecontent jcr:primaryType="jnt:contentList">
            <keycloakoauthview jcr:primaryType="soauthnt:keycloakOAuthView"/>
        </pagecontent>
    </keycloak-oauth-view>
    ```
* Internationalization
    * Edit `pom.xml` and define a dictionaryName
    ```xml
    <build>
        <plugins>
            <plugin>
                <artifactId>jahia-maven-plugin</artifactId>
                <groupId>org.jahia.server</groupId>
                <executions>
                <execution>
                <id>i18n2js</id>
                <goals>
                    <goal>javascript-dictionary</goal>
                </goals>
                <configuration>
                    <dictionaryName>sampleoauthi18n</dictionaryName>
                </configuration>
                </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    ```

    * Update the Angular controller with the dictionaryName
    ```js
    i18nService.addKey(sampleoauthi18n);
    ```

    * Update your default view for the site component
    ```html
    <template:addResources type="javascript" resources="i18n/sample-oauth-i18n_${currentResource.locale}.js" var="i18nJSFile"/>
    <c:if test="${empty i18nJSFile}">
        <template:addResources type="javascript" resources="i18n/sample-oauth-i18n_en.js"/>
    </c:if>
    ```

* UI integration
    * Edit `definitions.cnd`
    ```cnd
    [soauthnt:keycloakButton] > jnt:content, joamix:oauthButtonConnector
    ```
    * Add a default view and create a JavaScript function to call asynchronously your custom implementation
    ```html
    <template:addResources>
        <script>
            function connectToKeycloak${fn:replace(currentNode.identifier, '-', '')}() {
                var popup = window.open('', "Keycloak Authorization", "menubar=no,status=no,scrollbars=no,width=1145,height=725,modal=yes,alwaysRaised=yes");
                var xhr = new XMLHttpRequest();
                xhr.open('GET', '<c:url value="${url.base}${renderContext.site.home.path}"/>.connectToKeycloakAction.do');
                xhr.setRequestHeader('Accept', 'application/json;');
                xhr.onreadystatechange = () => {
                    if (xhr.readyState !== 4 || xhr.status !== 200) {
                        return;
                    }
                    popup.location.href = JSON.parse(xhr.responseText).authorizationUrl;
                    window.addEventListener('message', event => {
                        if (event.data.authenticationIsDone) {
                            setTimeout(() => {
                                popup.close();
                                if (event.data.isAuthenticate) {
                                    window.location.search = 'site=${renderContext.site.siteKey}';
                                }
                            }, 3000);
                        }
                    });
                };
                xhr.send();
            }
        </script>
    </template:addResources>
    ```
    * Add a custom properties file to set up cache parameters
    ```
    cache.mainResource=true
    cache.perUser=true
    ```
