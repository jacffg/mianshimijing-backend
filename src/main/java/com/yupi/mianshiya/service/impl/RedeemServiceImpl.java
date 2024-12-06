package com.yupi.mianshiya.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.mianshiya.common.ErrorCode;
import com.yupi.mianshiya.common.ResultUtils;
import com.yupi.mianshiya.constant.CommonConstant;
import com.yupi.mianshiya.exception.BusinessException;
import com.yupi.mianshiya.exception.ThrowUtils;
import com.yupi.mianshiya.mapper.RedeemMapper;
import com.yupi.mianshiya.model.dto.redeem.ExchangeRequest;
import com.yupi.mianshiya.model.dto.redeem.RedeemQueryRequest;
import com.yupi.mianshiya.model.entity.Redeem;

import com.yupi.mianshiya.model.entity.User;
import com.yupi.mianshiya.model.enums.UserRoleEnum;
import com.yupi.mianshiya.model.vo.RedeemVO;
import com.yupi.mianshiya.model.vo.UserVO;
import com.yupi.mianshiya.service.RedeemService;
import com.yupi.mianshiya.service.UserService;
import com.yupi.mianshiya.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 兑换服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class RedeemServiceImpl extends ServiceImpl<RedeemMapper, Redeem> implements RedeemService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param redeem
     * @param add    对创建的数据进行校验
     */
    @Override
    public void validRedeem(Redeem redeem, boolean add) {
        ThrowUtils.throwIf(redeem == null, ErrorCode.PARAMS_ERROR);
        String code = redeem.getCode();
        Date useTime = redeem.getUseTime();
        // 创建数据时，参数不能为空
//        if (add) {
//
//        }
        // 修改数据时，有参数则校验
        ThrowUtils.throwIf(StringUtils.isBlank(code), ErrorCode.PARAMS_ERROR);
        if (useTime != null) {
            ThrowUtils.throwIf(new Date().compareTo(useTime) < 0, ErrorCode.PARAMS_ERROR);
        }
    }

    /**
     * 获取查询条件
     *
     * @param redeemQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Redeem> getQueryWrapper(RedeemQueryRequest redeemQueryRequest) {
        QueryWrapper<Redeem> queryWrapper = new QueryWrapper<>();
        if (redeemQueryRequest == null) {
            return queryWrapper;
        }
        String code = redeemQueryRequest.getCode();
        Date useTime = redeemQueryRequest.getUseTime();
        String sortField = redeemQueryRequest.getSortField();
        String sortOrder = redeemQueryRequest.getSortOrder();
        Long userId = redeemQueryRequest.getUserId();


        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(code), "code", code);
        queryWrapper.eq(ObjectUtils.isNotEmpty(useTime), "useTime", useTime);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取兑换封装
     *
     * @param redeem
     * @param request
     * @return
     */
    @Override
    public RedeemVO getRedeemVO(Redeem redeem, HttpServletRequest request) {
        // 对象转封装类
        RedeemVO redeemVO = RedeemVO.objToVo(redeem);

        // region 可选
        // 1. 关联查询用户信息
        Long userId = redeem.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        redeemVO.setUser(userVO);

        // endregion

        return redeemVO;
    }

    /**
     * 分页获取兑换封装
     *
     * @param redeemPage
     * @param request
     * @return
     */
    @Override
    public Page<RedeemVO> getRedeemVOPage(Page<Redeem> redeemPage, HttpServletRequest request) {
        List<Redeem> redeemList = redeemPage.getRecords();
        Page<RedeemVO> redeemVOPage = new Page<>(redeemPage.getCurrent(), redeemPage.getSize(), redeemPage.getTotal());
        if (CollUtil.isEmpty(redeemList)) {
            return redeemVOPage;
        }
        // 对象列表 => 封装对象列表
        List<RedeemVO> redeemVOList = redeemList.stream().map(redeem -> {
            return RedeemVO.objToVo(redeem);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = redeemList.stream().map(Redeem::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        redeemVOList.forEach(redeemVO -> {
            Long userId = redeemVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
        });
        // endregion

        redeemVOPage.setRecords(redeemVOList);
        return redeemVOPage;
    }

    @Override
    @Transactional
    public Boolean exchangeCode(ExchangeRequest exchangeRequest, HttpServletRequest request) {
        if (exchangeRequest == null||exchangeRequest.getCode().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        if (UserRoleEnum.VIP.getValue().equals(user.getUserRole())){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"已经是尊贵的会员,不能重复兑换");
        }
        if (UserRoleEnum.ADMIN.getValue().equals(user.getUserRole())){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"管理员兑换啥啊");
        }
        if (UserRoleEnum.BAN.getValue().equals(user.getUserRole())){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"你已被封号，请联系管理员");
        }
        // 判断是否存在
        QueryWrapper<Redeem> redeemQueryWrapper = new QueryWrapper<>();
        redeemQueryWrapper.eq("code",exchangeRequest.getCode());
        Redeem redeem = this.getOne(redeemQueryWrapper);
        if (redeem==null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"兑换码无效");
        }
        // 操作数据库
        redeem.setUserId(user.getId());
        redeem.setUseTime(new Date());
        this.updateById(redeem);
        this.removeById(redeem.getId());
        //修改用户
        user.setUserRole(UserRoleEnum.VIP.getValue());
        userService.updateById(user);
        return true;
    }

}
