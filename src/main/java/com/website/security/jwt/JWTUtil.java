package com.website.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {
    private final SecretKey secretKey;    //yml에 있는 secretKey임

    public JWTUtil(@Value("${spring.jwt.secret}")String secret){    //생성자가 yml 의 key를 받아와서 시크릿 키를 만들었다.
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    private Claims getInfo(String token){
        //verifyWith 는 시크릿 키를 갖고 우리 서버에서 생성된 토큰이 맞는지 검사한다.
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }
    public Long getUserCode(String token) {
        return getInfo(token).get("userCode",Long.class);
    }

    public String getUsername(String token){
        return getInfo(token).get("userName",String.class);
    }
    public String getRole(String token){
        return getInfo(token).get("role",String.class);
    }

    public Boolean isExpired(String token){
        try{
            return getInfo(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e){
            return true;
        }


    }

    //JWT 를 발급하는 메서드이다. 추가하고 싶은 정보를 추가하자.
    public String createJwt(Long accountCode,String username, String role, Long expiredMs){
        System.out.println("토큰이 만료될 시간 : "+new Date(System.currentTimeMillis() + expiredMs));
        return Jwts.builder()
                .claim("userCode",accountCode)
                .claim("userName",username)
                .claim("role",role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }


}
