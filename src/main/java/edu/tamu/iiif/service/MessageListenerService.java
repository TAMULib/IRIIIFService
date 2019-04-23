package edu.tamu.iiif.service;

import static edu.tamu.iiif.constants.Constants.DSPACE_RDF_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.FEDORA_PCDM_IDENTIFIER;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.tamu.iiif.config.DSpaceRdfIiifConfig;
import edu.tamu.iiif.config.FedoraPcdmIiifConfig;
import edu.tamu.iiif.exception.InvalidUrlException;
import edu.tamu.iiif.exception.UnknownActionException;
import edu.tamu.iiif.model.RedisManifest;
import edu.tamu.iiif.model.repo.RedisManifestRepo;
import edu.tamu.iiif.model.repo.RedisResourceRepo;
import edu.tamu.weaver.messaging.annotation.WeaverMessageListener;

@Service
public class MessageListenerService {

    @Autowired
    private List<RedisManifestRepo> manifestRepos;

    @Autowired
    private RedisResourceRepo redisResourceRepo;

    @Autowired
    private FedoraPcdmIiifConfig fedoraConfig;

    @Autowired
    private DSpaceRdfIiifConfig dspaceConfig;

    @WeaverMessageListener(destination = "${app.messaging.channel}")
    private void update(Map<String, String> message) throws UnknownActionException, InvalidUrlException {
        switch (message.get("action")) {
        case "METADATA_CREATE":
        case "METADATA_UPDATE":
        case "METADATA_DELETE":
            updateMetadata(message);
            break;
        case "RESOURCE_CREATE":
        case "RESOURCE_DELETE":
            updateResource(message);
            break;
        default:
            throw new UnknownActionException("The action " + message.get("action") + "is not defined for this service");
        }
    }

    private void updateMetadata(Map<String, String> message) throws InvalidUrlException {
        updateManifests(message.get("contextPath"), message.get("repositoryType"));
    }

    private void updateManifests(String contextPath, String repositoryType) throws InvalidUrlException {
        for (RedisManifestRepo rmr : manifestRepos) {
            List<RedisManifest> manifests = rmr.findByPathAndRepository(contextPath, repositoryType);
            for (RedisManifest manifest : manifests) {
                rmr.delete(manifest);
                redisResourceRepo.getOrCreate(getUriFromMessage(contextPath, repositoryType));
            }
        }
    }

    private String getUriFromMessage(String contextPath, String repositoryType) {
        String uri;
        if (repositoryType.equals(FEDORA_PCDM_IDENTIFIER)) {
            uri = fedoraConfig.getUrl() + contextPath;
        } else if (repositoryType.equals(DSPACE_RDF_IDENTIFIER)) {
            uri = dspaceConfig.getUrl() + contextPath;
        } else {
            uri = contextPath;
        }
        return uri;
    }

    private void updateResource(Map<String, String> message) throws InvalidUrlException {
        String parentContextPath = getParentContext(message);
        updateManifests(parentContextPath, message.get("repositoryType"));
    }

    private String getParentContext(Map<String, String> message) {
        String context = message.get("contextPath");
        String parentContext;
        if (message.get("repositoryType").equals(FEDORA_PCDM_IDENTIFIER)) {
            parentContext = context.substring(0, context.length() - 49);
        } else {
            // TODO: Implement for DSpace
            parentContext = context;
        }
        return parentContext;
    }

}
