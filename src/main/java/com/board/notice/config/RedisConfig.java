package com.board.notice.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableCaching
public class RedisConfig {

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
		
		RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofMinutes(10)) // 기본 TTL 10분
				.disableCachingNullValues() // null값 제외
				.serializeValuesWith(RedisSerializationContext.SerializationPair
						.fromSerializer(serializer));

		// 개별 TTL 적용
		Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<String, RedisCacheConfiguration>();
		cacheConfigs.put("userDetail", defaultConfig.entryTtl(Duration.ofMinutes(30)));
		cacheConfigs.put("top6Boards", defaultConfig.entryTtl(Duration.ofMinutes(5)));
		cacheConfigs.put("popularBoards", defaultConfig.entryTtl(Duration.ofMinutes(5)));

		return RedisCacheManager.builder(connectionFactory).cacheDefaults(defaultConfig)
				.withInitialCacheConfigurations(cacheConfigs).build();
	}
}
