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

[IR IIIF Service Github Page](https://tamulib.github.io/IRIIIFService/)