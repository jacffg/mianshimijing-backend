package com.yupi.mianshiya.model.dto.statistic;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 万佳羊
 * {@code @date}  2024-11-09  16:00
 * @version 1.0
 */
@Data
public class UserSignCountDTO implements Serializable {

    // 用户id
    private Long userId;
    // 用户名字
    private String userName;
    // 用户签到数量
    private Integer signNum;
    private static final long serialVersionUID = 1L;
}

