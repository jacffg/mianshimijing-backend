package com.yupi.mianshiya.model.dto.question;

import com.yupi.mianshiya.model.entity.User;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 批量导入题目任务
 *
 */
@Data
@ToString
public class QuestionImportTask implements Serializable {

    /**
     * id
     */
    private String taskId;

    /**
     * 导入人
     */
    private User user;


    private static final long serialVersionUID = 1L;
}