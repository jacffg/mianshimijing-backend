package com.yupi.mianshiya.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建题目请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class QuestionRelatedRequest implements Serializable {

    /**
     * 题目id
     */
    private Long id;
    /**
     * 要获取的题目数
     */
    private Integer num=8;



    private static final long serialVersionUID = 1L;
}