package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.QuestionDto;
import com._1.spring_rest_api.entity.Text;
import com._1.spring_rest_api.entity.Week;
import com._1.spring_rest_api.repository.TextRepository;
import com._1.spring_rest_api.repository.WeekRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ClaudeService {

    private final ObjectMapper objectMapper;
    private final TextRepository textRepository;
    private final WeekRepository weekRepository;
    private final ChatModel chatModel;

    @Autowired
    public ClaudeService(TextRepository textRepository, WeekRepository weekRepository, ChatModel chatModel) {
        this.objectMapper = new ObjectMapper();
        this.textRepository = textRepository;
        this.weekRepository = weekRepository;
        this.chatModel = chatModel;
    }

    /**
     * 주차 ID를 기반으로 해당 주차의 텍스트 내용을 가져와 질문을 생성합니다.
     */
    public List<QuestionDto> generateQuestionsFromWeekTexts(Long weekId, int minQuestionCount) {
        // 주차 확인
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Week not found with id: " + weekId));

        // 해당 주차의 텍스트 컨텐츠 가져오기
        List<Text> texts = textRepository.findAllByWeekId(weekId);

        if (texts.isEmpty()) {
            throw new IllegalStateException("No text content found for week with id: " + weekId);
        }

        // 모든 텍스트 내용을 하나의 문자열로 결합
        String combinedContent = texts.stream()
                .map(Text::getContent)
                .collect(Collectors.joining("\n\n"));

        // 텍스트가 너무 길 경우 처리 (AI 모델의 최대 토큰 제한을 고려)
        if (combinedContent.length() > 30000) {
            combinedContent = combinedContent.substring(0, 30000);
        }

        return generateQuestionsFromContent(combinedContent, minQuestionCount);
    }

    /**
     * 주어진 텍스트 내용을 기반으로 질문을 생성합니다.
     */
    private List<QuestionDto> generateQuestionsFromContent(String content, int minQuestionCount) {

        String systemPrompt = """
                당신은 교육용 퀴즈 생성 전문가입니다. 
                주어진 학습 자료를 분석하여 학습자의 이해도를 평가할 수 있는 고품질의 퀴즈 문제를 생성해야 합니다.
                각 퀴즈는 질문(front)과 답변(back)으로 구성됩니다.
                
                규칙:
                1. 학습 자료의 핵심 개념과 중요 정보를 중심으로 질문을 생성하세요.
                2. 쉬운 문제부터 어려운 문제까지 다양한 난이도의 질문을 만드세요.
                3. 질문은 명확하고 간결하게 작성하세요.
                4. 답변은 충분히 자세하게 설명하되, 핵심 내용만 포함하세요.
                5. 최소 %d개 이상의 질문을 생성하세요.
                6. 출력은 반드시 다음 JSON 형식을 엄격히 따라야 합니다:
                [
                  <\\{
                    "front": "질문 내용",
                    "back": "답변 내용"
                  \\}>
                  ...
                ]
                """.formatted(minQuestionCount);

        Message systemMessage = new SystemMessage(systemPrompt);
        UserMessage userMessage = new UserMessage(
                "다음 학습 자료를 기반으로 퀴즈 문제를 생성해주세요: \n\n" + content
        );

        // Prompt 생성 및 AI 모델 호출
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        // 모델 파라미터 설정
        AnthropicChatOptions options = AnthropicChatOptions.builder()
                .temperature(0.7)
                .maxTokens(4000)
                .build();

        try {
            ChatResponse response = chatModel.call(prompt);
            String responseContent = response.getResult().getOutput().getText();

            // JSON 파싱
            return objectMapper.readValue(
                    responseContent,
                    new TypeReference<List<QuestionDto>>() {}
            );
        } catch (Exception e) {
            throw new RuntimeException("AI 모델을 통한 질문 생성에 실패했습니다: " + e.getMessage(), e);
        }
    }
}