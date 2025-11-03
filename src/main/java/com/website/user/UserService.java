package com.website.user;

import com.website.entity.User;
import com.website.repository.UserRepository;
import com.website.security.jwt.JWTUtil;
import com.website.user.kakao.dto.KakaoAccount;
import com.website.user.kakao.dto.KakaoUserResponse;
import com.website.user.dto.SignupRequestDto;
import com.website.user.kakao.Kakao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public UserService(UserRepository userRepository, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }
    @Transactional
    public String socialKaKaoLogin(String code) {
        Kakao kakao = new Kakao(code);
        KakaoUserResponse user = kakao.getUser();
        User loginUser = checkUser(
        "kakao",
                user.getId(),
                user.getKakaoAccount().getProfile().getNickname(),
                user.getKakaoAccount().getProfile().getProfileImageUrl());
        return jwtUtil.createJwt(loginUser.getUserCode(), loginUser.getName(), loginUser.getRole(), 1000 * 60 * 60 * 10L);
    }
    private User checkUser(String provider, Long userCode, String name, String profileImage) {
        return userRepository.findByOauthId(userCode)
                .orElseGet(()-> {
                    User u = new User();
                    u.setOauthId(userCode);
                    u.setName(name);
                    u.setNickname(name);
                    u.setProfileImage(profileImage);
                    u.setOauthProvider(provider);
                    u.setRole("ROLE_USER");
                    u.setEnable(true);
                    u.setCreateAt(LocalDateTime.now());
                    return userRepository.save(u);
                });
    }
}
