package com.website.user.kakao;

import com.website.user.kakao.dto.KakaoTokenResponse;
import com.website.user.kakao.dto.KakaoUserResponse;
import lombok.Getter;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
public class Kakao {
    private final String token;
    private final KakaoUserResponse user;
    private final WebClient webClient = WebClient.create();

    public Kakao(String code){
        this.token = this.getKakaoUserToken(code);
        this.user = this.getKakaoUserInfo(this.token);
    }

    private String getKakaoUserToken(String code){
        KakaoTokenResponse res = webClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .bodyValue("grant_type=authorization_code"+
                        "&client_id=aa744aa96b95e26a882f6c6d522c3d97"+
                        "&redirect_uri=http://localhost:3000/redirect"+
                        "&code="+code)
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .block();
        System.out.println(res);
        return res.getAccessToken();
    }
    private KakaoUserResponse getKakaoUserInfo(String token){
        return webClient.post()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization","Bearer "+token)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .bodyToMono(KakaoUserResponse.class)
                .block();
    }
}
