package com.example.A201.chatbot.service;

import com.example.A201.board.domain.BmsBoard;
import com.example.A201.board.repository.BmsBoardRepository;
import com.example.A201.chatbot.domain.ChatLog;
import com.example.A201.chatbot.dto.ChatLogDto;
import com.example.A201.chatbot.repository.ChatLogRepository;
import com.example.A201.member.domain.Member;
import com.example.A201.member.repository.MemberRepository;
import com.example.A201.progress.repository.ProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatLogService {

    private final ChatLogRepository chatLogRepository;

    private final MemberRepository memberRepository;

    private final BmsBoardRepository bmsBoardRepository;

    private final ProgressRepository progressRepository;


    @Value("${openai.api.key}")
    private String openaiApiKey;

    public ChatLogDto getAnswerFromChatGPT(ChatLogDto request) {

        BmsBoard bms = bmsBoardRepository.findByProgress(request.getProgressId())
                .orElseThrow(() -> new EntityNotFoundException("해당 데이터 찾을 수 없습니다"));

        Optional<ChatLog> chatLog = chatLogRepository.findByProgressId(request.getProgressId());

        if (chatLog.isEmpty()) {
            System.out.println("progress empty");
            String hardcodedQuestion = String.format("당신은 배터리 전문가입니다. 다음 데이터를 통해 배터리 상태를 판단하세요: " +
                            "배터리 제조 날짜=%s, 배터리 보증 날짜=%s, 과전류 횟수=%d, 과전압 횟수=%d, 저전압 횟수=%d, 고온도 횟수=%d, 저온도 횟수=%d." +
                            "답변은 반드시 '불량' 또는 '정상'으로 하며, 이유는 반드시 80자 이내로 설명하세요. 이유는 한줄로 설명하세요. 정보 부족같은 답은 불가능합니다" +
                            "답변 형식은 분석 결과:, 분석 내용: 형식으로 하세요. 예외는 없습니다. 분석 결과에는 불량 또는 정상을, 분석 내용에는 이유를 쓰세요. 분석 결과를 적고 엔터 한번, 그리고 분석 내용를 적으세요 ex) 분석 결과: 정상 \\n 분석 내용:...." +
                            "중간에 빈 값이 존재 해도 티내지말고 주어진 정보로만 분석하세요. 이유에 정보 부족이라는 내용을 담지마세요. 부족하면 부족한대로 이유를 도출하세요." +
                            "과전류, 과전압, 저전압, 고온도, 저온도의 횟수는 어느 하나라도 5회가 넘으면 불량으로 판단되며 모든 횟수의 합이 10회가 넘는 것 또한 불량으로 판단합니다." +
                            "과전류, 과전압, 저전압, 고온도, 저온도 모두 1회 이상이면 불량으로 판단합니다." +
                            "기준의 횟수에 대한 자세한 내용은 분석 내용에 작성하지 마세요. 기준에 대한 내용은 작성하지 말고 불량으로 판단한 이유를 작성하세요.",
                    bms.getReceiveDate(),
                    bms.getReceiveDate().plusYears(2),
                    bms.getOverCurrentCount(),
                    bms.getOverVoltageCount(),
                    bms.getUnderVoltageCount(),
                    bms.getOverTemperatureCount(),
                    bms.getUnderTemperatureCount()
            );

            String botResponse = callOpenAIApi(hardcodedQuestion);
            Member member = memberRepository.findById(request.getMemberId())
                    .orElseThrow(() -> new IllegalArgumentException("Member not found!"));

            chatLogRepository.save(ChatLog.builder()
                    .member(member)
                    .userMessage(hardcodedQuestion)
                    .progress(progressRepository.findById(request.getProgressId()).orElse(null))
                    .botResponse(botResponse)
                    .build());

            return ChatLogDto.builder()
                    .botResponse(botResponse)
                    .build();
        }
        return ChatLogDto.builder()
                .botResponse(chatLog.get().getBotResponse()).build();
    }

    private String callOpenAIApi(String hardcodedQuestion) {
        // OpenAI API를 호출하고 응답을 반환하는 로직
        // 예를 들면, RestTemplate나 HttpClient 등의 도구를 사용하여
        // OpenAI endpoint에 POST 요청을 보내고 응답을 처리할 수 있습니다.
        final String ENDPOINT_URL = "https://api.openai.com/v1/chat/completions";
        final String AUTH_HEADER = "Authorization";
        final String AUTH_VALUE = "Bearer " + openaiApiKey;

        RestTemplate restTemplate = new RestTemplate();

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTH_HEADER, AUTH_VALUE);

        // 요청 바디 설정
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "system");
        userMessage.put("content", "You are a helpful assistant.");
        messages.add(userMessage);

        userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", hardcodedQuestion);
        messages.add(userMessage);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 250);  // 응답의 최대 토큰 수, 필요에 따라 조정 가능
        requestBody.put("model", "gpt-4-1106-preview");
        log.info("Sending request to OpenAI API with body: {}", requestBody);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(ENDPOINT_URL, HttpMethod.POST, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();
            log.info("Received response from OpenAI API: {}", responseBody);
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty() && choices.get(0).containsKey("message")) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    if (message.containsKey("content")) {
                        return (String) message.get("content");
                    }
                }
            }
            return "API response is not as expected.";
        } catch (RestClientException e) {
            // 로그 처리나 특별한 예외 처리를 여기에 추가할 수 있습니다.
            log.error("Error while calling the OpenAI API", e);
            return "Error while calling the API.";
        }
    }
}
