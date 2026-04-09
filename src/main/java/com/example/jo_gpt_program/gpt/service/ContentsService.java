package com.example.jo_gpt_program.gpt.service;

import com.example.entitycom.entity.chat.ShowChat;
import com.example.entitycom.entity.log.CreateTimeLogs;
import com.example.entitycom.entity.member.Members;
import com.example.entitycom.entity.member.MyChat;
import com.example.jo_gpt_program.gpt.dto.MyChatDTO;
import com.example.jo_gpt_program.gpt.repository.jpa.MyChatRepository;
import com.example.jo_gpt_program.gpt.repository.jpa.ShowChatRepository;
import com.example.memberssecurity.member.repository.jpa.MemberRepository;
import com.example.memberssecurity.security.config.jwt.JWTUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("ALL")
@Service("contentsService")
@Slf4j
public class ContentsService {

    private final MyChatRepository myChatRepository;
    private final RestTemplate restTemplate;

    private final MemberRepository memberRepository;

    private final ShowChatRepository showChatRepository;

    private final JWTUtils jwtUtils;

    public ContentsService(@Qualifier("myChatRepository") MyChatRepository myChatRepository,
                           RestTemplate restTemplate, MemberRepository memberRepository, ShowChatRepository showChatRepository, JWTUtils jwtUtils) {
        this.restTemplate = restTemplate;
        this.myChatRepository = myChatRepository;
        this.memberRepository = memberRepository;
        this.showChatRepository = showChatRepository;
        this.jwtUtils = jwtUtils;
    }
    /*유저 정보 불러오기*/

    public String userInfo(Long memberKey, MyChatDTO dto) {
        Optional<Members> members = memberRepository.findByMemberKey(memberKey);
        Members member = members.orElseThrow(() -> new RuntimeException("Member not found with key: " + memberKey));
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

        MyChat myChat = myChatRepository.save(chat);

        return myChat.getMyChatContents();
    }

    public String sendGemini(MyChatDTO dto, String geminiKey) {

        Map<String, Object> body = Map.of("contents",
                List.of(Map.of("parts", List.of(Map.of("text", dto.getMyChatContents())))));

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key="
                        + geminiKey,
                body, String.class);
        log.debug("response gemini :{}", response);
        String body2 = response.getBody();
        if (body2 == null) {
            return "{\"error\": \"No response from Gemini\"}";
        }

        // TODO Auto-generated method stub
        return response.getBody();
    }


    public void createChat(String authHeader) {
        Members members = this.authHeader(authHeader);
        log.debug("membersssss={}", members);
        ShowChat showChat = ShowChat.builder()
                .members(members)
                .myChat()
                .build();
        showChatRepository.save(showChat);


    }

    public Members authHeader(String authHeader) {
        if (authHeader == null) {
            throw new IllegalArgumentException("Authorization header is missing");
        }
        authHeader = authHeader.replace("Bearer ", "");
        Long memberKey = jwtUtils.getUsername(authHeader);
        Members members=userInfoTwo(memberKey);
        if(members == null) {
            throw new RuntimeException("Member not Object: " + members);
        }


        return members;
    }
    /*유저 정보 불러오기*/
    public Members userInfoTwo(Long memberKey) {
        Optional<Members> members = memberRepository.findByMemberKey(memberKey);
        Members member = members.orElseThrow(() -> new RuntimeException("Member not found with key: " + memberKey));
        log.debug("member={}", member);
        return member;

    }
}
