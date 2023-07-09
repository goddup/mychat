package com.ddup.chat.controller;

import com.ddup.chat.chatapi.util.HttpServletRequestToStringConverter;
import com.ddup.chat.chatapi.config.ConfigInfo;
import com.ddup.chat.chatapi.WeChatAPI;
import org.apache.commons.codec.digest.DigestUtils;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.Document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
/**
 * author:myddup
 * data:2023-05-10
 */
@RestController
public class WxController {



    @Autowired
    ConfigInfo configInfo;

    private static final WeChatAPI weChatAPI = new WeChatAPI();


/*
当普通微信用户向公众账号发消息时，微信服务器将 POST 消息的 XML 数据包到开发者填写的 URL 上。数据包格式如下
<xml>
  <ToUserName><![CDATA[toUser]]></ToUserName>
  <FromUserName><![CDATA[fromUser]]></FromUserName>
  <CreateTime>1348831860</CreateTime>
  <MsgType><![CDATA[text]]></MsgType>
  <Content><![CDATA[this is a test]]></Content>
  <MsgId>1234567890123456</MsgId>
  <MsgDataId>xxxx</MsgDataId>
  <Idx>xxxx</Idx>
</xml>*/


    /***
     * TODO 微信公众号，超时，重试3次，可以做个本地缓存，如缓存没有才调用chatGPT
     * 基础消息格式参考：https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Passive_user_reply_message.html#1
     * @param req
     * @param resp
     * @return
     * @throws IOException
     */
    @PostMapping("/wx/echo")
    public String echoPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 1.解析消息
        String reqString = HttpServletRequestToStringConverter.convertToString(req);
        System.out.println("####POST echo HttpServletRequest req="+reqString);
        Map<String, String> param = parseRequest(req.getInputStream());
        // 2.逻辑处理（可以根据自身逻辑进行处理，这里略）
        // ...........
        String userMessage =  param.get("Content");
        System.out.println("##POST userMessage="+userMessage);
        //3.回复消息
        String textMsg = "<xml>" +
                "<ToUserName><![CDATA["+ param.get("FromUserName")+"]]></ToUserName>" +
                "<FromUserName><![CDATA["+ param.get("ToUserName")+"]]></FromUserName>" +
                "<CreateTime>12345678</CreateTime>" +
                "<MsgType><![CDATA[text]]></MsgType>" +
                "<Content><![CDATA["+userMessage+"]]></Content>" +
                "</xml>";




        String response =  weChatAPI.getResponse(userMessage);
        System.out.println(response);

        // 3.回复消息
        textMsg = "<xml>" +
                "<ToUserName><![CDATA[" + param.get("FromUserName") + "]]></ToUserName>" +
                "<FromUserName><![CDATA[" + param.get("ToUserName") + "]]></FromUserName>" +
                "<CreateTime>12345678</CreateTime>" +
                "<MsgType><![CDATA[text]]></MsgType>" +
                "<Content><![CDATA[" + response + "]]></Content>" +
                "</xml>";

//        }

        if(userMessage.startsWith("画图，")) {
            textMsg = "<xml>\n" +
                    "<ToUserName><![CDATA[" + param.get("FromUserName") + "]]></ToUserName>" +
                    "<FromUserName><![CDATA[" + param.get("ToUserName") + "]]></FromUserName>" +
                    "  <CreateTime>12345678</CreateTime>\n" +
                    "  <MsgType><![CDATA[image]]></MsgType>\n" +
                    "  <Image>\n" +
                    "    <MediaId><![CDATA["+response+"]]></MediaId>\n" +
                    "  </Image>\n" +
                    "</xml>";
        }

      return textMsg;
    }

    // 利用dom4j中的类进行解析
    public  Map<String, String> parseRequest(InputStream is) {
        Map<String,String> map = new HashMap();
        // 1. 通过io流得到文档对象
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(is);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        // 2.通过文档对象得到根节点对象
        Element root = document.getRootElement();
        // 3.通过根节点对象获取所有子节点对象
        List<Element> elements = root.elements();
        // 4.将所有节点放入map
        for (Element element : elements) {
            map.put(element.getName(), element.getStringValue());
        }
        return map;
    }


    @GetMapping("/wx/echo")
    public String echo(HttpServletRequest req, HttpServletResponse resp) {
        // 1.获取微信传入的4个参数
        String reqString = HttpServletRequestToStringConverter.convertToString(req);
        System.out.println("####GET echo HttpServletRequest req="+reqString);
        String signature = req.getParameter("signature");
        String timestamp = req.getParameter("timestamp");
        String nonce = req.getParameter("nonce");
        String echostr = req.getParameter("echostr");
        // 2.用timestamp, nonce, signature进行校验
        boolean result = check(timestamp, nonce, signature);
        if (result) {
            // 3.校验成功返回echostr
            return echostr;
        }
        return "error!";
    }

    public boolean check(String timestamp, String nonce, String signature) {
        String TOKEN = this.configInfo.getImgToken();
//        String TOKEN ="tiger";
        // 1.按字典序对TOKEN, timestamp和nonce排序
        String[] arr = new String[]{TOKEN,timestamp,nonce};
        Arrays.sort(arr);
        // 2.将3个参数拼成一个字符串进行sha1加密
        String str = arr[0]+arr[1]+arr[2];
        // 3.用commons-codec包中的工具类进行sha1加密
        str = DigestUtils.sha1Hex(str);
        // 4.将加密后的字符串和signature比较
        System.out.println(signature);
        return str.equalsIgnoreCase(signature);
    }
}
