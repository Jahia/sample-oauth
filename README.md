# Sample OAuth

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

* Edit `pom.xml`
    * Add `jahia-oauth` & `jahia-authentication` dependency
        ```xml
        <jahia-depends>default,jahia-authentication,jahia-oauth</jahia-depends>
        ```
* Edit `repository.xml`
    * Add `jahia-oauth` & `jahia-authentication` dependency
        ```xml
        <sample-oauth j:dependencies="default jahia-authentication jahia-oauth" ...
        ```

## OAuth Implementations

### Scribe implementation: Keycloak
* Edit `repository.xml`
    * Add site settings component
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
* Edit `definitions.cnd`
    * Add site settings component
    ```cnd
    [soauthnt:keycloakOAuthView] > jnt:content, jmix:authConnectorSettingView

    [soauthnt:keycloakButton] > jnt:content, joamix:oauthButtonConnector
    ```

### Custom implementation: Strava
* Add a new JavaScript file to create a new Angular controller
    * Add the controller to the module `angular.module('JahiaOAuthApp')`

## TODO
* Modules activation
    * Enable the module `jahia-oauth` on the site
    * Enable your custom module on the site
