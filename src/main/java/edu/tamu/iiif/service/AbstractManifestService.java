package edu.tamu.iiif.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.RedisManifest;
import edu.tamu.iiif.model.RepositoryType;
import edu.tamu.iiif.model.repo.RedisManifestRepo;
import edu.tamu.iiif.utility.StringUtility;

public abstract class AbstractManifestService implements ManifestService {

    private final static Logger LOG = LoggerFactory.getLogger(AbstractManifestService.class);

    @Value("${iiif.service.url}")
    protected String iiifServiceUrl;

    @Value("${iiif.image.server.url}")
    protected String imageServerUrl;

    @Value("${iiif.logo.url}")
    protected String logoUrl;

    @Autowired
    private RedisManifestRepo redisManifestRepo;

    public String getManifest(String path, boolean update) throws IOException, URISyntaxException {

        String manifest;
        Optional<RedisManifest> optionalRedisManifest = getRedisManifest(path);

        if (optionalRedisManifest.isPresent()) {
            LOG.info("Manifest already in redis: " + optionalRedisManifest.get().getId() + " (" + optionalRedisManifest.get().getCreation() + ")");
            manifest = optionalRedisManifest.get().getJson();
        } else {
            LOG.info("Generating new manifest.");
            manifest = generateManifest(path);
            redisManifestRepo.save(new RedisManifest(StringUtility.encode(path), getManifestType(), getRepositoryType(), manifest));
            update = false;
        }

        if (update) {
            RedisManifest redisManifest = optionalRedisManifest.get();
            manifest = generateManifest(path);
            redisManifest.setJson(manifest);
            redisManifestRepo.save(redisManifest);
            LOG.info("Manifest update requested: " + path);
        } else {
            LOG.info("Manifest requested: " + path);
        }

        return manifest;
    }

    protected abstract String generateManifest(String path) throws IOException, URISyntaxException;

    protected abstract ManifestType getManifestType();

    protected abstract RepositoryType getRepositoryType();

    private Optional<RedisManifest> getRedisManifest(String path) {
        return redisManifestRepo.findByPathAndTypeAndRepository(StringUtility.encode(path), getManifestType(), getRepositoryType());
    }

}
