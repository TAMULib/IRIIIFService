package edu.tamu.iiif.service;

import static edu.tamu.iiif.utility.StringUtility.encode;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.model.RedisManifest;
import edu.tamu.iiif.model.repo.RedisManifestRepo;
import edu.tamu.weaver.messaging.annotation.WeaverMessageListener;
import edu.tamu.weaver.messaging.model.MessageAction;

@Service
@Profile("!test")
public class MessageListenerService {

    private static final Logger logger = LoggerFactory.getLogger(MessageListenerService.class);

    @Autowired
    private RedisManifestRepo manifestRepo;

    @Autowired
    private List<ManifestService> manifestServices;

    @WeaverMessageListener(destination = "${iiif.messaging.channel:cap}", containerFactory = "topicContainerFactory")
    private void update(Map<String, String> message) {
        MessageAction action = MessageAction.valueOf(message.get("action"));
        switch (action) {
        case CREATE:
        case DELETE:
        case REFRESH:
        case UPDATE:
            updateManifest(message.get("id"));
            break;
        case READ:
        default:
            break;
        }

    }

    private void updateManifest(String id) {
        List<RedisManifest> manifests = manifestRepo.findByPath(encode(id));
        manifests.stream().forEach(manifest -> {
            manifestServices.stream().filter(manifestService -> manifestService.getManifestType().equals(manifest.getType()) && manifestService.getRepository().equals(manifest.getRepository())).forEach(manifestService -> {
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
