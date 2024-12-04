package com.yupi.mianshiya.model.dto.questionMark;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新题目标记请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class QuestionMarkUpdateRequest implements Serializable {

    /**
     * 标记 ID
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
     * 标记类型
     */
    private String markType;

    private static final long serialVersionUID = 1L;
}