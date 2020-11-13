package com.radiuslab.sample.roomItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

/* Spring CacheManager타입의 RedisCacheManager를 빈으로 등록 
 * -> 스프링에서는 캐싱할때 캐시에 저장하지 않고 redis에 저장한다.
 * */
@Configuration
public class CacheConfig {

	@Autowired
	RedisConnectionFactory redisConnectionFactory;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	RedisConnectionFactory connectionFactory;

	@Bean
	public CacheManager redisCacheManager() {
		RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
				.serializeKeysWith(
						RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
				.serializeValuesWith(RedisSerializationContext.SerializationPair
						.fromSerializer(new GenericJackson2JsonRedisSerializer()));

		RedisCacheManager redisCacheManager = RedisCacheManager.RedisCacheManagerBuilder
				.fromConnectionFactory(connectionFactory).cacheDefaults(redisCacheConfiguration).build();
		return redisCacheManager;
	}

}
