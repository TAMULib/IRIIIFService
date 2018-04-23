package edu.tamu.iiif.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import edu.tamu.iiif.model.RedisManifest;

/**
 * Redis configuration.
 * 
 * @author wwelling
 */
@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;

    /**
     * Configure Redis template bean.
     * 
     * @return
     */
    @Bean
    public RedisTemplate<String, RedisManifest> redisTemplate() {
        RedisTemplate<String, RedisManifest> redisTemplate = new RedisTemplate<String, RedisManifest>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

}
