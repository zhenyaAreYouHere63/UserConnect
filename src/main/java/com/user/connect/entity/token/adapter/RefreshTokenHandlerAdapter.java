package com.user.connect.entity.token.adapter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtHandlerAdapter;
import com.user.connect.entity.token.RefreshToken;
import com.user.connect.entity.token.RefreshTokenClaims;
import static com.user.connect.service.util.TimeUtils.convertDateToLocalDate;

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
