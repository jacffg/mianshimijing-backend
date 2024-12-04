package com.yupi.mianshiya.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.mianshiya.model.entity.Question;
import com.yupi.mianshiya.model.entity.QuestionFavour;
import com.yupi.mianshiya.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 万佳羊
 * @description 针对表【question_favour(题目收藏)】的数据库操作Service
 * @createDate 2024-12-04 10:18:28
 */
public interface QuestionFavourService extends IService<QuestionFavour> {

    /**
     * 题目收藏
     *
     * @param questionId
     * @param loginUser
     * @return
     */
    int doQuestionFavour(long questionId, User loginUser);

    /**
     * 分页获取用户收藏的题目列表
     *
     * @param page
     * @param queryWrapper
     * @param favourUserId
     * @return
     */
    Page<Question> listFavourQuestionByPage(IPage<Question> page, Wrapper<Question> queryWrapper, long favourUserId);

    /**
     * 题目收藏（内部服务）
     *
     * @param userId
     * @param questionId
     * @return
     */
    int doQuestionFavourInner(long userId, long questionId);
}
