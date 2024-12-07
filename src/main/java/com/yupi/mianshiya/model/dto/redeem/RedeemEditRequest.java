package com.yupi.mianshiya.model.dto.redeem;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 编辑兑换请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class RedeemEditRequest implements Serializable {

    /**
     * id
     */
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
     * 是否使用
     */
    private Integer isUsed;

    /**
     * 过期时间
     */
    private Date expirationTime;


    private static final long serialVersionUID = 1L;
}