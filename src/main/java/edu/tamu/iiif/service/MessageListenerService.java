package edu.tamu.iiif.service;

import static edu.tamu.iiif.utility.StringUtility.encode;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.exception.InvalidUrlException;
import edu.tamu.iiif.exception.UnknownActionException;
import edu.tamu.iiif.model.RedisManifest;
import edu.tamu.iiif.model.repo.RedisManifestRepo;
import edu.tamu.weaver.messaging.annotation.WeaverMessageListener;

@Service
public class MessageListenerService {

    private static final Logger logger = LoggerFactory.getLogger(MessageListenerService.class);

    @Autowired
    private RedisManifestRepo manifestRepo;

    @Autowired
    private List<ManifestService> manifestServices;

    @WeaverMessageListener(destination = "${iiif.messaging.channel}", containerFactory = "topicContainerFactory")
    private void update(Map<String, String> message) throws UnknownActionException, InvalidUrlException {
        switch (message.get("action")) {
        case "METADATA_CREATE":
        case "METADATA_UPDATE":
        case "METADATA_DELETE":
            updateManifest(message.get("contextPath"), message.get("repositoryType"));
            break;
        case "RESOURCE_CREATE":
        case "RESOURCE_DELETE":
            updateManifest(message.get("parentContextPath"), message.get("repositoryType"));
            break;
        default:
            throw new UnknownActionException("The action " + message.get("action") + "is not defined for this service");
        }
    }

    private void updateManifest(String contextPath, String repositoryType) {
        List<RedisManifest> manifests = manifestRepo.findByPathAndRepository(encode(contextPath), repositoryType);
        manifests.parallelStream().forEach(manifest -> {
            manifestServices.stream().filter(manifestService -> manifestService.getManifestType().equals(manifest.getType())).forEach(manifestService -> {
                try {
                    manifestService.getManifest(ManifestRequest.of(manifest));
                } catch (IOException | URISyntaxException e) {
                    if (logger.isDebugEnabled()) {
                        e.printStackTrace();
                    }
                    logger.info(String.format("Unable to get %s %s manifest for %s!", manifest.getRepository(), manifest.getType(), manifest.getPath()));
                }
            });
        });
    }

}
