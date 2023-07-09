package com.ddup.chat.chatapi;

import com.ddup.chat.chatapi.config.ConfigInfo;
import com.ddup.chat.chatapi.util.SpringContextUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.context.annotation.DependsOn;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * author:myddup
 * data:2023-05-10
 */
@DependsOn("springContextUtils")
public class WeChatAPI {
    private static final int CACHE_EXPIRY = 600; // 缓存过期时间（秒）

    private LoadingCache<String, CacheItem> cache; // 缓存

    private ChatGPTAPI chatGPTAPI; // ChatGPT API客户端
    private ChatImgAPI chatImgAPI; // ChatGPT API客户端

    ConfigInfo configInfo = SpringContextUtils.getBean(ConfigInfo.class);

//    private ConfigInfo configInfo ;
    public WeChatAPI() {
//        this.configInfo = configInfo;
        this.cache = CacheBuilder.newBuilder().maximumSize(10000) // 最大缓存数量
                .expireAfterWrite(CACHE_EXPIRY, TimeUnit.SECONDS)
                .build(new CacheLoader<String, CacheItem>() {
                    @Override
                    public CacheItem load(String key) {
                        return callAPI(key);
                    }
                });
        this.chatGPTAPI = new ChatGPTAPI(configInfo); // 创建ChatGPT API客户端
        this.chatImgAPI = new ChatImgAPI(configInfo); // 创建ChatImg API客户端
    }

    public String getResponse(String key) {
        try {
            CacheItem cacheItem = cache.get(key); // 从缓存中获取响应
            if (cacheItem != null) {
                return cacheItem.getResponse();
            } else {
                // 缓存项为空，需要重新调用 API
                return callAPI(key).getResponse();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private CacheItem callAPI(String key) {
        try {

            String response = "";
            if(key.startsWith("画图，")){
                response = chatImgAPI.getResponse(key);
            }else{
                response = chatGPTAPI.getResponse(key);
            }
            return new CacheItem(response);
        } catch (IOException e) {
            e.printStackTrace();
            // 调用 ChatGPT API 发生异常，返回包含错误信息的 CacheItem
            return new CacheItem("error: " + e.getMessage());
        }
    }
    private static class CacheItem {
        private String response;

        public CacheItem(String response) {
            this.response = response;
        }

        public String getResponse() {
            return response;
        }
    }

}
