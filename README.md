[![Build Status](https://travis-ci.org/TAMULib/IRIIIFService.svg?branch=master)](https://travis-ci.org/TAMULib/IRIIIFService)
[![Coverage Status](https://coveralls.io/repos/github/TAMULib/IRIIIFService/badge.svg?branch=master)](https://coveralls.io/github/TAMULib/IRIIIFService?branch=master)

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

<details>
<summary>Example Cantaloupe resolver delegate</summary>

<br/>

```
  module HttpResolver
    ##
    # @param identifier [String] Image identifier
    # @return [String,nil] URL of the image corresponding to the given
    #                      identifier, or nil if not found.
    #
    def self.get_url(_identifier)
      irid = Base64.decode64(_identifier)
      if irid.include? ":"
        parts = irid.split(':')
        ir = parts[0]
        path = parts[1]
        if ir == 'fedora'
          uri = '<%=@fedora_url%>' + path
        elsif ir == 'dspace'
          uri = '<%=@dspace_url%>' + path
        else
          uri = irid
        end
      else
        uri = '<%=@fedora_url%>' + irid
      end
    end
  end
```

</details>

## [DSpace](http://www.dspace.org/)
- [Installation](https://wiki.duraspace.org/display/DSDOC6x/Installing+DSpace#space-menu-link-content)
- [RDF](https://wiki.duraspace.org/display/DSDOC6x/Linked+%28Open%29+Data)
## [Fedora](https://fedorarepository.org/)
- [Installation](https://wiki.duraspace.org/display/FEDORA4x/Quick+Start)
- [API-X](https://github.com/fcrepo4-labs/fcrepo-api-x/blob/master/src/site/markdown/apix-design-overview.md)
- [Amherst PCDM](https://github.com/birkland/repository-extension-services/tree/apix-demo/acrepo-exts-pcdm)
# Configuration
> Configuration for this service is done in [application.yaml](https://github.com/TAMULib/IRIIIFService/blob/master/src/main/resources/application.yaml) file located in src/main/resrouces directory.

<details>
<summary>View Properties</summary>

<br/>

| **Property** | **Type** | **Description** | **Example** |
| :----------- | :------- | :-------------- | :---------- |
| server.contextPath | string | Path in which service is hosted. | /iiif-service |
| server.port | number | Port in which service is hosted. | 9000 |
| logging.level.edu.tamu.iiif | LOG_LEVEL | Log level for iiif service. | INFO |
| logging.level.org.springframework | LOG_LEVEL | Log level for spring framework. | INFO |
| logging.file | string | Log file. | iiif-service.log |
| logging.path | string | Path for log file. | /var/logs/iiif |
| spring.redis.host | string | Host for redis server. | localhost |
| spring.redis.port | number | Port for redis server. | 6379 |
| spring.profiles.active | string | Build environment profile. | production |
| spring.profiles.include | string | Additional build environment profiles. Must match repository identifier. | dspace, fedora |
| iiif.service.url | url | IIIF service URL. | http://localhost:${server.port}${server.contextPath} |
| iiif.service.connection.timeout | number | HTTP connection request timeout in milliseconds. | 300000  |
| iiif.service.connection.request.timeout | number | HTTP connection timeout in milliseconds. | 300000  |
| iiif.service.socket.timeout | number | HTTP socket timeout in milliseconds. | 300000  |
| iiif.image.server.url | url | IIIF image server URL. | http://localhost:8182/iiif/2 |
| iiif.logo.url | url | URL for a default logo. | https://localhost/assets/downloads/logos/Logo.png |
| iiif.dspace.identifier.dspace-rdf | string | DSpace RDF Identifier. | dspace |
| iiif.dspace.url | url | DSpace base URL. | http://localhost:8080 |
| iiif.dspace.webapp | string | DSpace UI webapp. | xmlui |
| iiif.fedora.identifier.fedora-pcm | string | Fedora PCDM RDF identifier. | fedora |
| iiif.fedora.url | url | Fedora REST URL. | http://localhost:9000/fcrepo/rest |
| iiif.fedora.pcdm.ext.url | url | Fedora Amherst PCDM service URL. | http://localhost:9107/pcdm |



</details>

# REST API

> All REST endpoints have these optional URL query parameters.

| **Query Parameter** | **Value** | **Functionality** |
| :----------- | :--------- | :---------------- |
| update | true | updates the redis cache for the requested manifest |
| allow | semicolon separated string of MIME types | only process the provided MIME types for resources |
| disallow | semicolon separated string of MIME types | exclude provided MIME types for resources |

## Fedora PCDM RDF
> With identifier ```fedora```

| **Title** | Collection |
| :-------- | :--------- |
| **Description** | Returns a generated or cached collection manifest for the provided Fedora container. |
| **URL** | ```/fedora/collection/**/*``` |
| **Method** | **GET** |
| **URL Parameters** | **Optional:**<br/>```update=[boolean]```<br/>```allow=[semicolon separated string of MIME types]```<br/>```disallow=[semicolon separated string of MIME types]``` |
| **Success Response** | **Code:** 200 OK<br/>**Content:**<br/>```{ ```<br/>&emsp;```"@context" : "http://iiif.io/api/presentation/2/context.json", ```<br/>&emsp;```"@id" : "http://localhost:8080/fedora/collection/cars_pcdm", ```<br/>&emsp;```"@type" : "sc:Collection", ```<br/>&emsp;```"collections" : [ ], ```<br/>&emsp;```"description" : "N/A", ```<br/>&emsp;```"label" : "Cars", ```<br/>&emsp;```"logo" : "https://localhost/assets/downloads/logos/Logo.png", ```<br/>&emsp;```"manifests" : [ { ```<br/>&emsp;&emsp;```"@id" : "http://localhost:8080/fedora/presentation/cars_pcdm_objects/vintage", ```<br/>&emsp;&emsp;```"@type" : "sc:Manifest", ```<br/>&emsp;&emsp;```"label" : "Vintage"```<br/>&emsp;```}, { ```<br/>&emsp;&emsp;```"@id" : "http://localhost:8080/fedora/presentation/cars_pcdm_objects/lamborghini", ```<br/>&emsp;&emsp;```"@type" : "sc:Manifest", ```<br/>&emsp;&emsp;```"label" : "Lamborghini"```<br/>&emsp;```}], ```<br/>&emsp;```"metadata" : [ ], ```<br/>&emsp;```"viewingHint" : "multi-part" ```<br/>&emsp;```}``` |
| **Error Response** | **Code:** 404 NOT_FOUND<br/>**Content:** ```Fedora PCDM RDF not found!``` |
| **Error Response** | **Code:** 503 SERVICE_UNAVAILABLE<br/>**Content:** ```[Exception message]``` |
| **Sample Request** | ```/fedora/collection/cars_pcdm``` |
| **Notes** | If the container is a root of a PCDM collection, the collection manifest will contain multiple manifests. If the container is an element of a collection within the PCDM model, the collection manifest will contain a single manifest. There is currently no way to generate a collection of collections. |

| **Title** | Presentation |
| :-------- | :--------- |
| **Description** | Returns a generated or cached presentation manifest for the provided Fedora container. |
| **URL** | ```/fedora/presentation/**/*``` |
| **Method** | **GET** |
| **URL Parameters** | **Optional:**<br/>```update=[boolean]```<br/>```allow=[semicolon separated string of MIME types]```<br/>```disallow=[semicolon separated string of MIME types]``` |
| **Success Response** | **Code:** 200 OK<br/>**Content:**<br/>```{ ```<br/>&emsp;```"@context" : "http://iiif.io/api/presentation/2/context.json", ```<br/>&emsp;```"@id" : "http://localhost:9003/fedora/presentation/cars_pcdm_objects/vintage", ```<br/>&emsp;```"@type" : "sc:Manifest", ```<br/>&emsp;```"description" : "Vintage", ```<br/>&emsp;```"label" : "Vintage", ```<br/>&emsp;```"logo" : "https://localhost/assets/downloads/logos/Logo.png", ```<br/>&emsp;```"metadata" : [ { ```<br/>&emsp;&emsp;```"label" : "Title", ```<br/>&emsp;&emsp;```"value" : "Vintage" ```<br/>&emsp;```}, { ```<br/>&emsp;&emsp;```"label" : "Description", ```<br/>&emsp;&emsp;```"value" : "A vintage car" ```<br/>&emsp;```} ], ```<br/>&emsp;```"sequences" : [ { ```<br/>&emsp;&emsp;```"@id" : "http://localhost:9003/fedora/sequence/cars_pcdm_objects/vintage", ```<br/>&emsp;&emsp;```"@type" : "sc:Sequence", ```<br/>&emsp;&emsp;```"canvases" : [ { ```<br/>&emsp;&emsp;&emsp;```"@id" : "http://localhost:9003/fedora/canvas/cars_pcdm_objects/vintage/pages/page_0", ```<br/>&emsp;&emsp;&emsp;```"@type" : "sc:Canvas", ```<br/>&emsp;&emsp;&emsp;```"height" : 1080, ```<br/>&emsp;&emsp;&emsp;```"images" : [ { ```<br/>&emsp;&emsp;&emsp;&emsp;```"@id" : "http://localhost:8182/iiif/2/ZmVkb3JhOmNhcnNfcGNkbV9vYmplY3RzL3ZpbnRhZ2UvcGFnZXMvcGFnZV8wL2ZpbGVzL3ZpbnRhZ2UuanBn/info.json", ```<br/>&emsp;&emsp;&emsp;&emsp;```"@type" : "oa:Annotation", ```<br/>&emsp;&emsp;&emsp;&emsp;```"motivation" : "sc:painting", ```<br/>&emsp;&emsp;&emsp;&emsp;```"on" : "http://localhost:9003/fedora/canvas/cars_pcdm_objects/vintage/pages/page_0", ```<br/>&emsp;&emsp;&emsp;&emsp;```"resource" : { ```<br/>&emsp;&emsp;&emsp;&emsp;&emsp;```"@id" : "http://localhost:8182/iiif/2/ZmVkb3JhOmNhcnNfcGNkbV9vYmplY3RzL3ZpbnRhZ2UvcGFnZXMvcGFnZV8wL2ZpbGVzL3ZpbnRhZ2UuanBn/full/full/0/default.jpg", ```<br/>&emsp;&emsp;&emsp;&emsp;&emsp;```"@type" : "dctypes:Image", ```<br/>&emsp;&emsp;&emsp;&emsp;&emsp;```"format" : "image/jpeg", ```<br/>&emsp;&emsp;&emsp;&emsp;&emsp;```"height" : 1080, ```<br/>&emsp;&emsp;&emsp;&emsp;&emsp;```"service" : { ```<br/>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;```"label" : "Fedora IIIF Image Resource Service", ```<br/>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;```"profile" : "http://iiif.io/api/image/2/level0.json", ```<br/>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;```"@context" : "http://iiif.io/api/image/2/context.json", ```<br/>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;```"@id" : "http://localhost:8182/iiif/2/ZmVkb3JhOmNhcnNfcGNkbV9vYmplY3RzL3ZpbnRhZ2UvcGFnZXMvcGFnZV8wL2ZpbGVzL3ZpbnRhZ2UuanBn" ```<br/>&emsp;&emsp;&emsp;&emsp;&emsp;```}, ```<br/>&emsp;&emsp;&emsp;&emsp;&emsp;```"width" : 1920 ```<br/>&emsp;&emsp;&emsp;&emsp;```} ```<br/>&emsp;&emsp;&emsp;```} ], ```<br/>&emsp;&emsp;&emsp;```"label" : "Page 0", ```<br/>&emsp;&emsp;&emsp;```"metadata" : [ ], ```<br/>&emsp;&emsp;&emsp;```"width" : 1920 ```<br/>&emsp;&emsp;```} ], ```<br/>&emsp;&emsp;```"label" : "Vintage" ```<br/>&emsp;```} ], ```<br/>&emsp;```"thumbnail" : { ```<br/>&emsp;&emsp;```"@id" : "http://localhost:8182/iiif/2/ZmVkb3JhOmNhcnNfcGNkbV9vYmplY3RzL3ZpbnRhZ2UvcGFnZXMvcGFnZV8wL2ZpbGVzL3ZpbnRhZ2UuanBn/full/!200,200/0/default.jpg", ```<br/>&emsp;&emsp;```"services" : [ { ```<br/>&emsp;&emsp;&emsp;```"@context" : "http://iiif.io/api/image/2/context.json", ```<br/>&emsp;&emsp;&emsp;```"@id" : "http://localhost:8182/iiif/2/ZmVkb3JhOmNhcnNfcGNkbV9vYmplY3RzL3ZpbnRhZ2UvcGFnZXMvcGFnZV8wL2ZpbGVzL3ZpbnRhZ2UuanBn", ```<br/>&emsp;&emsp;&emsp;```"label" : "Fedora IIIF Image Resource Service", ```<br/>&emsp;&emsp;&emsp;```"profile" : "http://iiif.io/api/image/2/level0.json" ```<br/>&emsp;&emsp;```} ] ```<br/>&emsp;```} ```<br/>```}``` |
| **Error Response** | **Code:** 404 NOT_FOUND<br/>**Content:** ```Fedora PCDM RDF not found!``` |
| **Error Response** | **Code:** 503 SERVICE_UNAVAILABLE<br/>**Content:** ```[Exception message]``` |
| **Sample Request** | ```/fedora/presentation/cars_pcdm_objects/vintage``` |
| **Notes** | <span style="color:red">Caution: </span> Currently, if the container is a root of a PCDM collection it will generate a compound presentation of all elements of the collection. This could take some time. It is planned to have all manifests utilize @id and be grainular. Then to expose an additional query parameter to explode the manifest. |

| **Title** | Sequence |
| :-------- | :--------- |
| **Description** | Returns a generated or cached sequence manifest for the provided Fedora container. |
| **URL** | ```/fedora/sequence/**/*``` |
| **Method** | **GET** |
| **URL Parameters** | **Optional:**<br/>```update=[boolean]```<br/>```allow=[semicolon separated string of MIME types]```<br/>```disallow=[semicolon separated string of MIME types]``` |
| **Success Response** | **Code:** 200 OK<br/>**Content:**<br/>```{}``` |
| **Error Response** | **Code:** 404 NOT_FOUND<br/>**Content:** ```Fedora PCDM RDF not found!``` |
| **Error Response** | **Code:** 503 SERVICE_UNAVAILABLE<br/>**Content:** ```[Exception message]``` |
| **Sample Request** | ```/fedora/sequence/cars_pcdm_objects/vintage``` |
| **Notes** | |

| **Title** | Canvas |
| :-------- | :--------- |
| **Description** | Returns a generated or cached sequence manifest for the provided Fedora container. |
| **URL** | ```/fedora/canvas/**/*``` |
| **Method** | **GET** |
| **URL Parameters** | **Optional:**<br/>```update=[boolean]```<br/>```allow=[semicolon separated string of MIME types]```<br/>```disallow=[semicolon separated string of MIME types]``` |
| **Success Response** | **Code:** 200 OK<br/>**Content:**<br/>```{}``` |
| **Error Response** | **Code:** 404 NOT_FOUND<br/>**Content:** ```Fedora PCDM RDF not found!``` |
| **Error Response** | **Code:** 503 SERVICE_UNAVAILABLE<br/>**Content:** ```[Exception message]``` |
| **Sample Request** | ```/fedora/canvas/cars_pcdm_objects/vintage/pages/page_0``` |
| **Notes** | |

| **Title** | Image |
| :-------- | :--------- |
| **Description** | Returns a generated or cached image manifest for the provided Fedora resource. |
| **URL** | ```/fedora/image/**/*``` |
| **Method** | **GET** |
| **URL Parameters** | **Optional:**<br/>```update=[boolean]```<br/>```allow=[semicolon separated string of MIME types]```<br/>```disallow=[semicolon separated string of MIME types]``` |
| **Success Response** | **Code:** 200 OK<br/>**Content:**<br/>```{}``` |
| **Error Response** | **Code:** 404 NOT_FOUND<br/>**Content:** ```Fedora PCDM RDF not found!``` |
| **Error Response** | **Code:** 503 SERVICE_UNAVAILABLE<br/>**Content:** ```[Exception message]``` |
| **Sample Request** | ```/fedora/image/cars_pcdm_objects/vintage/pages/page_0/files/vintage.jpg``` |
| **Notes** | |

## DSpace RDF
> With identifier ```dspace```

| **Title** | Collection |
| :-------- | :--------- |
| **Description** | Returns a generated or cached collection manifest for the provided DSpace handle. |
| **URL** | ```/dspace/collection/**/*``` |
| **Method** | **GET** |
| **URL Parameters** | **Optional:**<br/>```update=[boolean]```<br/>```allow=[semicolon separated string of MIME types]```<br/>```disallow=[semicolon separated string of MIME types]``` |
| **Success Response** | **Code:** 200 OK<br/>**Content:**<br/>```{}``` |
| **Error Response** | **Code:** 404 NOT_FOUND<br/>**Content:** ```Fedora PCDM RDF not found!``` |
| **Error Response** | **Code:** 503 SERVICE_UNAVAILABLE<br/>**Content:** ```[Exception message]``` |
| **Sample Request** | ```/dspace/collection/123456789/158298``` |
| **Notes** | If the handle is a community or subcommunity, the collection manifest will contain collections of its immediate children subcommunities of collections. If the handle is a collection, the collection manifest will contain presentation manifests of all the collections items. If the handle is an item, the collection manifest will contain a single presentation manifest. |

| **Title** | Presentation |
| :-------- | :--------- |
| **Description** | Returns a generated or cached presentation manifest for the provided DSpace handle. |
| **URL** | ```/dspace/presentation/**/*``` |
| **Method** | **GET** |
| **URL Parameters** | **Optional:**<br/>```update=[boolean]```<br/>```allow=[semicolon separated string of MIME types]```<br/>```disallow=[semicolon separated string of MIME types]``` |
| **Success Response** | **Code:** 200 OK<br/>**Content:**<br/>```{}``` |
| **Error Response** | **Code:** 404 NOT_FOUND<br/>**Content:** ```Fedora PCDM RDF not found!``` |
| **Error Response** | **Code:** 503 SERVICE_UNAVAILABLE<br/>**Content:** ```[Exception message]``` |
| **Sample Request** | ```/dspace/presentation/123456789/158313``` |
| **Notes** | <span style="color:red">Caution: </span> Currently, if the handle is a community, subcommunity, or collection, it will generate a compound presentation of all items below. This could take some time. It is planned to have all manifests utilize @id and be grainular. Then to expose an additional query parameter to explode the manifest. Additionally, how the items are nested in sequences and canvases may change in future releases. |

| **Title** | Sequence |
| :-------- | :--------- |
| **Description** | Returns a generated or cached sequence manifest for the provided DSpace handle. |
| **URL** | ```/dspace/sequence/**/*``` |
| **Method** | **GET** |
| **URL Parameters** | **Optional:**<br/>```update=[boolean]```<br/>```allow=[semicolon separated string of MIME types]```<br/>```disallow=[semicolon separated string of MIME types]``` |
| **Success Response** | **Code:** 200 OK<br/>**Content:**<br/>```{}``` |
| **Error Response** | **Code:** 404 NOT_FOUND<br/>**Content:** ```Fedora PCDM RDF not found!``` |
| **Error Response** | **Code:** 503 SERVICE_UNAVAILABLE<br/>**Content:** ```[Exception message]``` |
| **Sample Request** | ```/dspace/sequence/123456789/158313``` |
| **Notes** | |

| **Title** | Canvas |
| :-------- | :--------- |
| **Description** | Returns a generated or cached sequence manifest for the provided DSpace handle and bitstream path. |
| **URL** | ```/dspace/canvas/**/*``` |
| **Method** | **GET** |
| **URL Parameters** | **Optional:**<br/>```update=[boolean]```<br/>```allow=[semicolon separated string of MIME types]```<br/>```disallow=[semicolon separated string of MIME types]``` |
| **Success Response** | **Code:** 200 OK<br/>**Content:**<br/>```{}``` |
| **Error Response** | **Code:** 404 NOT_FOUND<br/>**Content:** ```Fedora PCDM RDF not found!``` |
| **Error Response** | **Code:** 503 SERVICE_UNAVAILABLE<br/>**Content:** ```[Exception message]``` |
| **Sample Request** | ```/dspace/canvas/123456789/158313/example.jpg``` |
| **Notes** | |

| **Title** | Image |
| :-------- | :--------- |
| **Description** | Returns a generated or cached image manifest for the provided DSpace handle and bitstream path. |
| **URL** | ```/dspace/image/**/*``` |
| **Method** | **GET** |
| **URL Parameters** | **Optional:**<br/>```update=[boolean]```<br/>```allow=[semicolon separated string of MIME types]```<br/>```disallow=[semicolon separated string of MIME types]``` |
| **Success Response** | **Code:** 200 OK<br/>**Content:**<br/>```{}``` |
| **Error Response** | **Code:** 404 NOT_FOUND<br/>**Content:** ```Fedora PCDM RDF not found!``` |
| **Error Response** | **Code:** 503 SERVICE_UNAVAILABLE<br/>**Content:** ```[Exception message]``` |
| **Sample Request** | ```/dspace/image/123456789/158313/example.jpg``` |
| **Notes** | |
