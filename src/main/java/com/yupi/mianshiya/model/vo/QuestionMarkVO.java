package com.yupi.mianshiya.model.vo;

import cn.hutool.json.JSONUtil;
import com.yupi.mianshiya.model.entity.QuestionMark;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目标记视图
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class QuestionMarkVO implements Serializable {

    /**
     * 标记 ID
     */
    private Long id;

    /**
     * 题目 ID
     */
    private Long questionId;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 标记类型
     */
    private String markType;

    /**
     * 封装类转对象
     *
     * @param questionMarkVO
     * @return
     */
    public static QuestionMark voToObj(QuestionMarkVO questionMarkVO) {
        if (questionMarkVO == null) {
            return null;
        }
        QuestionMark questionMark = new QuestionMark();
        BeanUtils.copyProperties(questionMarkVO, questionMark);
        return questionMark;
    }

    /**
     * 对象转封装类
     *
     * @param questionMark
     * @return
     */
    public static QuestionMarkVO objToVo(QuestionMark questionMark) {
        if (questionMark == null) {
            return null;
        }
        QuestionMarkVO questionMarkVO = new QuestionMarkVO();
        BeanUtils.copyProperties(questionMark, questionMarkVO);
        return questionMarkVO;
    }
}
