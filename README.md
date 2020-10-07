[![Build Status](https://travis-ci.org/TAMULib/IRIIIFService.svg?branch=main)](https://travis-ci.org/TAMULib/IRIIIFService)
[![Coverage Status](https://coveralls.io/repos/github/TAMULib/IRIIIFService/badge.svg?branch=main)](https://coveralls.io/github/TAMULib/IRIIIFService?branch=main)

# IR IIIF Service

> This service provides IIIF manifest generation from DSpace RDF and/or Fedora PCDM.

## Requirements

-   [Redis](https://redis.io/)
    -   manifest cache
    -   resource URL cache

## External Requirements

-   IIIF Image Server
    -   must support API v2
    -   script delegate to resolve identifier
    -   tested with [Cantaloupe](https://medusa-project.github.io/cantaloupe/)
-   DSpace
    -   RDF webapp deployed and indexed
    -   Triplestore
        -   tested with [Fuseki](https://jena.apache.org/documentation/fuseki2/)
-   Fedora
    -   structured with [PCDM](https://pcdm.org/)

### [IIIF](http://iiif.io/) Image Server

-   Image resolution by identifier
    -   `http://[iiif image server]/iiif/2/[UUID redis key]/full/full/0/default.jpg`
    -   UUID resource location resolution via resources interface
-   [Presentation API v2](http://iiif.io/api/presentation/2.1/)
-   [Image API v2](http://iiif.io/api/image/2.1/)

<details>
<summary>Example Cantaloupe custom delegate</summary>

<br/>

      require 'base64'
      class CustomDelegate
        ##
        # Returns one of the following:
        #
        # 1. String URI
        # 2. Hash with the following keys:
        #     * `uri` [String] (required)
        #     * `username` [String] For HTTP Basic authentication (optional).
        #     * `secret` [String] For HTTP Basic authentication (optional).
        #     * `headers` [Hash<String,String>] Hash of request headers (optional).
        # 3. nil if not found.
        #
        # @param options [Hash] Empty hash.
        # @return See above.
        #
        def httpsource_resource_info(options = {})
          id = context['identifier']
          puts id
          if ( id =~ /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/ )
            uri = '<%= @iiif_service_url %>resources/' + id + '/redirect'
          elsif
            irid = Base64.decode64(id)
            puts irid
            if irid.include? ":"
              parts = irid.split(':')
              ir = parts[0]
              path = parts[1]
              if ir == 'fedora'
                uri = '<%= @fedora_url %>' + path
              elsif ir == 'dspace'
                uri = '<%= @dspace_url %>' + path
              else
                uri = irid
              end
            else
              uri = id
            end
          end
          puts uri
          return uri
        end
      end

</details>

### [DSpace](http://www.dspace.org/)

-   [Installation](https://wiki.duraspace.org/display/DSDOC6x/Installing+DSpace#space-menu-link-content)
-   [RDF](https://wiki.duraspace.org/display/DSDOC6x/Linked+%28Open%29+Data)

### [Fedora](https://fedorarepository.org/)

-   [Installation](https://wiki.duraspace.org/display/FEDORA4x/Quick+Start)

## Developer Documentation

-   [Contributors Documentation](https://github.com/TAMULib/IRIIIFService/blob/master/CONTRIBUTING.md)
-   [Deployment Documentation](https://github.com/TAMULib/IRIIIFService/blob/master/DEPLOYING.md)
-   [API Documentation](https://tamulib.github.io/IRIIIFService)

Please feel free to file any issues concerning IRIIIFService to the issues section of the repository. Any questions concerning cap can be directed to [helpdesk@library.tamu.edu](<>)
