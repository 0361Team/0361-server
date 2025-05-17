package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.QuestionDto;
import com._1.spring_rest_api.entity.Keyword;
import com._1.spring_rest_api.entity.Text;
import com._1.spring_rest_api.entity.Week;
import com._1.spring_rest_api.repository.KeywordRepository;
import com._1.spring_rest_api.repository.TextRepository;
import com._1.spring_rest_api.repository.WeekRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ClaudeService {

    private final ObjectMapper objectMapper;
    private final TextRepository textRepository;
    private final WeekRepository weekRepository;
    private final KeywordRepository keywordRepository;
    private final ChatModel chatModel;

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

    public String generateSummation(Long textId) {
        Text text = textRepository.findById(textId).orElseThrow(
                () -> new EntityNotFoundException("Text not found with id: " + textId));
        String content = text.getContent();

        if (content.isEmpty() || content.isBlank()) {
            throw new IllegalStateException("No text content found for week with id: " + textId);
        }

        if (content.length() > 30000) {
            content = content.substring(0, 30000);
        }

        return generateSummationByClaude(textId, content);
    }

    public List<String> generateKeywords(Long textId) {
        Text text = textRepository.findById(textId).orElseThrow(
                () -> new EntityNotFoundException("Text not found with id: " + textId));
        String content = text.getContent();

        if (content.isEmpty() || content.isBlank()) {
            throw new IllegalStateException("No text content found for week with id: " + textId);
        }

        if (content.length() > 30000) {
            content = content.substring(0, 30000);
        }

        return generateKeywordsByClaude(textId, content);
    }

    private List<String> generateKeywordsByClaude(Long textId, String content) {
        String cleanText = content
                .replace("\\n", "\n")
                .replaceAll("\\\\n", "\n");

        String systemPrompt = """
        당신은 교육 자료 분석 전문가입니다.
        주어진 교육 자료를 분석하여 가장 중요하고 핵심적인 키워드를 추출해주세요.
        
        【키워드 추출 규칙】
        1. 교육 자료의 핵심 주제와 개념을 대표하는 키워드를 선별하세요.
        2. 정확히 7개의 키워드를 추출해주세요.
        3. 각 키워드는 학습 내용의 중요 개념, 이론, 용어, 또는 기술을 대표해야 합니다.
        4. 키워드는 1-3단어의 간결한 형태로 제시하세요.
        5. 출력은 반드시 다음 JSON 형식을 엄격히 따라야 합니다:
        ["키워드1", "키워드2", "키워드3", "키워드4", "키워드5", "키워드6", "키워드7"]
        """;

        Message systemMessage = new SystemMessage(systemPrompt);
        UserMessage userMessage = new UserMessage(
                "다음 교육 자료에서 핵심 키워드 7개를 추출해주세요: \n\n" + cleanText
        );

        // Prompt 생성 및 AI 모델 호출
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        try {
            ChatResponse response = chatModel.call(prompt);
            String responseContent = response.getResult().getOutput().getText();

            List<String> keywords = objectMapper.readValue(
                    responseContent,
                    new TypeReference<List<String>>() {}
            );

            saveKeywords(textId, keywords);
            return keywords;
        } catch (Exception e) {
            throw new RuntimeException("AI 모델을 통한 키워드 추출에 실패했습니다: " + e.getMessage(), e);
        }
    }

    private void saveKeywords(Long textId, List<String> keywords) {
        Text text = textRepository.findById(textId).orElseThrow(
                () -> new EntityNotFoundException("Text not found with id: " + textId));

        deleteExistingKeywords(text);

        for (String keywordContent : keywords) {
            Keyword keyword = Keyword.builder()
                    .content(keywordContent)
                    .text(text)
                    .build();

            keywordRepository.save(keyword);
        }
    }

    // 기존 키워드 삭제 메서드 (옵션)
    private void deleteExistingKeywords(Text text) {
        List<Keyword> existingKeywords = keywordRepository.findAllByText(text);
        if (!existingKeywords.isEmpty()) {
            keywordRepository.deleteAll(existingKeywords);
        }
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

    private String generateSummationByClaude(Long textId, String text) {
        String cleanText = text
                .replace("\\n", "\n")
                .replaceAll("\\\\n", "\n");

        String systemPrompt = """
            당신은 교육 자료 요약 전문가이자 최고의 학습 코치입니다.
            주어진 교육 자료를 면밀히 분석하여 학생들이 수업 내용을 효과적으로 복습하고 핵심 개념을 명확하게 이해할 수 있는 고품질 요약을 제공합니다.
            
            【요약 규칙】
            1. 핵심 개념 강조: 교육 자료에서 가장 중요한 개념과 원리를 식별하여 강조하세요. 특히 시험이나 실무에서 중요할 수 있는 핵심 내용을 우선시하세요.
            
            2. 구조화된 요약: 
               - 최상단에 핵심 키워드와 주요 개념을 3-5개 리스트로 제시
               - 본문은 논리적 흐름에 따라 섹션별로 구분하여 요약
               - 복잡한 개념은 단계별로 설명하여 이해하기 쉽게 구성
            
            3. 전문 용어 명확화: 모든 전문 용어에 간결한 정의를 제공하고, 가능하면 실제 적용 사례나 예시를 포함하세요.
            
            4. 개념 간 연결성: 다른 개념이나 이전/다음 수업과의 연결성을 명시하여 학생들이 지식을 통합적으로 이해할 수 있도록 하세요.
            
            5. 실용적 요약:
               - 이론적 개념이 실제 어떻게 적용되는지 간략한 예시 포함
               - 학생들이 자주 혼동하는 부분이나 주의해야 할 함정 강조
               - 코드 예제가 있다면 가장 중요한 부분만 간결하게 포함
            
            6. 학습 포인트 추가: 요약 말미에 "핵심 학습 포인트"를 3-5개 제시하여 학생들이 무엇에 집중해야 하는지 명확히 안내하세요.
            
            7. 분량 및 포맷:
               - 원본의 약 25% 분량으로 작성하되, 핵심 정보는 모두 포함
               - 중요 개념은 굵은 글씨로 강조
               - 번호 매기기와 글머리 기호를 적절히 사용하여 가독성 향상
               - 단락 간 논리적 흐름 유지
            
            이 요약은 학생들이 복습 시간을 최적화하고, 핵심 개념을 빠르게 파악하며, 수업 내용을 장기 기억으로 전환하는 데 도움이 되어야 합니다. 학생들이 이 요약만으로도 주요 개념을 이해하고 응용할 수 있는 수준으로 작성하세요.
            """;

        Message systemMessage = new SystemMessage(systemPrompt);
        UserMessage userMessage = new UserMessage(
                "다음 교육 자료를 요약해주세요: \n\n" + cleanText
        );

        // Prompt 생성 및 AI 모델 호출
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        try {
            ChatResponse response = chatModel.call(prompt);
            String responseContent = response.getResult().getOutput().getText();
            saveSummation(textId, responseContent);
            return responseContent;
        } catch (Exception e) {
            throw new RuntimeException("AI 모델을 통한 텍스트 요약에 실패했습니다: " + e.getMessage(), e);
        }
    }

    private void saveSummation(Long textId, String content) {
        Text text = textRepository.findById(textId).orElseThrow(
                () -> new EntityNotFoundException("Text not found with id: " + textId));
        text.setSummation(content);
        textRepository.save(text);
    }
}