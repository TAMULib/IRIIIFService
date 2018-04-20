# IR IIIF Service
This service provides IIIF manifest generation from DSpace RDF and/or Fedora PCDM.
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