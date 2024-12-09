package com.yupi.mianshiya.model.dto.question;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author 万佳羊
 * {@code @date}  2024-12-09  17:05
 * @version 1.0
 */
@Data
public class QuestionImportDTO {
    @ExcelProperty("标题")
    private String title;

    @ExcelProperty("内容")
    private String content;

    @ExcelProperty("标签列表(json数组)")
    private String tags;

    @ExcelProperty("推荐答案")
    private String answer;

    @ExcelProperty("难度")
    private String diffity;

    @ExcelProperty("是否为会员专属(0为是,1为不是)")
    private Integer isVip;
}
