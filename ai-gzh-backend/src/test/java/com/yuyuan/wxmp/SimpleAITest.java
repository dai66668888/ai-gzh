package com.yuyuan.wxmp;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SimpleAITest {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SimpleAITest.class, args);
        
        OpenAiChatModel chatModel = context.getBean(OpenAiChatModel.class);
        
        try {
            System.out.println("正在测试AI连接...");
            
            ChatResponse chatResponse = chatModel.call(
                    new Prompt(new UserMessage("请简要介绍一下Java语言"))
            );
            
            System.out.println("AI连接成功！");
            System.out.println("AI回复:");
            System.out.println(chatResponse.getResult().getOutput().getText());
            
        } catch (Exception e) {
            System.err.println("AI连接失败！");
            System.err.println("错误信息: " + e.getMessage());
            e.printStackTrace();
        } finally {
            context.close();
        }
    }
}