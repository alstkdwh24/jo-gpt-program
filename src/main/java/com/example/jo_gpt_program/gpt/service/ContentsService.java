package com.example.jo_gpt_program.gpt.service;

import com.example.entitycom.entity.log.CreateTimeLogs;
import com.example.entitycom.entity.member.MyChat;
import com.example.jo_gpt_program.gpt.dto.MyChatDTO;
import com.example.jo_gpt_program.gpt.repository.jpa.MyChatRepository;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service("contentsService")
@Slf4j
public class ContentsService {

    private final MyChatRepository myChatRepository;
    private final RestTemplate restTemplate;

    public ContentsService(@Qualifier("myChatRepository") MyChatRepository myChatRepository,
            RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.myChatRepository = myChatRepository;
    }

    @Transactional
    public String myChat(MyChatDTO dto) {

        MyChat chat = MyChat.builder()
                .myChatContents(dto.getMyChatContents())
                .myChatImage(dto.getMyChatImage())
                .createTimeLogs(CreateTimeLogs.builder()
                        .build()) // 여기서 builder로 생성만 하면, 위에서 추가한 @CreatedDate가 저장 시점에 시간을 자동 기입한다.
                .build();

        myChatRepository.save(chat);

        return "Hello, this is a sample chat response.";
    }

    public String sendGemini(MyChatDTO dto, String geminiKey) {

        Map<String, Object> body = Map.of("contents",
                List.of(Map.of("parts", List.of(Map.of("text", dto.getMyChatContents())))));

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key="
                        + geminiKey,
                body, String.class);
        log.debug("response gemini :{}", response);

        // TODO Auto-generated method stub
        return response.getBody();
    }
}
