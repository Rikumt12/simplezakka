package com.example.simplezakka.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private Environment env;

    @InjectMocks
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("正常系: メールが正しく構築され送信される")
    void sendResetLink_WithValidInputs_ShouldSendEmailCorrectly() {
        String toEmail = "user@example.com";
        String resetLink = "https://example.com/reset?token=abc123";
        String fromEmail = "noreply@example.com";

        when(env.getProperty("spring.mail.username")).thenReturn(fromEmail);

        emailService.sendResetLink(toEmail, resetLink);

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertThat(sentMessage.getTo()).containsExactly(toEmail);
        assertThat(sentMessage.getFrom()).isEqualTo(fromEmail);
        assertThat(sentMessage.getSubject()).contains("パスワード再設定リンク");
        assertThat(sentMessage.getText()).contains(resetLink);
    }

    @Test
    @DisplayName("境界値: toEmail が空の場合でも send は呼び出される（仕様により失敗する可能性）")
    void sendResetLink_WithEmptyToEmail_ShouldFailOrSkipSending() {
        String toEmail = "";
        String resetLink = "https://example.com/reset";

        when(env.getProperty("spring.mail.username")).thenReturn("noreply@example.com");

        emailService.sendResetLink(toEmail, resetLink);

        verify(mailSender).send(any(SimpleMailMessage.class)); // 呼ばれるかどうかを確認（fail or skip は実装依存）
    }

    @Test
    @DisplayName("境界値: resetLink が空の場合、本文に空リンクが含まれる")
    void sendResetLink_WithEmptyResetLink_ShouldSendEmailWithEmptyLink() {
        String toEmail = "user@example.com";
        String resetLink = "";

        when(env.getProperty("spring.mail.username")).thenReturn("noreply@example.com");

        emailService.sendResetLink(toEmail, resetLink);

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();

        assertThat(message.getText()).contains(resetLink); // 空でも含まれている
    }

    @Test
    @DisplayName("異常系: 送信元アドレスが null の場合も送信される（SMTPエラーになる可能性）")
    void sendResetLink_WithNullFromAddress_ShouldHandleGracefully() {
        String toEmail = "user@example.com";
        String resetLink = "https://example.com/reset";

        when(env.getProperty("spring.mail.username")).thenReturn(null);

        emailService.sendResetLink(toEmail, resetLink);

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();

        assertThat(message.getFrom()).isNull(); // nullがセットされたまま
    }

    @Test
    @DisplayName("異常系: mailSender が例外をスローした場合")
    void sendResetLink_WhenMailSenderFails_ShouldThrowOrHandleException() {
        String toEmail = "user@example.com";
        String resetLink = "https://example.com/reset";

        when(env.getProperty("spring.mail.username")).thenReturn("noreply@example.com");
        doThrow(new RuntimeException("メール送信失敗")).when(mailSender).send(any(SimpleMailMessage.class));

        assertThatThrownBy(() -> emailService.sendResetLink(toEmail, resetLink))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("メール送信失敗");
    }
}
