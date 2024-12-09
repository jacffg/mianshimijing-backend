package com.yupi.mianshiya.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.mianshiya.mapper.QuestionMapper;
import com.yupi.mianshiya.model.dto.question.QuestionQueryRequest;
import com.yupi.mianshiya.model.dto.question.QuestionRelatedRequest;
import com.yupi.mianshiya.model.entity.Question;
import com.yupi.mianshiya.model.vo.QuestionVO;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface QuestionService extends IService<Question> {


    /**
     * 校验数据
     *
     * @param question
     * @param add 对创建的数据进行校验
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);
    
    /**
     * 获取题目封装
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    /**
     * 分页获取题目列表
     *
     * @param questionQueryRequest
     * @return
     */
    Page<Question> listQuestionByPage(QuestionQueryRequest questionQueryRequest);
    /**
     * 批量删除题目
     *
     * @param questionIdList
     */

    void batchDeleteQuestions(List<Long> questionIdList);

    /**
     * Ai生成推荐答案
     *
     * @param questionId
     * @return
     */
    QuestionVO getQuestionByAi(Long questionId );
    /**
     * 获取相关题目
     * @return
     */
    List<QuestionVO> getRelatesQuesions(QuestionRelatedRequest questionRelatedRequest);

    /**
     * 获取最大题目号
     * @return
     */
    public Long getMaxQuestionNum() ;

    /**
     * 从es查询题目
     * @return
     */
    public Page<Question> searchFromEs(QuestionQueryRequest questionQueryRequest) ;

}
