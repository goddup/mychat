# Getting Started


## application.properties 设置自己的openai账号信息
```
#服务端口
server.port = 80

#接入微信公众号聊天相关的配置

#openAI 聊天 API
api.chatUrl = https://api.openai.com/v1/chat/completions
#openAI API key
api.chatKey = sk-xx
#openAI 模型版本
api.chatModel = gpt-3.5-turbo

#如果只是离聊天，不需要在微信公众号画图，图片相关属性可以不配置
#openAI 画图 API
api.imgChatUrl = https://api.openai.com/v1/images/generations
#openAI API key，可以和聊天OpenAI API key一样，也可以填其他的，画图API使用的token比较多
api.imgChatKey = sk-xx
#图片尺寸设置，可以三种类型 256x256 512x512 1024x1024
api.imgSize = 256x256


# 微信公众号相关的配置，具体可以网上查询怎么获取如下信息
#微信公众号的AppID
api.imgAppId = wx-xx
#微信公众号的Secret
api.imgSecret = exx
#微信公众号的token，这个可以自己设置
api.imgToken = tiger

#testFlag 1-测试 0-生产
api.testFlag = 1
#api.systemMessage =

```

## 原始编译打包方式   

### mvn 编译 执行 
```
nohup java -jar chat-0.0.2-SNAPSHOT.jar  &

#nohup java -jar chat-0.0.2-SNAPSHOT.jar --spring.config.location=file:/root/application.properties &
```

## 源码构建镜像方式

### 构建镜像
```
docker build -t chat:0.0.1 . 
```
### 执行镜像
```
docker run -p 80:80 --name chat  -d chat:0.0.1
 ```
### 挂载路径需要是绝对路径，修改配置文件 application.properties 设置自己的openai账号信息
```
docker run -v /ddup/chat/application.properties:/myconfig/application.properties  -p 80:80 --name chat  -d chat:0.0.1
```


## 最简单方式，直接使用远端镜像方式 application.properties 设置自己的openai账号信息
```
docker run -v /ddup/chat/application.properties:/myconfig/application.properties  -p 80:80 --name chat  -d myddup/mychat:0.0.1
```

### 测试服务
```
http://localhost/api/hello?msg=hello
```

## 问题支持
可以访问如下飞书文档，有相关联系方式

https://tva7nc4q9jf.feishu.cn/wiki/AmDXwlKtzii4FHkclVhcBxWunwc
