package com.yupi.mianshiya.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.mianshiya.model.dto.comment.CommentQueryRequest;
import com.yupi.mianshiya.model.entity.Comment;
import com.yupi.mianshiya.model.vo.CommentVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目评论服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface CommentService extends IService<Comment> {

    /**
     * 校验数据
     *
     * @param comment
     * @param add 对创建的数据进行校验
     */
    void validComment(Comment comment, boolean add);

    /**
     * 获取查询条件
     *
     * @param commentQueryRequest
     * @return
     */
    QueryWrapper<Comment> getQueryWrapper(CommentQueryRequest commentQueryRequest);
    
    /**
     * 获取题目评论封装
     *
     * @param comment
     * @param request
     * @return
     */
    CommentVO getCommentVO(Comment comment, HttpServletRequest request);

    /**
     * 分页获取题目评论封装
     *
     * @param commentPage
     * @param request
     * @return
     */
    Page<CommentVO> getCommentVOPage(Page<Comment> commentPage, HttpServletRequest request);

    /**
     * 根据题目Id获取评论
     *
     * @param questionId
     * @param request
     * @return
     */

     List<CommentVO> getCommentsByQuestionId(long questionId, HttpServletRequest request) ;
}
