package com.yupi.mianshiya.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName comment
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MyCommentVO implements Serializable {
    /**
     * 评论 ID
     */
    private Long id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 题目 ID
     */
    private Long questionId;
    /**
     * 题目 题号
     */
    private Long questionNum;
    /**
     * 题目标题
     */
    private String questionTitle;

    /**
     * 更新时间
     */
    private Date updateTime;


}
