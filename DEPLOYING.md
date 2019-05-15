## Packaging

IR IIIF Service build is done with [Maven](https://maven.apache.org/). The build is configured with [pom.xml](https://github.com/TAMULib/IRIIIFService/blob/master/pom.xml).

### Maven build arguments

* ```-DskipTests``` will skip tests.
* ```-Dspring.config.location=file:/var/ir-iiif-service/config/``` will configure the external configuration directory for development using `spring-boot:run`.

**Ending trailing slash is required for spring.config.location**

The external configuration directory is where an application.yml file can be added to override default properties. When packaging the application, define `config.uri`. This will template context.xml file with the `spring.config.location` system variable for container deployment. 

When running for development define `spring.config.location` to externalize the configuration.

**External configuration is recommended for production deployment**

### Development

```bash
$ mvn clean spring-boot:run
```

or run for development with external configuration

```bash
$ mvn clean spring-boot:run -Dspring.config.location=file:/var/ir-iiif-service/config/
```

### Production

```bash
$ mvn clean package -DskipTests -Dconfig.uri=file:/var/ir-iiif-service/config/
```

If build succeeds, you should have `ROOT.war` in the `target/` directory.

## Testing

```bash
$ mvn clean test
```

## Configuration

> Configuration for this service is done in [application.yml](https://github.com/TAMULib/IRIIIFService/blob/master/src/main/resources/application.yml) file located in src/main/resources directory.

<details>
<summary>View Properties</summary>

<br/>

| **Property** | **Type** | **Description** | **Example** |
| :----------- | :------- | :-------------- | :---------- |
| server.contextPath | string | Path in which service is hosted. | /iiif-service |
| server.port | number | Port in which service is hosted. | 9000 |
| logging.file | string | Log file. | iiif-service.log |
| logging.level.edu.tamu.iiif | LOG_LEVEL | Log level for iiif service. | INFO |
| logging.level.org.springframework | LOG_LEVEL | Log level for spring framework. | INFO |
| logging.path | string | Path for log file. | /var/logs/iiif |
| spring.activemq.broker-url | url | ActiveMQ broker URL. | tcp://localhost:61616 |
| spring.activemq.username | string | ActiveMQ broker username. | username |
| spring.activemq.password | string | ActiveMQ broker password. | password |
| spring.redis.host | string | Host for redis server. | localhost |
| spring.redis.port | number | Port for redis server. | 6379 |
| spring.profiles.active | string | Build environment profile. | production |
| spring.profiles.include | string | Additional build environment profiles. | dspace, fedora, weaver-messaging |
| messaging.channels.cap | string | Channel to listen for Weaver messages in order to update chached manifests. | cap |
| iiif.admins | object array | Array of admin credentials. | [ { username: admin, password: password } ] |
| iiif.service.url | url | IIIF service URL. | http://localhost:${server.port}${server.contextPath} |
| iiif.service.connection.timeout | number | HTTP connection request timeout in milliseconds. | 300000  |
| iiif.service.connection.request.timeout | number | HTTP connection timeout in milliseconds. | 300000  |
| iiif.service.socket.timeout | number | HTTP socket timeout in milliseconds. | 300000  |
| iiif.image.server.url | url | IIIF image server URL. | http://localhost:8182/iiif/2 |
| iiif.logo.url | url | URL for a default logo. | https://localhost/assets/downloads/logos/Logo.png |
| iiif.dspace.identifier | string | DSpace Identifier. | dspace |
| iiif.dspace.label-precedence | array | Array of valid RDF schema fields to determine title. | [ "http://purl.org/dc/elements/1.1/title", "http://purl.org/dc/terms/title" ] |
| iiif.dspace.description-precedence | array | Array of valid RDF schema fields to determine description. | [ "http://purl.org/dc/terms/abstract", "http://purl.org/dc/terms/description" ] |
| iiif.dspace.metadata-prefixes | array | Array of valid RDF schema URL to determine what metadate to include. | [ "http://purl.org/dc/elements/1.1/", "http://purl.org/dc/terms/" ] |
| iiif.dspace.url | url | DSpace base URL. | http://localhost:8080 |
| iiif.dspace.webapp | string | DSpace UI webapp. | xmlui |
| iiif.fedora.identifier | string | Fedora PCDM identifier. | fedora |
| iiif.fedora.url | url | Fedora REST URL. | http://localhost:9000/fcrepo/rest |
| iiif.dspace.label-precedence | array | Array of valid RDF schema fields to determine title. | [ "http://purl.org/dc/elements/1.1/title", "http://purl.org/dc/terms/title" ] |
| iiif.fedora.description-precedence | array | Array of valid RDF schema fields to determine description. | [ "http://purl.org/dc/terms/abstract", "http://purl.org/dc/terms/description" ] |
| iiif.fedora.metadata-prefixes | array | Array of valid RDF schema URL to determine what metadate to include. | [ "http://purl.org/dc/elements/1.1/", "http://purl.org/dc/terms/" ] |

</details>

Currently, in order to have Tomcat know where the external configuration directory is, `[Tomcat webapps directory]/ir-iiif-service/classes/META-INF/context.xml` will have to be updated. Skip step 1 if package built defining `config.uri`.

1) Update [context.xml](https://github.com/TAMULib/IRIIIFService/blob/master/src/main/resources/META-INF/context.xml) to set external configuration directory

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Context>
  <Parameter name="spring.config.location" value="file:/var/ir-iiif-service/config" />
</Context>
```

2) Update [application.yml](https://github.com/TAMULib/IRIIIFService/blob/master/src/main/resources/application.yml)


### Deploy to Tomcat

Copy war file into Tomcat webapps directory (your location may vary -- this is an example):

```bash
$ cp ~/ROOT.war /opt/tomcat/webapps/ir-iiif-service.war
```

**if not specifying config.uri during build the application.yml will be under the IR IIIF Service webapp's classpath, /opt/tomcat/webapps/ir-iiif-service/WEB-INF/classes/application.yml**

**if deployed from default WAR package and would like to externalize the config, you will have to edit /opt/tomcat/webapps/ir-iiif-service/META-INF/context.xml***

### Running WAR as a stand-alone Spring Boot application

```bash
java -jar target/ROOT.war
```
