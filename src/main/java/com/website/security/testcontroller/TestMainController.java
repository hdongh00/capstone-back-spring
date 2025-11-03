package com.website.security.testcontroller;

import com.website.user.dto.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@CrossOrigin
@Slf4j
public class TestMainController {

    @GetMapping("/api/test")
    public ResponseEntity<String> testApiUrl(){
        return ResponseEntity.ok().body("/api/test 경로 테스트");
    }
    @GetMapping("/auth/test")
    public ResponseEntity<CustomUserDetails> testDefaultUrl(@AuthenticationPrincipal CustomUserDetails user){
        return ResponseEntity.ok().body(user);
    }
}
