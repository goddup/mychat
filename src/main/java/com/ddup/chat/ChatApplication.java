package com.ddup.chat;

import com.ddup.chat.chatapi.util.SpringContextUtils;
import com.ddup.chat.chatapi.config.ConfigInfo;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.DependsOn;

@SpringBootApplication
@DependsOn("springContextUtils")//延迟加载springContextUtils类，避免出现null
public class ChatApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ChatApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ConfigInfo configInfo = SpringContextUtils.getBean(ConfigInfo.class);
		String conf = JSONObject.valueToString(configInfo);
		System.out.println("configInfo :" + conf);
	}
}
