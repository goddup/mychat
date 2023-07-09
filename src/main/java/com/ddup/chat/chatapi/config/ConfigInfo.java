package com.ddup.chat.chatapi.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//注意按照lombok的plugin
@Configuration
@ConfigurationProperties(prefix = "api")
@Data
public class ConfigInfo {
    private String chatKey;
    private String chatUrl;
    private String chatModel;
    private String systemMessage;
    private String imgSize;
    private String imgChatUrl;
    private String imgChatKey;
    private int testFlag;
    private String imgAppId;
    private String imgSecret;
    private String imgToken;


}
