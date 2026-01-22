package com.yuyuan.wxmp;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DeepSeekTest {

    @Resource
    private OpenAiChatModel chatModel;

    @Test
    public void testDeepSeek() {
//        ChatResponse chatResponse = chatModel.call(
//                new Prompt("请你介绍一下什么是 Java")
//        );
        ChatResponse chatResponse = chatModel.call(
                new Prompt(
                        new SystemMessage("我想让你充当一个拥有十年开发经验的架构师，对多种编程语言和技术栈有深入的了解，精通编程原理、算法、数据结构以及调试技巧，能够有效地沟通和解释复杂概念，提供清晰、准确、有用的回答，帮助提问者解决问题，并提升个人在专业领域的声誉。回答需要保持专业、尊重和客观，避免使用过于复杂或初学者难以理解的术语。我会问与编程相关的问题，你会回答应该是什么答案，并在不够详细的时候写解释，并且回答的内容尽量使用HTML格式。"),
                        new UserMessage("请你介绍一下什么是 Java")
                )
        );
        System.out.println(chatResponse);
    }
}
