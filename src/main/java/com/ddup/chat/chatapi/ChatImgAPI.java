package com.ddup.chat.chatapi;

import com.ddup.chat.chatapi.config.ConfigInfo;
import com.ddup.chat.chatapi.util.MediaUtil;
import org.json.JSONArray;
import org.json.JSONObject;

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
public class ChatImgAPI {
    private static  String chatUrl ;
    private static  String chatKey ;
    private static  String imgSize ;
    private ConfigInfo configInfo ;

    public ChatImgAPI(ConfigInfo configInfo){
        this.configInfo = configInfo;
        //init
        this.chatUrl = this.configInfo.getImgChatUrl();
        this.chatKey = this.configInfo.getImgChatKey();
        this.imgSize = this.configInfo.getImgSize();


    }


    public  String chat( String chatPrompt) throws IOException {

        // 构造 JSON 请求体
        String requestBody = String.format("{\n" +
                "    \"prompt\": \"%s\",\n" +
                "    \"size\": \"%s\",\n" +
                "    \"n\": 1\n" +
                "}",chatPrompt,imgSize);
        URL url = new URL(chatUrl);
        HttpURLConnection con = null;

        // 创建 HTTP 连接对象
        if(this.configInfo.getTestFlag() == 0) {
            // 禁用系统默认的代理设置
            System.setProperty("java.net.useSystemProxies", "false");
            con = (HttpURLConnection) url.openConnection();
        }else {
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
        JSONArray choices = jsonObject.getJSONArray("data");
        String message = choices.getJSONObject(0).getString("url");
        return message;
    }

    public  String getResponse( String userMessage) throws IOException{
        String response = chat( userMessage);
        String message = extract(response);
        //上传图片，得到media_id
        String mediaId = MediaUtil.getMediaID(this.configInfo.getImgAppId(),this.configInfo.getImgSecret(),message);

        return mediaId;

    }


    public static void main(String[] args) throws IOException {

//        String userMessage = "碧海蓝天";
//        ChatImgAPI chat = new ChatImgAPI();
//        String img = chat.getResponse(userMessage);
//        System.out.println(img);
    }
}
