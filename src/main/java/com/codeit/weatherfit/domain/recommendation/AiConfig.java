package com.codeit.weatherfit.domain.recommendation;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        return OpenAiApi.builder()
                .apiKey(OPENROUTER_API_KEY)
                .baseUrl(OPENAI_BASE_URL)
                .build();
    }
}