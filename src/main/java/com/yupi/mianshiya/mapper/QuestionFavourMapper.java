package com.yupi.mianshiya.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.mianshiya.model.entity.Question;
import com.yupi.mianshiya.model.entity.QuestionFavour;
import org.apache.ibatis.annotations.Param;

/**
 *  * 题目收藏数据库操作
 * @author 万佳羊
 * @description 针对表【question_favour(题目收藏)】的数据库操作Mapper
 * @createDate 2024-12-04 10:18:28
 * @Entity generator.domain.QuestionFavour
 */
public interface QuestionFavourMapper extends BaseMapper<QuestionFavour> {

    /**
     * 分页查询收藏题目列表
     *
     * @param page
     * @param queryWrapper
     * @param favourUserId
     * @return
     */
    Page<Question> listFavourQuestionByPage(IPage<Question> page, @Param(Constants.WRAPPER) Wrapper<Question> queryWrapper,
                                            long favourUserId);
}




