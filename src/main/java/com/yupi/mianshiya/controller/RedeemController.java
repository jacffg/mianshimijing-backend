package com.yupi.mianshiya.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.mianshiya.annotation.AuthCheck;
import com.yupi.mianshiya.common.BaseResponse;
import com.yupi.mianshiya.common.DeleteRequest;
import com.yupi.mianshiya.common.ErrorCode;
import com.yupi.mianshiya.common.ResultUtils;
import com.yupi.mianshiya.constant.UserConstant;
import com.yupi.mianshiya.exception.BusinessException;
import com.yupi.mianshiya.exception.ThrowUtils;
import com.yupi.mianshiya.model.dto.redeem.*;
import com.yupi.mianshiya.model.entity.Redeem;
import com.yupi.mianshiya.model.entity.User;
import com.yupi.mianshiya.model.vo.RedeemVO;
import com.yupi.mianshiya.service.RedeemService;
import com.yupi.mianshiya.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;

/**
 * 兑换接口
 */
@RestController
@RequestMapping("/redeem")
@Slf4j
public class RedeemController {

    @Resource
    private RedeemService redeemService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建兑换
     *
     * @param redeemAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addRedeem(@RequestBody RedeemAddRequest redeemAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(redeemAddRequest == null, ErrorCode.PARAMS_ERROR);
        Redeem redeem = new Redeem();
        BeanUtils.copyProperties(redeemAddRequest, redeem);
        //默认过期时间为一周
        if (redeem.getExpirationTime()==null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date()); // 设置当前时间
            calendar.add(Calendar.WEEK_OF_YEAR, 1); // 增加一周
            redeem.setExpirationTime(calendar.getTime());
        }
        // 数据校验
        redeemService.validRedeem(redeem, true);
        // 写入数据库
        boolean result = redeemService.save(redeem);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newRedeemId = redeem.getId();
        return ResultUtils.success(newRedeemId);
    }

    /**
     * 删除兑换
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteRedeem(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Redeem oldRedeem = redeemService.getById(id);
        ThrowUtils.throwIf(oldRedeem == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = redeemService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新兑换（仅管理员可用）
     *
     * @param redeemUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateRedeem(@RequestBody RedeemUpdateRequest redeemUpdateRequest) {
        if (redeemUpdateRequest == null || redeemUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Redeem redeem = new Redeem();
        BeanUtils.copyProperties(redeemUpdateRequest, redeem);
        // 数据校验
        redeemService.validRedeem(redeem, false);
        // 判断是否存在
        long id = redeemUpdateRequest.getId();
        Redeem oldRedeem = redeemService.getById(id);
        ThrowUtils.throwIf(oldRedeem == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = redeemService.updateById(redeem);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取兑换（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<RedeemVO> getRedeemVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Redeem redeem = redeemService.getById(id);
        ThrowUtils.throwIf(redeem == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(redeemService.getRedeemVO(redeem, request));
    }

    /**
     * 分页获取兑换列表（仅管理员可用）
     *
     * @param redeemQueryRequest
     * @return
     */
    @PostMapping("/list/page")

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Redeem>> listRedeemByPage(@RequestBody RedeemQueryRequest redeemQueryRequest) {
        long current = redeemQueryRequest.getCurrent();
        long size = redeemQueryRequest.getPageSize();

        // 查询数据库
        Page<Redeem> redeemPage = redeemService.page(new Page<>(current, size),
                redeemService.getQueryWrapper(redeemQueryRequest));
        return ResultUtils.success(redeemPage);
    }

    /**
     * 分页获取兑换列表（封装类）
     *
     * @param redeemQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<RedeemVO>> listRedeemVOByPage(@RequestBody RedeemQueryRequest redeemQueryRequest,
                                                               HttpServletRequest request) {
        long current = redeemQueryRequest.getCurrent();
        long size = redeemQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Redeem> redeemPage = redeemService.page(new Page<>(current, size),
                redeemService.getQueryWrapper(redeemQueryRequest));
        // 获取封装类
        return ResultUtils.success(redeemService.getRedeemVOPage(redeemPage, request));
    }

    /**
     * 分页获取当前登录用户创建的兑换列表
     *
     * @param redeemQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<RedeemVO>> listMyRedeemVOByPage(@RequestBody RedeemQueryRequest redeemQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(redeemQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        redeemQueryRequest.setUserId(loginUser.getId());
        long current = redeemQueryRequest.getCurrent();
        long size = redeemQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Redeem> redeemPage = redeemService.page(new Page<>(current, size),
                redeemService.getQueryWrapper(redeemQueryRequest));
        // 获取封装类
        return ResultUtils.success(redeemService.getRedeemVOPage(redeemPage, request));
    }

    /**
     * 编辑兑换（给用户使用）
     *
     * @param redeemEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> editRedeem(@RequestBody RedeemEditRequest redeemEditRequest, HttpServletRequest request) {
        if (redeemEditRequest == null || redeemEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Redeem redeem = new Redeem();
        BeanUtils.copyProperties(redeemEditRequest, redeem);
        // 数据校验
        redeemService.validRedeem(redeem, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = redeemEditRequest.getId();
        Redeem oldRedeem = redeemService.getById(id);
        ThrowUtils.throwIf(oldRedeem == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = redeemService.updateById(redeem);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
    /**
     * 兑换
     * @param request
     * @return
     */
    @PostMapping("/exchange")
    public BaseResponse<Boolean> exchange(@RequestBody ExchangeRequest exchangeRequest, HttpServletRequest request) {
        return ResultUtils.success(redeemService.exchangeCode(exchangeRequest,request));
    }

    // endregion
}
