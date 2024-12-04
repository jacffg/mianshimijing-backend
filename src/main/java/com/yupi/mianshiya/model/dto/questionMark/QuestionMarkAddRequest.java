package com.yupi.mianshiya.model.dto.questionMark;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建题目标记请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class QuestionMarkAddRequest implements Serializable {


    /**
     * 题目 ID
     */
    private Long questionId;


    /**
     * 标记类型
     */
    private String markType;


    private static final long serialVersionUID = 1L;
}