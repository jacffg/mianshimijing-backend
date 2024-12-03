package com.yupi.mianshiya.model.dto.comment;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class CommentUpdateRequest implements Serializable {

    /**
     * 评论 ID
     */
    private Long id;

    /**
     * 题目 ID
     */
    private Long questionId;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父评论 ID，支持多级嵌套回复
     */
    private Long parentId;


    private static final long serialVersionUID = 1L;
}