package com.example.jo_gpt_program.gpt.restController;

import com.example.jo_gpt_program.gpt.dto.MyChatDTO;
import com.example.jo_gpt_program.gpt.service.ContentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contents")
@Slf4j
public class ContentsController {

    private final String geminiKey;


    private final ContentsService contentsService;

    // static 메서드 사용 시, 생성자 사용 불가
    public ContentsController(ContentsService contentsService, @Value("${spring.llm.key}") String geminiKey) {
        this.geminiKey = geminiKey;
        this.contentsService = contentsService;
    }

    @PostMapping("/myContents")
    public ResponseEntity<String> getMyContents(@RequestBody MyChatDTO dto, @RequestHeader("Authorization") String authHeader) {

        log.debug("authHeader :{}", authHeader);
        String response = contentsService.myChat(dto);
        log.debug("response :{}", response);
        System.out.println("response :" + response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/gptContents")
    public ResponseEntity<String> getGptContents(@RequestBody MyChatDTO dto) {
        String response = contentsService.sendGemini(dto, geminiKey);
        return ResponseEntity.ok(response);
    }
}
