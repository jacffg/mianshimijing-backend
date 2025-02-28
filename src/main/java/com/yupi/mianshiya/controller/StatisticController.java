package com.yupi.mianshiya.controller;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.mianshiya.annotation.AuthCheck;
import com.yupi.mianshiya.common.BaseResponse;
import com.yupi.mianshiya.common.ResultUtils;
import com.yupi.mianshiya.constant.UserConstant;
import com.yupi.mianshiya.model.dto.statistic.QuestionFavourCountDTO;
import com.yupi.mianshiya.model.dto.statistic.QuestionTagsCounttDTO;
import com.yupi.mianshiya.model.dto.statistic.QuestionViewCountDTO;
import com.yupi.mianshiya.model.dto.statistic.UserSignCountDTO;
import com.yupi.mianshiya.model.entity.Question;
import com.yupi.mianshiya.model.entity.User;
import com.yupi.mianshiya.model.vo.CommentVO;
import com.yupi.mianshiya.service.QuestionService;
import com.yupi.mianshiya.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.json.JSONUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据分析
 */
@RestController
@RequestMapping("/statistic")
@Slf4j
public class StatisticController {
    @Resource
    QuestionService questionService;

    @Resource
    UserService userService;


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
                    questionViewCountDTO.setQuestion(question.getQuestionNum() + "、" + question.getTitle());
                    return questionViewCountDTO;
                }).collect(Collectors.toList());
        return ResultUtils.success(res);
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
                    questionFavourCountDTO.setQuestion(question.getQuestionNum() + "、" + question.getTitle());
                    questionFavourCountDTO.setFavourNum(question.getFavourNum().toString());
                    return questionFavourCountDTO;
                }).collect(Collectors.toList());
        return ResultUtils.success(res);
    }

    /**
     * 统计用户签到排行
     */
    @GetMapping("/user/sigin")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<List<UserSignCountDTO>> getUserSignInStatict(@RequestParam(required = false) Integer num, @RequestParam(required = false) Integer year, HttpServletRequest request) {
        // 如果 num 为空，则默认设置为 10
        if (num == null || num <= 0) {
            num = 10;
        }
        // 如果 num 为空，则默认设置为 10
        if (year == null || year <= 0)  {
            LocalDate date = LocalDate.now();
            year = date.getYear();
        }
         List<User> list = userService.list();
        Integer finalYear = year;
        final List<UserSignCountDTO> res =new ArrayList<>();
        // 遍历用户列表，统计签到次数
        list.forEach(user ->{
            List<Integer> userSignInRecord = userService.getUserSignInRecord(user.getId(), finalYear);
            UserSignCountDTO userSignCountDTO = new UserSignCountDTO();
            userSignCountDTO.setUserId(user.getId());
            userSignCountDTO.setUserName(user.getUserName());
            userSignCountDTO.setSignNum(userSignInRecord.size());
            res.add(userSignCountDTO);
        });
        //统计前十的用户
        List<UserSignCountDTO> result = res.stream()
                .sorted((o1, o2) -> o2.getSignNum().compareTo(o1.getSignNum())) // 排序
                .limit(num) // 取前 num 个
                .collect(Collectors.toList());// 收集结果
        return ResultUtils.success(result);
    }
    /**
     * 获取空间图片标签分析
     * @return
     */
    @GetMapping("/tag")
    public BaseResponse<List<QuestionTagsCounttDTO>> getQuestionTagsCountt( ){

        // 构造查询条件
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        // 查询所有符合条件的标签
        queryWrapper.select("tags");
        List<String> tagsJsonList = questionService.getBaseMapper().selectObjs(queryWrapper)
                .stream()
                .filter(ObjUtil::isNotNull)
                .map(Object::toString)
                .collect(Collectors.toList());

        // 解析标签并统计
        Map<String, Long> tagCountMap = tagsJsonList.stream()
                // ["Java", "Python"], ["Java", "PHP"] => "Java", "Python", "Java", "PHP"
                .flatMap(tagsJson -> JSONUtil.toList(tagsJson, String.class).stream())
                .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()));

        // 转换为响应对象，按照使用次数进行排序
        List<QuestionTagsCounttDTO> res = tagCountMap.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // 降序排序
                .map(entry -> new QuestionTagsCounttDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return ResultUtils.success(res);
    }
}
