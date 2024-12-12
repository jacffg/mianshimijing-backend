package com.yupi.mianshiya.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.mianshiya.annotation.AuthCheck;
import com.yupi.mianshiya.common.BaseResponse;
import com.yupi.mianshiya.common.ResultUtils;
import com.yupi.mianshiya.constant.UserConstant;
import com.yupi.mianshiya.model.dto.statistic.QuestionFavourCountDTO;
import com.yupi.mianshiya.model.dto.statistic.QuestionViewCountDTO;
import com.yupi.mianshiya.model.entity.Question;
import com.yupi.mianshiya.model.vo.CommentVO;
import com.yupi.mianshiya.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.dev33.satoken.annotation.SaCheckRole;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 *数据分析
 */
@RestController
@RequestMapping("/statistic")
@Slf4j
public class StatisticController {
    @Resource
    QuestionService questionService;


    /**
     * 统计浏览量题目
     */

    @GetMapping("/question/viewNum_count")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<List<QuestionViewCountDTO>> getQuestionViewNumStatict(@RequestParam(required = false) Integer num, HttpServletRequest request) {
        // 如果 num 为空，则默认设置为 10
        if (num == null || num <= 0) {
            num = 10;
        }
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("viewNum");  // 按浏览数倒序排列
        wrapper.last("LIMIT " + num);    // 限制返回的记录数
        List<Question> questions = questionService.list(wrapper);

        List<QuestionViewCountDTO> res =
                questions.stream().map(question -> {
                    QuestionViewCountDTO questionViewCountDTO = new QuestionViewCountDTO();
                    questionViewCountDTO.setViewNum(question.getViewNum().toString());
                    questionViewCountDTO.setQuestion(question.getQuestionNum()+"、"+question.getTitle());
                    return questionViewCountDTO;
                }).collect(Collectors.toList());
        return  ResultUtils.success(res);
    }

    /**
     * 统计收藏数题目
     */
    @GetMapping("/question/favour_count")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<List<QuestionFavourCountDTO>> getQuestionFavourStatict(@RequestParam(required = false) Integer num, HttpServletRequest request) {
        // 如果 num 为空，则默认设置为 10
        if (num == null || num <= 0) {
            num = 10;
        }
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("favourNum");  // 按浏览数倒序排列
        wrapper.last("LIMIT " + num);    // 限制返回的记录数
        List<Question> questions = questionService.list(wrapper);
        List<QuestionFavourCountDTO> res =
                questions.stream().map(question -> {
                    QuestionFavourCountDTO questionFavourCountDTO = new QuestionFavourCountDTO();
                    questionFavourCountDTO.setQuestion(question.getQuestionNum()+"、"+question.getTitle());
                    questionFavourCountDTO.setFavourNum(question.getFavourNum().toString());
                    return questionFavourCountDTO;
                }).collect(Collectors.toList());
        return  ResultUtils.success(res);
    }

}
