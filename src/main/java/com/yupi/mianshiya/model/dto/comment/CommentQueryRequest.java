package com.yupi.mianshiya.model.dto.comment;

import com.yupi.mianshiya.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommentQueryRequest extends PageRequest implements Serializable {

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
    /**
     * 顶级父 ID，支持多级嵌套回复
     */
    private Long ancestorId;


    private static final long serialVersionUID = 1L;
}