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

@Configuration
@EnableCaching
public class RedisConfig {

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig();
		defaultConfig.entryTtl(Duration.ofMinutes(10)); // 기본 TTL 10분
		defaultConfig.disableCachingNullValues(); // null값 제외
		
		// 개별 TTL 적용
		Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<String, RedisCacheConfiguration>();
		cacheConfigs.put("userDetail", defaultConfig.entryTtl(Duration.ofMinutes(30))); // boardContent TTL 10분

		return RedisCacheManager.builder(connectionFactory).cacheDefaults(defaultConfig)
				.withInitialCacheConfigurations(cacheConfigs).build();
	}
}
