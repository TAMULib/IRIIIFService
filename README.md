[![Build Status][build-badge]][build-status]
[![Coverage Status][coverage-badge]][coverage-status]

# Institutional Repository (IR) International Image Interoperability Framework (IIIF) Service

<a name="readme-top"></a>

This service provides IIIF manifest generation from DSpace RDF and/or Fedora PCDM.

<details>
<summary>Table of contents</summary>

  - [Requirements:](#requirements)
  - [External Requirements:](#external-requirements)
    - [IIIF Image Server](#iiif-image-server)
    - [DSpace](#dspace)
    - [Fedora](#fedora)
  - [Developer Documentation](#developer-documentation)
  - [Additional Resources](#additional-resources)

</details>

## Requirements:

- [Redis](redis)
  - manifest cache
  - resource URL cache

<div align="right">(<a href="#readme-top">back to top</a>)</div>

### External Requirements:

- IIIF Image Server
  - must support API v2
  - script delegate to resolve identifier
  - tested with [Cantaloupe](cantaloupe)
- DSpace
  - RDF webapp deployed and indexed
  - Triplestore
    - tested with [Fuseki](fuseki)
- Fedora
  - structured with [PCDM](pcdm)

<div align="right">(<a href="#readme-top">back to top</a>)</div>

### [IIIF](iiif) Image Server

- Image resolution by identifier
    - `http://[iiif image server]/iiif/2/[UUID redis key]/full/full/0/default.jpg`
    - UUID resource location resolution via resources interface
- [Presentation API v2](iiif-presentation-api-v2)
- [Image API v2](iiif-image-api-v2)

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

<div align="right">(<a href="#readme-top">back to top</a>)</div>

### [DSpace](dspace)

-   [Installation](dspace-install)
-   [RDF](dspace-rdf)

<div align="right">(<a href="#readme-top">back to top</a>)</div>

#### [Fedora](refora)

  - [Installation](fedora-install)

<div align="right">(<a href="#readme-top">back to top</a>)</div>

## Developer Documentation

- [Contributors Documentation][contribute-guide]
- [Deployment Documentation][deployment-guide]
- [API Documentation](api-guide)

<div align="right">(<a href="#readme-top">back to top</a>)</div>

## Additional Resources

Please feel free to file any issues concerning Ecosystem Identifier Service to the issues section of the repository.

Any questions concerning Ecosystem Identifier Service can be directed to helpdesk@library.tamu.edu.

<div align="right">(<a href="#readme-top">back to top</a>)</div>

Copyright © 2022 Texas A&M University Libraries under the [The MIT License][license].

<!-- LINKS -->
[build-badge]: https://github.com/TAMULib/IRIIIFService/workflows/Build/badge.svg
[build-status]: https://github.com/TAMULib/IRIIIFService/actions?query=workflow%3ABuild
[coverage-badge]: https://coveralls.io/repos/github/TAMULib/IRIIIFService/badge.svg
[coverage-status]: https://coveralls.io/github/TAMULib/IRIIIFService

[api-guide]: https://tamulib.github.io/IRIIIFService
[tamu-library]: http://library.tamu.edu
[deployment-guide]: DEPLOYING.md
[contribute-guide]: CONTRIBUTING.md
[license]: LICENSE

[redis]: https://redis.io/

[cantaloupe]: https://medusa-project.github.io/cantaloupe/
[fuseki]: https://jena.apache.org/documentation/fuseki2/

[pcdm]: https://pcdm.org/

[iiif]: http://iiif.io/
[iiif-presentation-api-v2]: http://iiif.io/api/presentation/2.1/)
[iiif-image-api-v2]: http://iiif.io/api/image/2.1/)

[dspace]: http://www.dspace.org/
[dspace-install]: https://wiki.duraspace.org/display/DSDOC6x/Installing+DSpace#space-menu-link-content
[dspace-rdf]: https://wiki.duraspace.org/display/DSDOC6x/Linked+%28Open%29+Data

[fedora]: https://fedorarepository.org/
[fedora-install]: https://wiki.duraspace.org/display/FEDORA4x/Quick+Start
