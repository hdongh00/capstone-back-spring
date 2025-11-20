package com.website.user;

import com.website.user.dto.CustomUserDetails;
import com.website.user.dto.SignupRequestDto;
import com.website.user.dto.UserProfileDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/api/user/oauth2/kakao")
    public ResponseEntity<String> socialLogin(@RequestParam String code){
        String jwt = userService.socialKaKaoLogin(code);
        return ResponseEntity.ok().body(jwt);
    }
    @GetMapping("/auth/user")
    public ResponseEntity<UserProfileDto> getProfileData(@AuthenticationPrincipal CustomUserDetails user){
        return ResponseEntity.ok().body(userService.getProfileData(user.getUserCode()));
    }
    @PostMapping("/auth/user")
    public ResponseEntity<Boolean> modifyUserProfile(@AuthenticationPrincipal CustomUserDetails user, @RequestBody UserProfileDto data){

        return ResponseEntity.ok().body(userService.modifyUserProfile(user.getUserCode(), data));

    }
}
