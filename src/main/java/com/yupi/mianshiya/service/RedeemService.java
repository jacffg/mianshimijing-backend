package com.yupi.mianshiya.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.mianshiya.model.dto.redeem.ExchangeRequest;
import com.yupi.mianshiya.model.dto.redeem.RedeemQueryRequest;
import com.yupi.mianshiya.model.entity.Redeem;
import com.yupi.mianshiya.model.vo.RedeemVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 兑换服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface RedeemService extends IService<Redeem> {

    /**
     * 校验数据
     *
     * @param redeem
     * @param add 对创建的数据进行校验
     */
    void validRedeem(Redeem redeem, boolean add);

    /**
     * 获取查询条件
     *
     * @param redeemQueryRequest
     * @return
     */
    QueryWrapper<Redeem> getQueryWrapper(RedeemQueryRequest redeemQueryRequest);
    
    /**
     * 获取兑换封装
     *
     * @param redeem
     * @param request
     * @return
     */
    RedeemVO getRedeemVO(Redeem redeem, HttpServletRequest request);

    /**
     * 分页获取兑换封装
     *
     * @param redeemPage
     * @param request
     * @return
     */
    Page<RedeemVO> getRedeemVOPage(Page<Redeem> redeemPage, HttpServletRequest request);
    /**
     * 兑换
     *
     * @param exchangeRequest
     * @param request
     * @return
     */
    Boolean exchangeCode(ExchangeRequest exchangeRequest, HttpServletRequest request);
}
