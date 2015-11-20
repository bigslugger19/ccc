package org.rmcc.ccc.controller;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.rmcc.ccc.annotations.Loggable;
import org.rmcc.ccc.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController extends BaseController {

    @Value("${jwt.secret}")
    private String jwtKey;

    @Loggable
    @RequestMapping(value = "/debug", method = RequestMethod.GET)
    public User debug(@RequestParam("email") String email) {
        return userRepository.findOneByEmail(email);
    }

    @Loggable
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginResponse login(HttpServletRequest request, @RequestBody User user) throws Exception {
        User u = userRepository.findOneByEmail(user.getEmail());

        if (u == null) {
            throw new AuthenticationException(AuthenticationException.INVALID);
        }

        if (!u.passwordMatches(user.getPassword())) {
            activityService.addAccountEntry(u.getId(), request.getRemoteAddr(),
                    AccountActivityEntry.EventType.SIGN_IN_FAILED);
            throw new AuthenticationException(AuthenticationException.INVALID);
        }

        if (u.isTwoFactorEnabled()) {
            if (user.getValidationCode() == null || user.getValidationCode() == 0) {
                throw new TwoFactorException(TwoFactorException.REQUIRED);
            } else {
                GoogleAuthenticator gAuth = new GoogleAuthenticator();
                if (!gAuth.authorize(u.getTwoFactorSecretKey(), user.getValidationCode())) {
                    activityService.addAccountEntry(u.getId(), request.getRemoteAddr(),
                            AccountActivityEntry.EventType.SIGN_IN_FAILED);
                    throw new TwoFactorException(TwoFactorException.INVALID);
                }
            }
        }

        u.setTwoFactorSecretKey(null);
        u.setPassword(null);

        activityService.addAccountEntry(u.getId(), request.getRemoteAddr(),
                AccountActivityEntry.EventType.SIGNED_IN);

        return new LoginResponse(u, Jwts.builder().setSubject(u.getEmail())
                .claim("roles", u.getRoles()).setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, jwtKey).compact());
    }

    @Loggable
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public User createUser(HttpServletRequest request, @RequestBody User user) throws Exception {
        if (userRepository.findOneByEmail(user.getEmail()) != null) {
            throw new Exception("Email already in use");
        }

        user.encodePassword();

        try (Transaction tx = graphDatabase.beginTx()) {
            user = userRepository.save(user);
            tx.success();
        }

        activityService.addAccountEntry(user.getId(), request.getRemoteAddr(),
                AccountActivityEntry.EventType.ACCOUNT_CREATED);

        return user;
    }

    @Loggable
    @RequestMapping(value = "/check-install", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public void checkInstall() throws Exception {
        Settings settings = settingsRepository.findOneByProfile("default");
        if (settings == null || !settings.isInitialized()) {
            throw new Exception("Setup needed");
        }
    }

    @Loggable
    @RequestMapping(value = "/check-connectivity", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public void checkConnectivity() throws Exception {
        cryptsyService.checkConnectivity();
    }
}

