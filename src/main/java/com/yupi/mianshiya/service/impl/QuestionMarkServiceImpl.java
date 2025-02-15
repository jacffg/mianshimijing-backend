package com.yupi.mianshiya.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.mianshiya.common.ErrorCode;
import com.yupi.mianshiya.constant.CommonConstant;
import com.yupi.mianshiya.exception.BusinessException;
import com.yupi.mianshiya.exception.ThrowUtils;
import com.yupi.mianshiya.mapper.QuestionMarkMapper;
import com.yupi.mianshiya.model.dto.questionMark.QuestionMarkQueryRequest;
import com.yupi.mianshiya.model.entity.Question;
import com.yupi.mianshiya.model.entity.QuestionMark;
import com.yupi.mianshiya.model.entity.User;
import com.yupi.mianshiya.model.enums.MarkTypeEnum;
import com.yupi.mianshiya.model.vo.QuestionMarkVO;
import com.yupi.mianshiya.model.vo.UserVO;
import com.yupi.mianshiya.service.QuestionMarkService;
import com.yupi.mianshiya.service.QuestionService;
import com.yupi.mianshiya.service.UserService;
import com.yupi.mianshiya.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 题目标记服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class QuestionMarkServiceImpl extends ServiceImpl<QuestionMarkMapper, QuestionMark> implements QuestionMarkService {

    @Resource
    private UserService userService;
    @Resource
    private QuestionService questionService;

    /**
     * 校验数据
     *
     * @param questionMark
     * @param add          对创建的数据进行校验
     */
    @Override
    public void validQuestionMark(QuestionMark questionMark, boolean add) {
        ThrowUtils.throwIf(questionMark == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String markType = questionMark.getMarkType();
        Long questionId = questionMark.getQuestionId();
        // 创建数据时，参数不能为空
        if (add) {
            //校验
            Question question = questionService.getById(questionId);
            ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);

            // 防止重复添加
            QueryWrapper<QuestionMark> wrapper = new QueryWrapper<>();
            wrapper.eq("questionId", questionMark.getQuestionId())
                    .eq("userId", questionMark.getUserId());

            // 查询数据库
            QuestionMark existingMark = this.getOne(wrapper);

            if (existingMark != null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能重复添加");
            }


        }
        ThrowUtils.throwIf(StringUtils.isBlank(markType), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(MarkTypeEnum.getEnumByValue(markType) == null, ErrorCode.PARAMS_ERROR);
    }

    /**
     * 获取查询条件
     *
     * @param questionMarkQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionMark> getQueryWrapper(QuestionMarkQueryRequest questionMarkQueryRequest) {
        QueryWrapper<QuestionMark> queryWrapper = new QueryWrapper<>();
        if (questionMarkQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = questionMarkQueryRequest.getId();
        Long questionId = questionMarkQueryRequest.getQuestionId();
        String markType = questionMarkQueryRequest.getMarkType();
        String sortField = questionMarkQueryRequest.getSortField();
        String sortOrder = questionMarkQueryRequest.getSortOrder();
        Long userId = questionMarkQueryRequest.getUserId();
        // todo 补充需要的查询条件


        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(markType), "markType", markType);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题目标记封装
     *
     * @param questionMark
     * @param request
     * @return
     */
    @Override
    public QuestionMarkVO getQuestionMarkVO(QuestionMark questionMark, HttpServletRequest request) {
        // 对象转封装类
        QuestionMarkVO questionMarkVO = QuestionMarkVO.objToVo(questionMark);

        //  可以根据需要为封装对象补充值，不需要的内容可以删除


        // endregion

        return questionMarkVO;
    }

    /**
     * 分页获取题目标记封装
     *
     * @param questionMarkPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionMarkVO> getQuestionMarkVOPage(Page<QuestionMark> questionMarkPage, HttpServletRequest request) {
        List<QuestionMark> questionMarkList = questionMarkPage.getRecords();
        Page<QuestionMarkVO> questionMarkVOPage = new Page<>(questionMarkPage.getCurrent(), questionMarkPage.getSize(), questionMarkPage.getTotal());
        if (CollUtil.isEmpty(questionMarkList)) {
            return questionMarkVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionMarkVO> questionMarkVOList = questionMarkList.stream().map(questionMark -> {
            return QuestionMarkVO.objToVo(questionMark);
        }).collect(Collectors.toList());

        // 可以根据需要为封装对象补充值，不需要的内容可以删除


        // endregion

        questionMarkVOPage.setRecords(questionMarkVOList);
        return questionMarkVOPage;
    }

}
