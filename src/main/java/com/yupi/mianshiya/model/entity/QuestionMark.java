package com.yupi.mianshiya.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 题目标记
 * @TableName question_mark
 */
@TableName(value ="question_mark")
@Data
public class QuestionMark implements Serializable {
    /**
     * 标记 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}