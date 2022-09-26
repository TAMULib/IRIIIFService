[![Build Status][build-badge]][build-status]
[![Coverage Status][coverage-badge]][coverage-status]

# Institutional Repository International Image Interoperability Framework Service

<a name="readme-top"></a>

A *Spring* backend for the *Institutional Repository International Image Interoperability Framework (IRIIIF) Service* developed and maintained by [Texas A&M University Libraries][tamu-library].

This service provides **IIIF** manifest generation from **DSpace RDF** and/or **Fedora PCDM**.

<details>
<summary>Table of contents</summary>

  - [Requirements:](#requirements)
  - [External Requirements:](#external-requirements)
    - [IIIF Server](#iiif-server)
    - [DSpace](#dspace)
    - [Fedora](#fedora)
  - [Developer Documentation](#developer-documentation)
  - [Additional Resources](#additional-resources)

</details>


## Requirements:

- [Redis][redis]
  - manifest cache
  - resource URL cache

<div align="right">(<a href="#readme-top">back to top</a>)</div>


### External Requirements:

- IIIF Server
  - must support API v2
  - script delegate to resolve identifier
  - tested with [Cantaloupe][cantaloupe]
- DSpace
  - RDF webapp deployed and indexed
  - Triplestore
    - tested with [Fuseki][fuseki]
- Fedora
  - structured with [PCDM][pcdm]

<div align="right">(<a href="#readme-top">back to top</a>)</div>


#### [IIIF][iiif] Server

- Image resolution by identifier
  - `http://[iiif image server]/iiif/2/[UUID redis key]/full/full/0/default.jpg`
  - UUID resource location resolution via resources interface
- [Presentation API v2][iiif-presentation-api-v2]
- [Image API v2][iiif-image-api-v2]

<details>
<summary>Example Cantaloupe custom delegate</summary>

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


#### [DSpace][dspace]

- [Installation][dspace-install]
- [RDF][dspace-rdf]

<div align="right">(<a href="#readme-top">back to top</a>)</div>


#### [Fedora][fedora]

- [Installation][fedora-install]

<details>
<summary>Fedora PCDM RDF Example</summary>

The following series of Fedora requests for PCDM RDF

https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457
```
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://fedora.info/definitions/v4/repository#Container> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://fedora.info/definitions/v4/repository#Resource> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://pcdm.org/models#Object> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://purl.org/dc/elements/1.1/contributor> "Fancy Paper Company"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://fedora.info/definitions/v4/repository#lastModifiedBy> "fedoraAdmin"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://www.iana.org/assignments/relation/last> <https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/orderProxies/page_0_proxy> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://purl.org/dc/elements/1.1/format> "reformatted digital"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://purl.org/dc/elements/1.1/rights> "In copyright; For more information see: http://rightsstatements.org/vocab/InC/1.0/"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://digital.library.tamu.edu/schemas/local/details> "Surface application; Sprinkled; M5.D2.F5; Machine made; Section(s) from the Decorated and Decorative Paper Terms Thesaurus: VIII.I.1; Not signed; Not stamped; Not stickered; Not embossed; Paper darkened; somewhat brittle and fragile; slightly torn"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://purl.org/dc/elements/1.1/title> "Berger-Cloonan #466"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://purl.org/dc/terms/extent> "1 sheet of red, gray, black and light brown sprinkled paper; 21.00 x 22.50 in."^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://purl.org/dc/elements/1.1/subject> "Decorative papers"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://purl.org/dc/elements/1.1/subject> "sprinkling"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://fedora.info/definitions/v4/repository#createdBy> "fedoraAdmin"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://fedora.info/definitions/v4/repository#created> "2022-01-27T18:18:54.551Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://fedora.info/definitions/v4/repository#lastModified> "2022-01-27T18:18:54.551Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://purl.org/dc/terms/medium> "Paper"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://purl.org/dc/elements/1.1/identifier> "466"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://purl.org/dc/elements/1.1/relation> "M5.D2.F5.A"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://purl.org/dc/terms/type> "StillImage"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://purl.org/dc/elements/1.1/type> "art reproduction"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://purl.org/dc/elements/1.1/type> "decorated papers"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://purl.org/dc/elements/1.1/type> "Multi-colored sprinkled papers"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://www.iana.org/assignments/relation/first> <https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/orderProxies/page_0_proxy> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/ldp#RDFSource> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/ldp#Container> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://fedora.info/definitions/v4/repository#writable> "false"^^<http://www.w3.org/2001/XMLSchema#boolean> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://fedora.info/definitions/v4/repository#hasParent> <https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://www.w3.org/ns/ldp#contains> <https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://www.w3.org/ns/ldp#contains> <https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/orderProxies> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> <http://pcdm.org/models#hasMember> <https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0> .
```

https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/orderProxies/page_0_proxy/fcr:metadata
```
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/orderProxies/page_0_proxy> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://fedora.info/definitions/v4/repository#Container> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/orderProxies/page_0_proxy> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://fedora.info/definitions/v4/repository#Resource> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/orderProxies/page_0_proxy> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://pcdm.org/models#Object> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/orderProxies/page_0_proxy> <http://fedora.info/definitions/v4/repository#lastModifiedBy> "fedoraAdmin"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/orderProxies/page_0_proxy> <http://fedora.info/definitions/v4/repository#createdBy> "fedoraAdmin"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/orderProxies/page_0_proxy> <http://fedora.info/definitions/v4/repository#created> "2022-01-27T18:18:54.551Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/orderProxies/page_0_proxy> <http://fedora.info/definitions/v4/repository#lastModified> "2022-01-27T18:18:54.551Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/orderProxies/page_0_proxy> <http://www.openarchives.org/ore/terms#proxyFor> <https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/orderProxies/page_0_proxy> <http://www.openarchives.org/ore/terms#proxyIn> <https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/orderProxies/page_0_proxy> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/ldp#RDFSource> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/orderProxies/page_0_proxy> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/ldp#Container> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/orderProxies/page_0_proxy> <http://fedora.info/definitions/v4/repository#writable> "false"^^<http://www.w3.org/2001/XMLSchema#boolean> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/orderProxies/page_0_proxy> <http://fedora.info/definitions/v4/repository#hasParent> <https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/orderProxies> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/orderProxies/page_0_proxy> <http://www.openarchives.org/ore/terms#proxyIn> <https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457> .
```

https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/fcr:metadata
```
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://fedora.info/definitions/v4/repository#Container> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://fedora.info/definitions/v4/repository#Resource> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://pcdm.org/models#Object> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0> <http://fedora.info/definitions/v4/repository#lastModifiedBy> "fedoraAdmin"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0> <http://fedora.info/definitions/v4/repository#createdBy> "fedoraAdmin"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0> <http://fedora.info/definitions/v4/repository#created> "2022-01-27T18:18:54.551Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0> <http://fedora.info/definitions/v4/repository#lastModified> "2022-01-27T18:18:54.551Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/ldp#RDFSource> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/ldp#Container> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0> <http://fedora.info/definitions/v4/repository#writable> "false"^^<http://www.w3.org/2001/XMLSchema#boolean> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0> <http://fedora.info/definitions/v4/repository#hasParent> <https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0> <http://www.w3.org/ns/ldp#contains> <https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0> <http://pcdm.org/models#hasFile> <https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg> .
```

https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/fcr:metadata
```
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://fedora.info/definitions/v4/repository#Container> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://fedora.info/definitions/v4/repository#Resource> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/ldp#DirectContainer> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://pcdm.org/models#Object> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files> <http://fedora.info/definitions/v4/repository#lastModifiedBy> "fedoraAdmin"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files> <http://fedora.info/definitions/v4/repository#createdBy> "fedoraAdmin"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files> <http://fedora.info/definitions/v4/repository#created> "2022-01-27T18:18:54.551Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files> <http://fedora.info/definitions/v4/repository#lastModified> "2022-01-27T18:18:54.551Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files> <http://www.w3.org/ns/ldp#membershipResource> <https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files> <http://www.w3.org/ns/ldp#hasMemberRelation> <http://pcdm.org/models#hasFile> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/ldp#RDFSource> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/ldp#Container> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files> <http://fedora.info/definitions/v4/repository#writable> "false"^^<http://www.w3.org/2001/XMLSchema#boolean> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files> <http://fedora.info/definitions/v4/repository#hasParent> <https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files> <http://www.w3.org/ns/ldp#contains> <https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg> .
```

https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg/fcr:metadata
```
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://pcdm.org/models#File> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://fedora.info/definitions/v4/repository#Binary> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://fedora.info/definitions/v4/repository#Resource> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg> <http://purl.org/dc/elements/1.1/filename> "00466.jpg"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg> <http://fedora.info/definitions/v4/repository#lastModifiedBy> "fedoraAdmin"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg> <http://www.loc.gov/premis/rdf/v1#hasSize> "22601344"^^<http://www.w3.org/2001/XMLSchema#long> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg> <http://www.ebu.ch/metadata/ontologies/ebucore/ebucore#hasMimeType> "image/jpeg"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg> <http://fedora.info/definitions/v4/repository#createdBy> "fedoraAdmin"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg> <http://fedora.info/definitions/v4/repository#created> "2022-01-27T18:18:54.551Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg> <http://www.loc.gov/premis/rdf/v1#hasMessageDigest> <urn:sha1:5ae7e8e2a5a385babc0a2cfa2aa4f7de69a9d7f7> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg> <http://fedora.info/definitions/v4/repository#lastModified> "2022-01-27T18:18:54.551Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg> <http://www.ebu.ch/metadata/ontologies/ebucore/ebucore#filename> "00466.jpg"^^<http://www.w3.org/2001/XMLSchema#string> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/ldp#NonRDFSource> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg> <http://fedora.info/definitions/v4/repository#writable> "false"^^<http://www.w3.org/2001/XMLSchema#boolean> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg> <http://www.iana.org/assignments/relation/describedby> <https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg/fcr:metadata> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg> <http://fedora.info/definitions/v4/repository#hasParent> <https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files> .
<https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg> <http://fedora.info/definitions/v4/repository#hasFixityService> <https://api-pre.library.tamu.edu/fcrepo/rest/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0/files/00466.jpg/fcr:fixity> .
```

Will result in IIIF v2 presentation manifest

```
{
  "@context" : "http://iiif.io/api/presentation/2/context.json",
  "@id" : "https://api-pre.library.tamu.edu/iiif-service/fedora/presentation/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457",
  "@type" : "sc:Manifest",
  "attribution" : [ "Fancy Paper Company", "In copyright; For more information see: http://rightsstatements.org/vocab/InC/1.0/" ],
  "label" : [ "Berger-Cloonan #466", "466" ],
  "logo" : "https://library.tamu.edu/assets/images/tamu-logos/TAM-PrimaryMarkB.png",
  "metadata" : [ {
    "label" : "identifier",
    "value" : "466"
  }, {
    "label" : "contributor",
    "value" : "Fancy Paper Company"
  }, {
    "label" : "subject",
    "value" : [ "Decorative papers", "sprinkling" ]
  }, {
    "label" : "rights",
    "value" : "In copyright; For more information see: http://rightsstatements.org/vocab/InC/1.0/"
  }, {
    "label" : "format",
    "value" : "reformatted digital"
  }, {
    "label" : "type",
    "value" : [ "Multi-colored sprinkled papers", "art reproduction", "decorated papers" ]
  }, {
    "label" : "title",
    "value" : "Berger-Cloonan #466"
  }, {
    "label" : "relation",
    "value" : "M5.D2.F5.A"
  }, {
    "label" : "extent",
    "value" : "1 sheet of red, gray, black and light brown sprinkled paper; 21.00 x 22.50 in."
  }, {
    "label" : "medium",
    "value" : "Paper"
  }, {
    "label" : "type",
    "value" : "StillImage"
  }, {
    "label" : "context",
    "value" : "bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457"
  } ],
  "sequences" : [ {
    "@id" : "https://api-pre.library.tamu.edu/iiif-service/fedora/sequence/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457",
    "@type" : "sc:Sequence",
    "canvases" : [ {
      "@id" : "https://api-pre.library.tamu.edu/iiif-service/fedora/canvas/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0",
      "@type" : "sc:Canvas",
      "height" : 9430,
      "images" : [ {
        "@id" : "https://api-pre.library.tamu.edu/iiif/2/8cabeecc-d4b6-3365-a075-bacc30d082e3/info.json",
        "@type" : "oa:Annotation",
        "motivation" : "sc:painting",
        "on" : "https://api-pre.library.tamu.edu/iiif-service/fedora/canvas/bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0",
        "resource" : {
          "@id" : "https://api-pre.library.tamu.edu/iiif/2/8cabeecc-d4b6-3365-a075-bacc30d082e3/full/full/0/default.jpg",
          "@type" : "dctypes:Image",
          "format" : "image/jpeg",
          "height" : 9430,
          "service" : {
            "label" : "Fedora IIIF Image Resource Service",
            "profile" : "http://iiif.io/api/image/2/level0.json",
            "@context" : "http://iiif.io/api/image/2/context.json",
            "@id" : "https://api-pre.library.tamu.edu/iiif/2/8cabeecc-d4b6-3365-a075-bacc30d082e3"
          },
          "width" : 8590
        }
      } ],
      "label" : "fedora:bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0",
      "metadata" : [ {
        "label" : "context",
        "value" : "bb/97/f2/3e/bb97f23e-803a-4bd6-8406-06802623554c/20/d5/bc/11/20d5bc11-1f51-487c-8ef6-00a5ece451aa/berger_cloonan_batch_5_objects/457/pages/page_0"
      } ],
      "width" : 8590
    } ],
    "label" : [ "Berger-Cloonan #466", "466" ]
  } ],
  "thumbnail" : {
    "@id" : "https://api-pre.library.tamu.edu/iiif/2/8cabeecc-d4b6-3365-a075-bacc30d082e3/full/!100,100/0/default.jpg",
    "service" : {
      "label" : "Fedora IIIF Image Resource Service",
      "profile" : "http://iiif.io/api/image/2/level0.json",
      "@context" : "http://iiif.io/api/image/2/context.json",
      "@id" : "https://api-pre.library.tamu.edu/iiif/2/8cabeecc-d4b6-3365-a075-bacc30d082e3"
    }
  }
}
```
</details>

> Please see [Mock Fedora PCDM RDF](https://github.com/TAMULib/IRIIIFService/tree/main/src/test/resources/mock/fedora/rdf) and [IIIF JSON](https://github.com/TAMULib/IRIIIFService/tree/main/src/test/resources/mock/fedora/json) for additional examples.

<div align="right">(<a href="#readme-top">back to top</a>)</div>

## Developer Documentation

- [Contributors Documentation][contribute-guide]
- [Deployment Documentation][deployment-guide]
- [API Documentation][api-guide]

<div align="right">(<a href="#readme-top">back to top</a>)</div>


## Additional Resources

Please feel free to file any issues concerning Ecosystem Identifier Service to the issues section of the repository.

Any questions concerning Ecosystem Identifier Service can be directed to helpdesk@library.tamu.edu.

Copyright © 2022 Texas A&M University Libraries under the [MIT License][license].

<div align="right">(<a href="#readme-top">back to top</a>)</div>


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
[iiif-presentation-api-v2]: http://iiif.io/api/presentation/2.1/
[iiif-image-api-v2]: http://iiif.io/api/image/2.1/

[dspace]: http://www.dspace.org/
[dspace-install]: https://wiki.duraspace.org/display/DSDOC6x/Installing+DSpace#space-menu-link-content
[dspace-rdf]: https://wiki.duraspace.org/display/DSDOC6x/Linked+%28Open%29+Data

[fedora]: https://fedorarepository.org/
[fedora-install]: https://wiki.duraspace.org/display/FEDORA4x/Quick+Start
