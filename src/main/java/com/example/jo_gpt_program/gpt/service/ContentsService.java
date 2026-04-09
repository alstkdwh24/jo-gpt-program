package com.example.jo_gpt_program.gpt.service;

import com.example.entitycom.entity.log.CreateTimeLogs;
import com.example.entitycom.entity.member.Members;
import com.example.entitycom.entity.member.MyChat;
import com.example.jo_gpt_program.gpt.dto.MyChatDTO;
import com.example.jo_gpt_program.gpt.repository.jpa.MyChatRepository;
import com.example.memberssecurity.member.repository.jpa.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service("contentsService")
@Slf4j
public class ContentsService {

    private final MyChatRepository myChatRepository;
    private final RestTemplate restTemplate;

    private final MemberRepository memberRepository;

    public ContentsService(@Qualifier("myChatRepository") MyChatRepository myChatRepository,
                           RestTemplate restTemplate, MemberRepository memberRepository) {
        this.restTemplate = restTemplate;
        this.myChatRepository = myChatRepository;
        this.memberRepository = memberRepository;
    }
    public String userInfo(Long memberKey, MyChatDTO dto) {
        Optional<Members> members = memberRepository.findByMemberKey(memberKey);
        Members member = members.orElse(null);
        log.debug("member={}", member);
        String response = this.myChat(dto, member);
        return response;

    }
    @Transactional
    public String myChat(MyChatDTO dto, Members member) {

        MyChat chat = MyChat.builder()
                .member(member)
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
