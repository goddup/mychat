package com.ddup.chat.controller;


import com.ddup.chat.chatapi.WeChatAPI;
import com.ddup.chat.chatapi.util.HttpServletRequestToStringConverter;
import com.ddup.chat.chatapi.config.ConfigInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
/**
 * author:myddup
 * data:2023-05-10
 */
@RestController
public class ChatController {

    @Autowired
    ConfigInfo configInfo;

/*WeChatAPI类的缓存只是在内存中的，如果应用程序重启，则缓存也会被清空。
另外，如果应用程序运行在多个节点上，则不同节点之间的缓存是不共享的，
 需要使用一些分布式缓存方案来解决这个问题。
 TODO
 */
    private static final WeChatAPI weChatAPI = new WeChatAPI();

    @GetMapping("/api/hello")
    public String sayHello(HttpServletRequest req) {
//        String systemMessage = "水滴保的客服顾问，女生，30岁，有丰富的保险客服经验。";
        String reqString = HttpServletRequestToStringConverter.convertToString(req);
        System.out.println("####GET echo HttpServletRequest req="+reqString);
        String userMessage = "帮我复述一下这个句子：我现在不方便。";


        String msg = req.getParameter("msg");
        if(!"".equals(msg)){
            userMessage =msg;
        }

        String response =  weChatAPI.getResponse(userMessage);
        System.out.println(response);

        return response;

    }


    @GetMapping("/api/img")
    public String imgHello(HttpServletRequest req) {
        String reqString = HttpServletRequestToStringConverter.convertToString(req);
        System.out.println("####GET echo HttpServletRequest req="+reqString);
        String userMessage = "画图，碧海蓝天";

        String msg = req.getParameter("msg");
        if(!"".equals(msg)){
            userMessage =msg;
        }
        String response =  weChatAPI.getResponse(userMessage);
        System.out.println(response);

        return response;

    }

}
