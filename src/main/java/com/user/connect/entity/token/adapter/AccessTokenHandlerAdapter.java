package com.user.connect.entity.token.adapter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtHandlerAdapter;
import com.user.connect.entity.token.AccessToken;
import com.user.connect.entity.token.AccessTokenClaims;
import com.user.connect.entity.user.Role;
import static com.user.connect.service.util.TimeUtils.convertDateToLocalDate;

public class AccessTokenHandlerAdapter extends JwtHandlerAdapter<AccessToken> {

    @Override
    public AccessToken onClaimsJws(Jws<Claims> jws) {
        return new AccessToken(
                jws.getHeader(),
                new AccessTokenClaims(
                        convertDateToLocalDate(jws.getPayload().getIssuedAt()),
                        convertDateToLocalDate(jws.getPayload().getExpiration()),
                        jws.getPayload().getSubject(),
                        Role.valueOf(jws.getPayload().get("role", String.class))
                )
        );
    }
}
