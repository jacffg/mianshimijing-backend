package com.yupi.mianshiya.config;

import com.volcengine.ark.runtime.service.ArkService;
import lombok.Data;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

/**
 * @author 万佳羊
 * {@code @date}  2025-02-27  11:18
 * @version 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "deepseek")
@Data
public class DeepSeekAiConfig {
    // ApiKey
    private String apiKey;

    /**
     * AI 请求客户端
     *
     * @return
     */
    @Bean
    public ArkService aiService() {
        ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
        Dispatcher dispatcher = new Dispatcher();
        ArkService service = ArkService.builder().dispatcher(dispatcher).connectionPool(connectionPool)
                .baseUrl("https://ark.cn-beijing.volces.com/api/v3")
                .apiKey(apiKey)
                .build();
        return service;
    }
}
