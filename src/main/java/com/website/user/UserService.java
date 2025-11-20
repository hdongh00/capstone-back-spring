package com.website.user;

import com.website.entity.User;
import com.website.repository.ConversationRepository;
import com.website.repository.UserRepository;
import com.website.security.jwt.JWTUtil;
import com.website.user.dto.UserProfileDto;
import com.website.user.kakao.dto.KakaoAccount;
import com.website.user.kakao.dto.KakaoUserResponse;
import com.website.user.dto.SignupRequestDto;
import com.website.user.kakao.Kakao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final JWTUtil jwtUtil;

    public UserService(UserRepository userRepository, ConversationRepository conversationRepository, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
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
        return "Bearer "+jwtUtil.createJwt(loginUser.getUserCode(), loginUser.getName(), loginUser.getRole(), 1000 * 60 * 60 * 10L);
    }
    public UserProfileDto getProfileData(Long userCode){
        User user = userRepository.findById(userCode).orElseThrow();
        Integer conversationCount = conversationRepository.countByUserCode(userCode);
        Integer monthCount = conversationRepository.countByUserCodeAndDateBetween(userCode, LocalDate.now().withMonth(1).atStartOfDay(), LocalDate.now().withMonth(1).atStartOfDay().plusMonths(1));
        return new UserProfileDto(
                user.getName(),
                user.getEmail(),
                user.getCreateAt().toLocalDate(),
                user.getBio(),
                user.getCity()+", "+user.getRegion(),
                new String[] {"감사함", "평온함","성취감"},
                user.getProfileImage(),
                conversationCount,
                monthCount
        );
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

    @Transactional
    public boolean modifyUserProfile(Long userCode, UserProfileDto data) {
        try {
            if (data != null) {
                User user = userRepository.findById(userCode).orElseThrow();
                if (!data.getName().isEmpty()) user.setName(data.getName());
                if (!data.getBio().isEmpty()) user.setBio(data.getBio());
                if (!data.getEmail().isEmpty()) user.setEmail(data.getEmail());
                userRepository.save(user);
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            return false;
        }
    }
}
