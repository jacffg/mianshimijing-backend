package com.yupi.mianshiya.model.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * AI 生成题目请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class QuestionAIGenerateRequest implements Serializable {

    /**
     * 题目类型，比如 Java
     */
    private String questionType;

    /**
     * 题目数量，比如 10
     */
    private int number = 5;

    private static final long serialVersionUID = 1L;
}