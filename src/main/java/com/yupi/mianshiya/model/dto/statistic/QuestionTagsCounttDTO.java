package com.yupi.mianshiya.model.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 万佳羊
 * {@code @date}  2024-11-09  16:00
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionTagsCounttDTO implements Serializable {

    /**
     * 标签名称
     */
    private String tag;

    /**
     * 使用次数
     */
    private Long count;

    private static final long serialVersionUID = 1L;
}

