package com.ddup.chat.chatapi;




import com.ddup.chat.chatapi.config.ConfigInfo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 * author:myddup
 * data:2023-05-10
 */
@Service
public class ChatGPTAPI {

    private static  String chatUrl ;
    private static  String chatKey ;
    private static  String chatModel;


    private ConfigInfo configInfo ;

    public ChatGPTAPI(ConfigInfo configInfo){
        this.configInfo = configInfo;
        //init
        this.chatUrl = this.configInfo.getChatUrl();
        this.chatKey = this.configInfo.getChatKey();
        this.chatModel = this.configInfo.getChatModel();


    }
    public ChatGPTAPI(){

    }


    public  String chat( String userMessage) throws IOException {

        // 构造 JSON 请求体
        String requestBody = String.format("{\n" +
                "    \"model\": \"%s\",\n" +
                "    \"messages\": [\n" +
//                "        {\"role\": \"system\", \"content\": \"%s\"},\n" +
                "        {\"role\": \"user\", \"content\": \"%s\"}\n" +
                "    ],\n" +
                "    \"n\": 1\n" +
                "}",chatModel, userMessage);


//        URL url = new URL(this.configInfo.getChatUrl());
        URL url = new URL(chatUrl);
        HttpURLConnection con = null;
        // 创建 HTTP 连接对象
        if(this.configInfo.getTestFlag() == 0) {
            // 禁用系统默认的代理设置
            System.setProperty("java.net.useSystemProxies", "false");
            con = (HttpURLConnection) url.openConnection();
        }else{
            // 设置代理服务器地址和端口号
            String proxyHost = "127.0.0.1";
            int proxyPort = 8001;
            // 创建代理服务器对象
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            con = (HttpURLConnection) url.openConnection(proxy);
        }

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + chatKey);


        System.out.println("requestBody : "+requestBody);
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(requestBody.getBytes());
        os.flush();
        os.close();

        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public  String  extract(String str) {
        JSONObject jsonObject = new JSONObject(str);
        JSONArray choices = jsonObject.getJSONArray("choices");
        JSONObject messageObj = choices.getJSONObject(0).getJSONObject("message");

        String message = messageObj.getString("content");
        return message;
    }

    public  String getResponse( String userMessage) throws IOException{
        String response = chat( userMessage);
        String message = extract(response);
        return message;
    }


    public static void main(String[] args) throws IOException {
        String systemMessage = "你是客服顾问，女生，30岁，有丰富的保险客服经验。";
        String userMessage = "你是谁，有啥特长。";
        ChatGPTAPI chat = new ChatGPTAPI();
        String response = chat.chat(userMessage);
        System.out.println(response);
        String message = chat.extract(response);
        System.out.println(message);
    }


}
