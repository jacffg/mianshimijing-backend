package com.yupi.mianshiya.mapper;

import com.yupi.mianshiya.model.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
* @author 李鱼皮
* @description 针对表【question(题目)】的数据库操作Mapper
* @createDate 2024-08-24 21:46:47
* @Entity com.yupi.mianshiya.model.entity.Question
*/
public interface QuestionMapper extends BaseMapper<Question> {

    @Select("SELECT MAX(questionNum) FROM question")
    Long selectMaxQuestionNum();
}




