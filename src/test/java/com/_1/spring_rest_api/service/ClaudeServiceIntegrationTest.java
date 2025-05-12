package com._1.spring_rest_api.service;

import com._1.spring_rest_api.api.dto.QuestionDto;
import com._1.spring_rest_api.entity.Text;
import com._1.spring_rest_api.entity.Week;
import com._1.spring_rest_api.repository.TextRepository;
import com._1.spring_rest_api.repository.WeekRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test") // application-test.properties 설정 사용
class ClaudeServiceIntegrationTest {

    @MockBean
    private TextRepository textRepository;

    @MockBean
    private WeekRepository weekRepository;

    @Autowired
    private ClaudeService claudeService;

    @Test
    @DisplayName("실제 AI 모델과 연동하여 질문 생성 테스트")
    void generateQuestionsFromWeekTexts_WithRealAIModel() {
        // Given
        Long weekId = 1L;
        int minQuestionCount = 3; // 최소 3개의 질문 요청

        // 주차 모킹
        Week week = Week.builder()
                .id(weekId)
                .title("자바 프로그래밍 기초")
                .build();

        // 학습 자료 텍스트 준비 (실제 내용 기반)
        List<Text> texts = new ArrayList<>();
        Text text = Text.builder()
                .id(1L)
                .content("""
                        자바(Java)는 썬 마이크로시스템즈의 제임스 고슬링(James Gosling)이 개발한 객체 지향적 프로그래밍 언어입니다.
                        
                        자바의 주요 특징:
                        1. 객체 지향(Object-Oriented): 자바는 객체 지향 언어로, 클래스와 객체 개념을 중심으로 설계되었습니다.
                        2. 플랫폼 독립성(Platform Independence): "한 번 작성하면, 어디서나 실행(Write Once, Run Anywhere)"이라는 목표로, JVM을 통해 어떤 플랫폼에서도 동일하게 동작합니다.
                        3. 가비지 컬렉션(Garbage Collection): 메모리 관리를 자동으로 처리하여 개발자가 명시적으로 메모리를 해제할 필요가 없습니다.
                        4. 강력한 타입 체크(Strong Typing): 컴파일 시 타입을 체크하여 런타임 오류를 줄입니다.
                        5. 다중 스레드(Multi-threaded): 동시 작업 처리를 위한 스레드 기능을 제공합니다.
                        
                        자바의 핵심 개념:
                        - 클래스(Class): 객체의 설계도 역할을 하며, 데이터와 메서드를 포함합니다.
                        - 객체(Object): 클래스의 인스턴스로, 실제 메모리에 할당된 실체입니다.
                        - 상속(Inheritance): 기존 클래스의 특성을 새로운 클래스가 물려받는 개념입니다.
                        - 다형성(Polymorphism): 같은 메서드가 객체에 따라 다르게 동작할 수 있습니다.
                        - 캡슐화(Encapsulation): 데이터와 메서드를 하나의 단위로 묶고, 외부로부터 접근을 제한합니다.
                        - 추상화(Abstraction): 복잡한 구현 세부 사항을 숨기고 중요한 개념만 표현합니다.
                        """)
                .build();
        texts.add(text);

        when(weekRepository.findById(weekId)).thenReturn(Optional.of(week));
        when(textRepository.findAllByWeekId(weekId)).thenReturn(texts);

        // When
        List<QuestionDto> questions = claudeService.generateQuestionsFromWeekTexts(weekId, minQuestionCount);

        // Then
        System.out.println("=== 실제 AI 모델로 생성된 질문 목록 ===");
        for (int i = 0; i < questions.size(); i++) {
            QuestionDto question = questions.get(i);
            System.out.println("질문 " + (i+1) + ": " + question.getFront());
            System.out.println("답변 " + (i+1) + ": " + question.getBack());
            System.out.println();
        }

        // 검증
        assertNotNull(questions, "생성된 질문 목록은 null이 아니어야 합니다");
        assertTrue(questions.size() >= minQuestionCount, "최소 " + minQuestionCount + "개 이상의 질문이 생성되어야 합니다");

        // 모든 질문이 front와 back을 가지고 있는지 확인
        for (QuestionDto question : questions) {
            assertNotNull(question.getFront(), "질문 내용은 null이 아니어야 합니다");
            assertFalse(question.getFront().isEmpty(), "질문 내용은 비어있지 않아야 합니다");
            assertNotNull(question.getBack(), "답변 내용은 null이 아니어야 합니다");
            assertFalse(question.getBack().isEmpty(), "답변 내용은 비어있지 않아야 합니다");
        }

        // 리포지토리 메서드 호출 확인
        verify(weekRepository).findById(weekId);
        verify(textRepository).findAllByWeekId(weekId);
    }
}