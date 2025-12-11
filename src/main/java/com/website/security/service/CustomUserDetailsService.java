package com.website.security.service;

import com.website.entity.User;
import com.website.repository.UserRepository;
import com.website.user.dto.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //매니저가 UserDetailsService 를 구현한 클래스를 찾아 이 메서드를 실행시킨다.
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userData = userRepository.findByUserNickname(username);   //유저의 정보를 DB 에서 조회해오고,
        if(userData != null){
            userData.setLastLoginTime(LocalDateTime.now());
            userRepository.save(userData);
            return new CustomUserDetails(userData); //유저가 있다면 내가 만든 CustomUserDetails 의 형태로 return 한다. CustomUserDetails 보러가셈
        }

        throw new UsernameNotFoundException("아이디, 비밀번호가 다릅니다.");
    }
}
