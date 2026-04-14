package com.codeit.weatherfit.domain.recommendation;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class AiConfig {

    @Value("${spring.ai.openai.api-key}")
    private String OPENROUTER_API_KEY;
    @Value("${spring.ai.openai.baseurl}")
    private String OPENAI_BASE_URL;
    @Value("${spring.ai.openai.chat.options.model}")
    private String MODEL;

    @Bean
    public ChatClient.Builder chatClientBuilder(OpenAiChatModel model) {
        return ChatClient.builder(model);
    }

    @Bean
    public OpenAiChatModel openAiChatModel(OpenAiApi api) {
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(
                        org.springframework.ai.openai.OpenAiChatOptions.builder()
                                .model(MODEL)
//                                .temperature(0.3)
//                                .maxCompletionTokens(1000)
                                .build()
                )
                .build();
    }

    @Bean
    public OpenAiApi openAiApi() {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofSeconds(60));    // 읽기 타임아웃 60초 (LLM 응답 대기용)
//        requestFactory.setConnectTimeout(Duration.ofSeconds(10)); // 연결 타임아웃 10초

        // 2. 위에서 만든 설정을 사용하는 RestClient.Builder 생성
        RestClient.Builder restClientBuilder = RestClient.builder()
                .requestFactory(requestFactory)
                .defaultHeader("HTTP-Referer", "https://weatherfit.cloud")
                .defaultHeader("X-Title", "WeatherFit");
        return OpenAiApi.builder()
                .apiKey(OPENROUTER_API_KEY)
                .baseUrl(OPENAI_BASE_URL)
                .restClientBuilder(restClientBuilder)
                .build();
    }
}