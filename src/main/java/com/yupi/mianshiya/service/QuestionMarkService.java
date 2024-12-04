package com.yupi.mianshiya.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.mianshiya.model.dto.questionMark.QuestionMarkQueryRequest;
import com.yupi.mianshiya.model.vo.QuestionMarkVO;
import com.yupi.mianshiya.model.entity.QuestionMark;

import javax.servlet.http.HttpServletRequest;

/**
 * 题目标记服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface QuestionMarkService extends IService<QuestionMark> {

    /**
     * 校验数据
     *
     * @param questionMark
     * @param add 对创建的数据进行校验
     */
    void validQuestionMark(QuestionMark questionMark, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionMarkQueryRequest
     * @return
     */
    QueryWrapper<QuestionMark> getQueryWrapper(QuestionMarkQueryRequest questionMarkQueryRequest);
    
    /**
     * 获取题目标记封装
     *
     * @param questionMark
     * @param request
     * @return
     */
    QuestionMarkVO getQuestionMarkVO(QuestionMark questionMark, HttpServletRequest request);

    /**
     * 分页获取题目标记封装
     *
     * @param questionMarkPage
     * @param request
     * @return
     */
    Page<QuestionMarkVO> getQuestionMarkVOPage(Page<QuestionMark> questionMarkPage, HttpServletRequest request);
}
