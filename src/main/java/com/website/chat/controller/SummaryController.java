package com.website.chat.controller;

import com.website.user.dto.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class SummaryController {

    @PostMapping("/summary")
    public Boolean createSummary(@AuthenticationPrincipal CustomUserDetails user, @RequestBody String summary){
        System.out.println(summary);
        return true;
    }
}
