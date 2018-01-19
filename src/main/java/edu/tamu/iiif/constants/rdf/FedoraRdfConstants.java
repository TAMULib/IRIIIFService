package edu.tamu.iiif.constants.rdf;

public class FedoraRdfConstants {

    public final static String FEDORA_FCR_METADATA = "/fcr:metadata";
    public final static String FEDORA_X_TURTLE_ACCEPT_TYPE = "application/x-turtle";

    public final static String IIIF_IMAGE_API_CONTEXT = "http://iiif.io/api/image/2/context.json";
    public final static String IIIF_IMAGE_API_LEVEL_ZERO_PROFILE = "http://iiif.io/api/image/2/level0.json";
    public final static String IIIF_IMAGE_API_LEVEL_ONE_PROFILE = "http://iiif.io/api/image/2/level1.json";
    public final static String IIIF_IMAGE_API_LEVEL_TWO_PROFILE = "http://iiif.io/api/image/2/level2.json";

    public final static String DUBLIN_CORE_PREFIX = "http://purl.org/dc/elements/1.1/";

    public final static String DUBLIN_CORE_TITLE_PREDICATE = DUBLIN_CORE_PREFIX + "title";
    public final static String DUBLIN_CORE_DESCRIPTION_PREDICATE = DUBLIN_CORE_PREFIX + "description";

    public final static String LDP_CONTAINS_PREDICATE = "http://www.w3.org/ns/ldp#contains";
    public final static String LDP_HAS_MEMBER_RELATION_PREDICATE = "http://www.w3.org/ns/ldp#hasMemberRelation";
    public final static String LDP_IS_MEMBER_OD_RELATION_PREDICATE = "http://www.w3.org/ns/ldp#isMemberOfRelation";

    public final static String PCDM_HAS_FILE_PREDICATE = "http://pcdm.org/models#hasFile";
    public final static String PCDM_HAS_MEMBER_PREDICATE = "http://pcdm.org/models#hasMember";

    public final static String IANA_FIRST_PREDICATE = "http://www.iana.org/assignments/relation/first";
    public final static String IANA_LAST_PREDICATE = "http://www.iana.org/assignments/relation/last";
    public final static String IANA_NEXT_PREDICATE = "http://www.iana.org/assignments/relation/next";
    public final static String IANA_PREVIOUS_PREDICATE = "http://www.iana.org/assignments/relation/previous";

    public final static String ORE_PROXY_FOR_PREDICATE = "http://www.openarchives.org/ore/terms#proxyFor";

    public final static String FEDORA_HAS_PARENT_PREDICATE = "http://fedora.info/definitions/v4/repository#hasParent";

    public final static String EBUCORE_HAS_MIME_TYPE_PREDICATE = "http://www.ebu.ch/metadata/ontologies/ebucore/ebucore#hasMimeType";
    public final static String EBUCORE_HEIGHT_PREDICATE = "http://www.ebu.ch/metadata/ontologies/ebucore/ebucore#height";
    public final static String EBUCORE_WIDTH_PREDICATE = "http://www.ebu.ch/metadata/ontologies/ebucore/ebucore#width";

}
