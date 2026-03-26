package com.codeit.weatherfit.global.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class S3ControllerTest {

    @Mock
    private LogS3Service logS3Service;

    @InjectMocks
    private S3Controller s3Controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(s3Controller).build();
    }

    @Nested
    @DisplayName("POST /api/s3")
    class UploadLog {

        @Test
        @DisplayName("성공 - 로그 업로드 서비스를 호출한다")
        void success() throws Exception {
            // given
            doNothing().when(logS3Service).uploadLogFile();

            // when
            mockMvc.perform(post("/api/s3"))
                    .andExpect(status().isCreated());

            // then
            verify(logS3Service).uploadLogFile();
        }

        @Test
        @DisplayName("실패 - 로그 업로드 중 예외 발생 시 ServletException으로 전파된다")
        void failure() {
            // given
            doThrow(new RuntimeException("로그 디렉토리 접근 실패"))
                    .when(logS3Service).uploadLogFile();

            // when & then
            assertThatThrownBy(() -> mockMvc.perform(post("/api/s3")))
                    .hasCauseInstanceOf(RuntimeException.class);
        }
    }
}
