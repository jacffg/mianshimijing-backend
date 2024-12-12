package com.yupi.mianshiya.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.mianshiya.annotation.AuthCheck;
import com.yupi.mianshiya.common.BaseResponse;
import com.yupi.mianshiya.common.ErrorCode;
import com.yupi.mianshiya.common.ResultUtils;
import com.yupi.mianshiya.constant.UserConstant;
import com.yupi.mianshiya.exception.BusinessException;
import com.yupi.mianshiya.exception.ThrowUtils;
import com.yupi.mianshiya.model.dto.question.QuestionQueryRequest;
import com.yupi.mianshiya.model.dto.questionfavour.QuestionFavourAddRequest;
import com.yupi.mianshiya.model.dto.questionfavour.QuestionFavourQueryRequest;
import com.yupi.mianshiya.model.entity.Question;
import com.yupi.mianshiya.model.entity.QuestionFavour;
import com.yupi.mianshiya.model.entity.User;
import com.yupi.mianshiya.model.vo.QuestionVO;
import com.yupi.mianshiya.service.QuestionFavourService;
import com.yupi.mianshiya.service.QuestionService;
import com.yupi.mianshiya.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.annotation.SaCheckRole;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 题目收藏接口
 *
 * @author <a href="https://github.com/jacffg">码羊</a>
 */
@RestController
@RequestMapping("/question_favour")
@Slf4j
public class QuestionFavourController {

    @Resource
    private QuestionFavourService questionFavourService;

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    /**
     * 收藏 / 取消收藏
     *
     * @param questionFavourAddRequest
     * @param request
     * @return resultNum 收藏变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doQuestionFavour(@RequestBody QuestionFavourAddRequest questionFavourAddRequest,
                                                  HttpServletRequest request) {
        if (questionFavourAddRequest == null || questionFavourAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能操作
        final User loginUser = userService.getLoginUser(request);
        long questionId = questionFavourAddRequest.getQuestionId();
        int result = questionFavourService.doQuestionFavour(questionId, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 获取我收藏的题目列表
     *
     * @param questionQueryRequest
     * @param request
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<QuestionVO>> listMyFavourQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                     HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 得到用户收藏的所有题目 id
        LambdaQueryWrapper<QuestionFavour> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionFavour.class)
                .select(QuestionFavour::getQuestionId)
                .eq(QuestionFavour::getUserId, loginUser.getId());
        List<QuestionFavour> questionFavourList = questionFavourService.list(lambdaQueryWrapper);
        if (questionFavourList.isEmpty()){
            return ResultUtils.success(null);
        }
        List<Long> questionIdList = questionFavourList.stream().map(QuestionFavour::getQuestionId).collect(Collectors.toList());
        //构造条件
        QueryWrapper<Question> queryWrapper = questionService.getQueryWrapper(questionQueryRequest);
        queryWrapper.in("id", questionIdList);
        // 查询数据库
        Page<Question> questionPage = questionService.page(new Page<>(current, size), queryWrapper);
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollUtil.isEmpty(questionList)) {
            return ResultUtils.success(questionVOPage);
        }
        // 对象列表 => 封装对象列表
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            return QuestionVO.objToVo(question);
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return ResultUtils.success(questionVOPage);
    }


    /**
     * 获取用户收藏的题目列表
     *
     * @param questionFavourQueryRequest
     * @param request
     */
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @PostMapping("/list/page")
    public BaseResponse<Page<Question>> listFavourQuestionByPage(@RequestBody QuestionFavourQueryRequest questionFavourQueryRequest,
                                                                   HttpServletRequest request) {
        if (questionFavourQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = questionFavourQueryRequest.getCurrent();
        long size = questionFavourQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 得到指定用户收藏的所有题目 id,没有就全部
        LambdaQueryWrapper<QuestionFavour> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionFavour.class)
                .select(QuestionFavour::getQuestionId);
        if (questionFavourQueryRequest.getUserId() != null) {
            lambdaQueryWrapper.eq(QuestionFavour::getUserId, questionFavourQueryRequest.getUserId());
        }
        List<QuestionFavour> questionFavourList = questionFavourService.list(lambdaQueryWrapper);
        Set<Long> questionIdList = questionFavourList.stream().map(QuestionFavour::getQuestionId).collect(Collectors.toSet());
        //构造条件
        QueryWrapper<Question> queryWrapper = questionService.getQueryWrapper(questionFavourQueryRequest.getQuestionQueryRequest());
        queryWrapper.in("id", questionIdList);
        // 查询数据库
        Page<Question> questionPage = questionService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(questionPage);
    }

    /**
     * 用户是否收藏
     *
     * @param
     * @param request
     */
    @GetMapping("/getisCollect")
    public BaseResponse<Boolean> getisCollect(Long questionId, HttpServletRequest request) {
        if (questionId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QuestionFavour questionFavour = new QuestionFavour();
        questionFavour.setQuestionId(questionId);
        questionFavour.setUserId(loginUser.getId());
        QueryWrapper<QuestionFavour> wrapper = new QueryWrapper<>(questionFavour);
        QuestionFavour questionFavourByData = questionFavourService.getOne(wrapper);
        boolean res = false;
        if (questionFavourByData != null) {
            //已收藏
            res = true;
        }
        return ResultUtils.success(res);
    }
}
