package com.yupi.mianshiya.config;

import com.zhipu.oapi.ClientV4;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 万佳羊
 * {@code @date}  2024-11-28  14:25
 * @version 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
public class AiConfig {

    /**
     * apiKey ,需要从开发平台获取
     */
    private String apiKey;
    @Bean
    public ClientV4 getClientV4(){
        return new ClientV4.Builder(apiKey).build();
    }


}
