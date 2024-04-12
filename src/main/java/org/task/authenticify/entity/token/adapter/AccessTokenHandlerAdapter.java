package org.task.authenticify.entity.token.adapter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtHandlerAdapter;
import org.task.authenticify.entity.token.AccessToken;
import org.task.authenticify.entity.token.AccessTokenClaims;
import org.task.authenticify.entity.user.Role;
import static org.task.authenticify.service.util.TimeUtils.convertDateToLocalDate;

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
