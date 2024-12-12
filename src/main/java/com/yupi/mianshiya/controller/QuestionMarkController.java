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
import com.yupi.mianshiya.model.dto.questionMark.QuestionMarkAddRequest;
import com.yupi.mianshiya.model.dto.questionMark.QuestionMarkEditRequest;
import com.yupi.mianshiya.model.dto.questionMark.QuestionMarkQueryRequest;
import com.yupi.mianshiya.model.dto.questionMark.QuestionMarkUpdateRequest;
import com.yupi.mianshiya.model.entity.QuestionFavour;
import com.yupi.mianshiya.model.entity.QuestionMark;
import com.yupi.mianshiya.model.entity.User;
import com.yupi.mianshiya.model.vo.QuestionMarkVO;
import com.yupi.mianshiya.service.QuestionMarkService;
import com.yupi.mianshiya.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.annotation.SaCheckRole;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目标记接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@RestController
@RequestMapping("/questionMark")
@Slf4j
public class QuestionMarkController {

    @Resource
    private QuestionMarkService questionMarkService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建题目标记
     *
     * @param questionMarkAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestionMark(@RequestBody QuestionMarkAddRequest questionMarkAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionMarkAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        QuestionMark questionMark = new QuestionMark();
        BeanUtils.copyProperties(questionMarkAddRequest, questionMark);
        User loginUser = userService.getLoginUser(request);
        questionMark.setUserId(loginUser.getId());
        // 数据校验
        questionMarkService.validQuestionMark(questionMark, true);
        // 写入数据库
        boolean result = questionMarkService.save(questionMark);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionMarkId = questionMark.getId();
        return ResultUtils.success(newQuestionMarkId);
    }

    /**
     * 删除题目标记
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestionMark(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        QuestionMark oldQuestionMark = questionMarkService.getById(id);
        ThrowUtils.throwIf(oldQuestionMark == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestionMark.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionMarkService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新题目标记（仅管理员可用）
     *
     * @param questionMarkUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestionMark(@RequestBody QuestionMarkUpdateRequest questionMarkUpdateRequest) {
        if (questionMarkUpdateRequest == null || questionMarkUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        QuestionMark questionMark = new QuestionMark();
        BeanUtils.copyProperties(questionMarkUpdateRequest, questionMark);
        // 数据校验
        questionMarkService.validQuestionMark(questionMark, false);
        // 判断是否存在
        long id = questionMarkUpdateRequest.getId();
        QuestionMark oldQuestionMark = questionMarkService.getById(id);
        ThrowUtils.throwIf(oldQuestionMark == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionMarkService.updateById(questionMark);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题目标记（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionMarkVO> getQuestionMarkVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        QuestionMark questionMark = questionMarkService.getById(id);
        ThrowUtils.throwIf(questionMark == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(questionMarkService.getQuestionMarkVO(questionMark, request));
    }

    /**
     * 分页获取题目标记列表（仅管理员可用）
     *
     * @param questionMarkQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionMark>> listQuestionMarkByPage(@RequestBody QuestionMarkQueryRequest questionMarkQueryRequest) {
        long current = questionMarkQueryRequest.getCurrent();
        long size = questionMarkQueryRequest.getPageSize();
        // 查询数据库
        Page<QuestionMark> questionMarkPage = questionMarkService.page(new Page<>(current, size),
                questionMarkService.getQueryWrapper(questionMarkQueryRequest));
        return ResultUtils.success(questionMarkPage);
    }

    /**
     * 分页获取题目标记列表（封装类）
     *
     * @param questionMarkQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionMarkVO>> listQuestionMarkVOByPage(@RequestBody QuestionMarkQueryRequest questionMarkQueryRequest,
                                                               HttpServletRequest request) {
        long current = questionMarkQueryRequest.getCurrent();
        long size = questionMarkQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<QuestionMark> questionMarkPage = questionMarkService.page(new Page<>(current, size),
                questionMarkService.getQueryWrapper(questionMarkQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionMarkService.getQuestionMarkVOPage(questionMarkPage, request));
    }

    /**
     * 分页获取当前登录用户创建的题目标记列表
     *
     * @param questionMarkQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionMarkVO>> listMyQuestionMarkVOByPage(@RequestBody QuestionMarkQueryRequest questionMarkQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(questionMarkQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        questionMarkQueryRequest.setUserId(loginUser.getId());
        long current = questionMarkQueryRequest.getCurrent();
        long size = questionMarkQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<QuestionMark> questionMarkPage = questionMarkService.page(new Page<>(current, size),
                questionMarkService.getQueryWrapper(questionMarkQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionMarkService.getQuestionMarkVOPage(questionMarkPage, request));
    }

    /**
     * 编辑题目标记（给用户使用）
     *
     * @param questionMarkEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestionMark(@RequestBody QuestionMarkEditRequest questionMarkEditRequest, HttpServletRequest request) {
        if (questionMarkEditRequest == null || questionMarkEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        QuestionMark questionMark = new QuestionMark();
        BeanUtils.copyProperties(questionMarkEditRequest, questionMark);
        // 数据校验
        questionMarkService.validQuestionMark(questionMark, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = questionMarkEditRequest.getId();
        QuestionMark oldQuestionMark = questionMarkService.getById(id);
        ThrowUtils.throwIf(oldQuestionMark == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestionMark.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionMarkService.updateById(questionMark);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    // endregion
    /**
     * 用户是否标记
     *
     * @param
     * @param request
     */
    @GetMapping("/getMark")
    public BaseResponse<QuestionMarkVO> getMark( Long questionId,HttpServletRequest request) {
        if ( questionId== null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QuestionMark questionMark = new QuestionMark();
        questionMark.setQuestionId(questionId);
        questionMark.setUserId(loginUser.getId());
        QueryWrapper<QuestionMark> wrapper = new QueryWrapper<>(questionMark);
        QuestionMark res = questionMarkService.getOne(wrapper);
        if (res!=null){
            return ResultUtils.success(QuestionMarkVO.objToVo(res));
        }
        return ResultUtils.success(null);


    }
}
