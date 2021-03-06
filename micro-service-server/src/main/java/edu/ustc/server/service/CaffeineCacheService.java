package edu.ustc.server.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

@Service
public class CaffeineCacheService {
	
	private static final Logger logger = LoggerFactory.getLogger(CaffeineCacheService.class);
	
	private static LoadingCache<String, String> caffeineCache;
	
	/**
	 * refreshAfterWrite will make a key eligible for refresh after the specified duration, 
	 * but a refresh will only be actually initiated when the entry is queried
	 */
	@PostConstruct
	public void init() {
		
		caffeineCache = Caffeine.newBuilder()
			.maximumSize(10_000)
			.expireAfterWrite(60, TimeUnit.SECONDS)
			.refreshAfterWrite(30, TimeUnit.SECONDS)
			.build(key -> costExpensiveResources(key));
		
		caffeineCache.put("k1", costExpensiveResources("k1"));
	}
	
	@Scheduled(fixedDelay = 1000 * 5)
	public void loadCaffeineCache() {
		logger.info("load caffeine cache, {}", caffeineCache.asMap());
	}
	
	@Scheduled(fixedDelay = 1000 * 15)
	public void queryCaffeineCache() {
		logger.info("query caffeine cache, {}", caffeineCache.get("k1"));
	}
	
	private String costExpensiveResources(String key) {
		return RandomStringUtils.randomAlphanumeric(10);
	}
}
