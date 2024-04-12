package org.task.authenticify.entity.token.adapter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtHandlerAdapter;
import org.task.authenticify.entity.token.RefreshToken;
import org.task.authenticify.entity.token.RefreshTokenClaims;
import static org.task.authenticify.service.util.TimeUtils.convertDateToLocalDate;

public class RefreshTokenHandlerAdapter extends JwtHandlerAdapter<RefreshToken> {

    @Override
    public RefreshToken onClaimsJws(Jws<Claims> jws) {
        return new RefreshToken(
                jws.getHeader(),
                new RefreshTokenClaims(
                        convertDateToLocalDate(jws.getPayload().getIssuedAt()),
                        convertDateToLocalDate(jws.getPayload().getExpiration()),
                        jws.getPayload().getSubject()
                )
        );
    }
}
