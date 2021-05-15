package at.cosylab.cloud.acam.commons.caching;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import payloads.acam.auth.Session;
import utils.CloudConstants;

import static javax.management.timer.Timer.ONE_DAY;

@Service
public class SessionCacheHandler {

    @Autowired
    CacheManager cm;

    @Scheduled(fixedRate = ONE_DAY)
    @CacheEvict(value = CloudConstants.TOKEN_CACHE_NAME)
    public void evictExpiredSessions(){ }

    @CachePut(value = CloudConstants.TOKEN_CACHE_NAME, key = "#result.getSessionID()")
    public Session put(Session session){
        return session;
    }

    @CacheEvict(value = CloudConstants.TOKEN_CACHE_NAME, key = "#sessionID")
    public void evictOnLogout(String sessionID){
        Cache cache = cm.getCache(CloudConstants.TOKEN_CACHE_NAME);
        cache.evictIfPresent(sessionID);
    }

    public Session findBySessionID(String sessionID) {
        Cache cache = cm.getCache(CloudConstants.TOKEN_CACHE_NAME);
        Session session = cache.get(sessionID, Session.class);
        if (session != null) {
            return session;
        } else {
            return null;
        }
    }
}
