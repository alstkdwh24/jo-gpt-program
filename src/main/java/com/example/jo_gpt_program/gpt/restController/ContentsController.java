package com.example.jo_gpt_program.gpt.restController;

import com.example.jo_gpt_program.gpt.dto.MyChatDTO;
import com.example.jo_gpt_program.gpt.dto.ShowChatDTO;
import com.example.jo_gpt_program.gpt.service.ContentsService;
import com.example.memberssecurity.member.service.MemberService;
import com.example.memberssecurity.security.config.jwt.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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
        if (authHeader == null) {
            return ResponseEntity.badRequest().body("Authorization header is missing");
        }
        authHeader = authHeader.replace("Bearer ", "");

        Long memberKey = jwtUtils.getUsername(authHeader);
        String success = contentsService.userInfo(memberKey, dto);

        return ResponseEntity.ok(success);
    }

    @PostMapping("/gptContents")
    public ResponseEntity<String> getGptContents(@RequestBody MyChatDTO dto) {
        String response = contentsService.sendGemini(dto, geminiKey);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/chatRoom")
    public ResponseEntity<String> createChatRoom( @RequestHeader("Authorization") String authHeader) {
        contentsService.createChat(authHeader);


        log.debug("dtosss={}", authHeader);

        return ResponseEntity.ok("Chat room created successfully");
    }
//여기서는 엔티티를 넣는 것보다는 DTO필드를 넣으면 된다 조인한 데이터가 필요하다면 DTO에 넣으면 된다.
    @GetMapping("/chattingList")
    public ResponseEntity<Set<ShowChatDTO>> getChattingList(@RequestHeader("Authorization") String authHeader) {
        Set<ShowChatDTO> showChatList = contentsService.getChattingList(authHeader);
        log.debug("showChatListssss={}", showChatList);
        return ResponseEntity.ok(showChatList);
    }

}
