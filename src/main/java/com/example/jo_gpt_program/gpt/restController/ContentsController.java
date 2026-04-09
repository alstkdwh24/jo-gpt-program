package com.example.jo_gpt_program.gpt.restController;

import com.example.jo_gpt_program.gpt.dto.MyChatDTO;
import com.example.jo_gpt_program.gpt.service.ContentsService;
import com.example.memberssecurity.member.service.MemberService;
import com.example.memberssecurity.security.config.jwt.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contents")
@Slf4j
public class ContentsController {

    private final String geminiKey;

    private final JWTUtils jwtUtils;

    @Qualifier("memberService")
    private final MemberService memberService;

    private final ContentsService contentsService;

    // static 메서드 사용 시, 생성자 사용 불가
    public ContentsController(ContentsService contentsService, @Value("${spring.llm.key}") String geminiKey, JWTUtils jwtUtils, MemberService memberService) {
        this.geminiKey = geminiKey;
        this.contentsService = contentsService;
        this.jwtUtils = jwtUtils;
        this.memberService = memberService;
    }

    @PostMapping("/myContents")
    public ResponseEntity<String> getMyContents(@RequestBody MyChatDTO dto, @RequestHeader("Authorization") String authHeader) {
        authHeader = authHeader.replace("Bearer ", "");

        Long memberKey = jwtUtils.getUsername(authHeader );
        contentsService.userInfo(memberKey,dto);

        return ResponseEntity.ok("success");
    }

    @PostMapping("/gptContents")
    public ResponseEntity<String> getGptContents(@RequestBody MyChatDTO dto) {
        String response = contentsService.sendGemini(dto, geminiKey);
        return ResponseEntity.ok(response);
    }
}
