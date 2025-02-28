package com.yupi.mianshiya.model.dto.statistic;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 万佳羊
 * {@code @date}  2024-11-09  16:00
 * @version 1.0
 */
@Data
public class QuestionViewCountDTO implements Serializable {

    // 题目名称
    private String question;
    // 浏览量
    private String viewNum;
    private static final long serialVersionUID = 1L;
}

