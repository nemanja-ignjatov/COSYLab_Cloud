package at.cosylab.cloud.acam.services.auth;

import at.cosylab.cloud.acam.commons.caching.SessionCacheHandler;
import exceptions.AccountNotFoundException;
import exceptions.InvalidCredentialsException;
import exceptions.UnauthorizedAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import payloads.acam.auth.Session;
import utils.CloudConstants;
import utils.CryptoUtilFunctions;

@Service
public class AuthService {

    @Value("${cosylab.acam.god.username}")
    private String godUsername;
    @Value("${cosylab.acam.god.password}")
    private String godPassword;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private SessionCacheHandler sessionCache;

    public Session doLogin(String accountName, String password) throws InvalidCredentialsException {
        logger.info("[AUTH][LOGIN] " + accountName);
        if(!accountName.equals(godUsername) || !password.equals(godPassword)){
            throw new InvalidCredentialsException();
        }
        Session session = new Session(accountName, CloudConstants.Role.GOD, CryptoUtilFunctions.generateUUID());
        sessionCache.put(session);
        return session;
    }

    public boolean doLogout(String sessionID) {
        logger.info("[AUTH][LOGOUT] " + sessionID);
        sessionCache.evictOnLogout(sessionID);
        return true;
    }

    public Session retrieveSession(String sessionID) throws UnauthorizedAccessException {
        logger.info("[AUTH][RETRIEVESESSION] " + sessionID);

        Session session = sessionCache.findBySessionID(sessionID);
        if (session == null) {
            throw new UnauthorizedAccessException();
        }
        return session;
    }

    public boolean authorizeDeviceTypeAccess(Session session) throws UnauthorizedAccessException {
        logger.info("[AUTH][AUTHORIZEDEVICETYPEACCESS] " + session.getSessionID());
        if (session.getAccountRole().equals(CloudConstants.Role.GOD)) {
            return true;
        } else {
            throw new UnauthorizedAccessException();
        }
    }
}
