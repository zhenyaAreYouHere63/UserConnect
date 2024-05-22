package com.user.connect.service.impl;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.user.connect.entity.token.AccessToken;
import com.user.connect.entity.token.RefreshToken;
import com.user.connect.entity.token.TokenPair;
import com.user.connect.entity.token.adapter.AccessTokenHandlerAdapter;
import com.user.connect.entity.token.adapter.RefreshTokenHandlerAdapter;
import com.user.connect.entity.user.User;
import com.user.connect.exception.external.InvalidTokenException;
import com.user.connect.jwt.JwtProperties;
import com.user.connect.service.JwtService;
import com.user.connect.service.util.KeyUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import static com.user.connect.service.util.TimeUtils.convertLocaDateTimeToDate;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;

    private final PrivateKey refreshTokenPrivateKey;
    private final JwtParser refreshTokenParser;
    private final RefreshTokenHandlerAdapter refreshTokenAdapter;

    private final PrivateKey accessTokenPrivateKey;
    private final JwtParser accessTokenParser;
    private final AccessTokenHandlerAdapter accessTokenAdapter;

    private final Map<String, String> accessTokenStorage = new ConcurrentHashMap<>();

    private final Map<String, String> refreshTokenStorage = new ConcurrentHashMap<>();


    public JwtServiceImpl(
            JwtProperties jwtProperties
    ) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        this.jwtProperties = jwtProperties;

        refreshTokenPrivateKey = KeyUtils.convertBase64PrivateKeyToJavaPrivateKey(
                jwtProperties.refreshToken().secretKey(),
                keyFactory
        );

        PublicKey refreshTokenPublicKey = KeyUtils.convertBase64PublicKeyToJavaPublicKey(
                jwtProperties.refreshToken().publicKey(),
                keyFactory
        );

        refreshTokenParser = Jwts.parser()
                .verifyWith(refreshTokenPublicKey)
                .build();

        refreshTokenAdapter = new RefreshTokenHandlerAdapter();

        accessTokenPrivateKey = KeyUtils.convertBase64PrivateKeyToJavaPrivateKey(
                jwtProperties.accessToken().secretKey(),
                keyFactory
        );

        PublicKey accessTokenPublicKey = KeyUtils.convertBase64PublicKeyToJavaPublicKey(
                jwtProperties.accessToken().publicKey(),
                keyFactory
        );

        accessTokenParser = Jwts.parser()
                .verifyWith(accessTokenPublicKey)
                .build();

        accessTokenAdapter = new AccessTokenHandlerAdapter();
    }

    @Override
    public String issueAccessToken(User user) {
        LocalDateTime issuedTime = LocalDateTime.now().withNano(0);
        LocalDateTime expirationTime = issuedTime.plusMinutes(jwtProperties.accessToken().expirationTimeInMinutes);

        String buildAccessToken = Jwts.builder()
                .issuedAt(convertLocaDateTimeToDate(issuedTime))
                .expiration(convertLocaDateTimeToDate(expirationTime))
                .subject(user.getEmail())
                .claim("role", user.getRole())
                .signWith(accessTokenPrivateKey, Jwts.SIG.RS256)
                .compact();
        
        log.info("Issued access token for user with email: {}", user.getEmail());
        accessTokenStorage.put(user.getEmail(), buildAccessToken);
        
        return buildAccessToken;
    }

    @Override
    public String issueRefreshToken(User user) {
        LocalDateTime issuedTime = LocalDateTime.now().withNano(0);
        LocalDateTime expirationTime = issuedTime.plusMinutes(jwtProperties.refreshToken().expirationTimeInMinutes);

        String buildRefreshToken = Jwts.builder()
                .issuedAt(convertLocaDateTimeToDate(issuedTime))
                .expiration(convertLocaDateTimeToDate(expirationTime))
                .subject(user.getEmail())
                .signWith(accessTokenPrivateKey, Jwts.SIG.RS256)
                .compact();

        log.info("Issued refresh token for user with email: {}", user.getEmail());
        refreshTokenStorage.put(user.getEmail(), buildRefreshToken);

        return buildRefreshToken;
    }

    @Override
    public TokenPair issueTokenPair(User user) {
        String accessToken = issueAccessToken(user);
        String refreshToken = issueRefreshToken(user);

        log.info("Issued token pair for user with email: {}", user.getEmail());
        return new TokenPair(accessToken, refreshToken);
    }

    @Override
    public AccessToken parseAccessToken(String accessToken) {
        try {
            return accessTokenParser.parse(accessToken).accept(accessTokenAdapter);
        }
        catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Given access token has expired", e);
        } catch (RuntimeException e) {
            throw new InvalidTokenException("Given invalid access token", e);
        }
    }

    @Override
    public RefreshToken parseRefreshToken(String refreshToken) {
        try {
            return refreshTokenParser.parse(refreshToken).accept(refreshTokenAdapter);
        }
        catch (RuntimeException e) {
            throw new InvalidTokenException("Given refresh token has expired", e);
        }
    }

    @Override
    public boolean isTokenValid(String accessToken, String email) {
        String emailOfUser = parseAccessToken(accessToken).tokenClaims().email();
        return emailOfUser.equals(email) && !isTokenExpired(accessToken);
    }

    @Override
    public boolean isTokenExpired(String accessToken) {
        Date today = Date.from(Instant.now());
        LocalDateTime expirationDateTime = parseAccessToken(accessToken).tokenClaims().exp();
        Date expirationDate = Date.from(expirationDateTime.atZone(ZoneId.systemDefault()).toInstant());
        boolean isExpired = expirationDate.before(today);

        if(isExpired) {
            log.warn("Token expired for accessToken: {}", accessToken);
        } else {
            log.info("Token has not expired for accessToken: {}", accessToken);
        }
        return isExpired;
    }

    @Override
    public void deleteAccessToken(String email) {
        String removedAccessToken = accessTokenStorage.remove(email);
        log.info("Deleted access token: {}", removedAccessToken);
    }

    @Override
    public void deleteRefreshToken(String email) {
        String removedRefreshToken = refreshTokenStorage.remove(email);
        log.info("Deleted refresh token: {}", removedRefreshToken);
    }
}
