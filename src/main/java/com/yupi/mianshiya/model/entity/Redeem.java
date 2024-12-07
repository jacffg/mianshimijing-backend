package com.yupi.mianshiya.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 兑换表
 * @TableName redeem
 */
@TableName(value ="redeem")
@Data
public class Redeem implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 兑换码
     */
    private String code;

    /**
     * 使用用户 id
     */
    private Long userId;

    /**
     * 使用时间
     */
    private Date useTime;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 过期时间
     */
    private Date expirationTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否使用
     */
    private Integer isUsed;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}