FROM openjdk:8-jdk-alpine

# 安装Maven并设置环境变量
RUN apk add --no-cache maven
ENV MAVEN_HOME /usr/share/maven
ENV PATH $MAVEN_HOME/bin:$PATH

# 设置工作目录
WORKDIR /app

# 构建项目依赖
COPY pom.xml .
RUN mvn dependency:resolve

# 拷贝源代码
COPY src ./src
# 构建项目
RUN mvn package

#copy config
RUN mkdir /myconfig
COPY ./src/main/resources/application.properties /myconfig/

# startup container
#ENTRYPOINT ["java","-jar","/app/target/chat-0.0.2-SNAPSHOT.jar"]
ENTRYPOINT ["java","-jar","/app/target/chat-0.0.2-SNAPSHOT.jar","--spring.config.location=file:/myconfig/application.properties "]

 







