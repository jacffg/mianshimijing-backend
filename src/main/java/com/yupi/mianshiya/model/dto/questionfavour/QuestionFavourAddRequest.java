package com.yupi.mianshiya.model.dto.questionfavour;

import lombok.Data;

import java.io.Serializable;

/**
 * 题目收藏 / 取消收藏请求

 */
@Data
public class QuestionFavourAddRequest implements Serializable {

    /**
     * 题目 id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}