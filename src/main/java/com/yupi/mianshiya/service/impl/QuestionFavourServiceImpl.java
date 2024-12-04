package com.yupi.mianshiya.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.mianshiya.common.ErrorCode;
import com.yupi.mianshiya.exception.BusinessException;
import com.yupi.mianshiya.mapper.QuestionFavourMapper;
import com.yupi.mianshiya.model.entity.Question;
import com.yupi.mianshiya.model.entity.QuestionFavour;
import com.yupi.mianshiya.model.entity.User;
import com.yupi.mianshiya.service.QuestionFavourService;
import com.yupi.mianshiya.service.QuestionService;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 题目收藏服务实现
 *

 */
@Service
public class QuestionFavourServiceImpl extends ServiceImpl<QuestionFavourMapper, QuestionFavour>
        implements QuestionFavourService {

    @Resource
    private QuestionService QuestionService;

    /**
     * 题目收藏
     *
     * @param QuestionId
     * @param loginUser
     * @return
     */
    @Override
    public int doQuestionFavour(long QuestionId, User loginUser) {
        // 判断是否存在
        Question Question = QuestionService.getById(QuestionId);
        if (Question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已题目收藏
        long userId = loginUser.getId();
        // 每个用户串行题目收藏
        // 锁必须要包裹住事务方法
        QuestionFavourService QuestionFavourService = (QuestionFavourService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return QuestionFavourService.doQuestionFavourInner(userId, QuestionId);
        }
    }

    @Override
    public Page<Question> listFavourQuestionByPage(IPage<Question> page, Wrapper<Question> queryWrapper, long favourUserId) {
        if (favourUserId <= 0) {
            return new Page<>();
        }
        return baseMapper.listFavourQuestionByPage(page, queryWrapper, favourUserId);
    }

    /**
     * 封装了事务的方法
     *
     * @param userId
     * @param QuestionId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doQuestionFavourInner(long userId, long QuestionId) {
        QuestionFavour QuestionFavour = new QuestionFavour();
        QuestionFavour.setUserId(userId);
        QuestionFavour.setQuestionId(QuestionId);
        QueryWrapper<QuestionFavour> QuestionFavourQueryWrapper = new QueryWrapper<>(QuestionFavour);
        QuestionFavour oldQuestionFavour = this.getOne(QuestionFavourQueryWrapper);
        boolean result;
        // 已收藏
        if (oldQuestionFavour != null) {
            result = this.remove(QuestionFavourQueryWrapper);
            if (result) {
                // 题目收藏数 - 1
                result = QuestionService.update()
                        .eq("id", QuestionId)
                        .gt("favourNum", 0)
                        .setSql("favourNum = favourNum - 1")
                        .update();
                return result ? -1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {
            // 未题目收藏
            result = this.save(QuestionFavour);
            if (result) {
                // 题目收藏数 + 1
                result = QuestionService.update()
                        .eq("id", QuestionId)
                        .setSql("favourNum = favourNum + 1")
                        .update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
    }

}




