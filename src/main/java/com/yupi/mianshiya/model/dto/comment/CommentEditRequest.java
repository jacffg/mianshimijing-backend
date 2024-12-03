package com.yupi.mianshiya.model.dto.comment;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class CommentEditRequest implements Serializable {

    /**
     * 评论 ID
     */
    private Long id;


    /**
     * 评论内容
     */
    private String content;



    private static final long serialVersionUID = 1L;
}