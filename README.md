# IR IIIF Service
> This service provides IIIF manifest generation from DSpace RDF and/or Fedora PCDM.
## Requirements
- [Redis](https://redis.io/)
	- used to cache manifest
- [Tomcat](http://tomcat.apache.org/)
	- for production deployment of IR IIIF service
### External Requirements
- IIIF image server
	- must support API v2
	- script delegate to resolve identifier
	- tested with [Cantaloupe](https://medusa-project.github.io/cantaloupe/)
- DSpace
	- RDF webapp deployed and indexed
	- Triplestore
		- tested with [Fuseki](https://jena.apache.org/documentation/fuseki2/)
- Fedora
	- API-X
	- Amherst PCDM service and extensions
## [IIIF](http://iiif.io/) Image Server
- Image resolution by identifier
	- ```http://[iiif image server]/iiif/2/[base 64 encoded path]/full/full/0/default.jpg```
	- currently identifier is base 64 encoded path prefixed with IR type
	- path for Fedora is the resource path
		- e.g. ```fedora:9b/e3/2a/4b/9be32a4b-b506-4913-9939-9c7921c00e21/38/63/cb/f5/3863cbf5-6139-4a2b-b679-e92376231732```
	- path for DSpace is the webapp bitstream path
		- e.g. ```dspace:xmlui/bitstream/123456789/158319/11/primary.tif```
- [Presentation API v2](http://iiif.io/api/presentation/2.1/)
- [Image API v2](http://iiif.io/api/image/2.1/)
## [DSpace](http://www.dspace.org/)
- [Installation](https://wiki.duraspace.org/display/DSDOC6x/Installing+DSpace#space-menu-link-content)
- [RDF](https://wiki.duraspace.org/display/DSDOC6x/Linked+%28Open%29+Data)
## [Fedora](https://fedorarepository.org/)
- [Installation](https://wiki.duraspace.org/display/FEDORA4x/Quick+Start)
- [API-X](https://github.com/fcrepo4-labs/fcrepo-api-x/blob/master/src/site/markdown/apix-design-overview.md)
- [Amherst PCDM](https://github.com/birkland/repository-extension-services/tree/apix-demo/acrepo-exts-pcdm)
# Configuration
> Configuration for this service is done in application.properties file located in src/main/resrouces directory.

<details><summary>example application.properties</summary>
```
server.port: 9000

server.contextPath:

logging.level.edu.tamu.iiif: INFO
logging.level.org.springframework: INFO

logging.file: ir-iiif-service.log
logging.path: /var/log

spring.redis.host: localhost

spring.redis.port: 6379

spring.profiles: production
spring.profiles.include: dspace, fedora

iiif.service.url: http://localhost:${server.port}${server.contextPath}

iiif.dspace.url: http://localhost:8080

iiif.dspace.webapp: xmlui

iiif.fedora.url: http://localhost:9000/fcrepo/rest

iiif.pcdm.rdf.ext.url: http://localhost:9107/pcdm

iiif.image.server.url: http://localhost:8182/iiif/2

iiif.logo.url: https://localhost/assets/downloads/logos/Logo.png

iiif.service.connection.timeout: 300000

iiif.service.connection.request.timeout: 300000

iiif.service.socket.timeout: 300000

iiif.service.request.retries: 3
```
</details>
# REST API
## Fedora
| **Title** | Collection |
| :-------- | :--------- |
| **URL** | ```/fedora/collection``` |
| **Method** | **GET** |
| **URL Parameters** | **Required:**<br/>```context=[string]```<br/>**Optional:**<br/>```update=[boolean]``` |
| **Success Response** | **Code:** 200 OK<br/>**Content:**<br/>```{ ```<br/>&emsp;```"@context" : "http://iiif.io/api/presentation/2/context.json", ```<br/>&emsp;```"@id" : "http://localhost:8080/fedora/collection?context=cars_pcdm", ```<br/>&emsp;```"@type" : "sc:Collection", ```<br/>&emsp;```"collections" : [ ], ```<br/>&emsp;```"description" : "N/A", ```<br/>&emsp;```"label" : "Cars", ```<br/>&emsp;```"logo" : "https://localhost/assets/downloads/logos/Logo.png", ```<br/>&emsp;```"manifests" : [ { ```<br/>&emsp;&emsp;```"@id" : "http://localhost:8080/fedora/presentation?context=cars_pcdm_objects/vintage", ```<br/>&emsp;&emsp;```"@type" : "sc:Manifest", ```<br/>&emsp;&emsp;```"label" : "Vintage"```<br/>&emsp;```}, { ```<br/>&emsp;&emsp;```"@id" : "http://localhost:8080/fedora/presentation?context=cars_pcdm_objects/lamborghini", ```<br/>&emsp;&emsp;```"@type" : "sc:Manifest", ```<br/>&emsp;&emsp;```"label" : "Lamborghini"```<br/>&emsp;```}], ```<br/>&emsp;```"metadata" : [ ], ```<br/>&emsp;```"viewingHint" : "multi-part" ```<br/>&emsp;```}``` |
| **Error Response** | **Code:** 404 NOT_FOUND<br/>**Content:** ```Fedora PCDM RDF not found!``` |
| **Error Response** | **Code:** 503 SERVICE_UNAVAILABLE<br/>**Content:** ```[Exception message]``` |
| **Sample Request** | ```/collection?context=9b/e3/2a/4b/9be32a4b-b506-4913-9939-9c7921c00e21&update=true``` |
| **Notes** | |


