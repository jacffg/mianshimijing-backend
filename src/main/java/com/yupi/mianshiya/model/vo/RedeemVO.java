package com.yupi.mianshiya.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.yupi.mianshiya.model.entity.Redeem;
import com.yupi.mianshiya.model.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 兑换视图
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class RedeemVO implements Serializable {

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
     * 使用时间
     */
    private UserVO user;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 封装类转对象
     *
     * @param redeemVO
     * @return
     */
    public static Redeem voToObj(RedeemVO redeemVO) {
        if (redeemVO == null) {
            return null;
        }
        Redeem redeem = new Redeem();
        BeanUtils.copyProperties(redeemVO, redeem);
        return redeem;
    }

    /**
     * 对象转封装类
     *
     * @param redeem
     * @return
     */
    public static RedeemVO objToVo(Redeem redeem) {
        if (redeem == null) {
            return null;
        }
        RedeemVO redeemVO = new RedeemVO();
        BeanUtils.copyProperties(redeem, redeemVO);
        return redeemVO;
    }
}
