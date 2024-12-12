package com.yupi.mianshiya.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 封禁用户
 *
 * @author <a href="https://github.com/jacffg">码羊</a>
 */
@Data
public class UserBanRequest implements Serializable {

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 封禁时间（天数）
     */
    private Long banTime;


    private static final long serialVersionUID = 1L;
}