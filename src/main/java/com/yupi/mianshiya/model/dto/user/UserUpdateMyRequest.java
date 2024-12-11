package com.yupi.mianshiya.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户更新个人信息请求
 *
 * @author <a href="https://github.com/jacffg">码羊</a>
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;
    /**
     * 是否展示答案
     */
    private Integer likeShowAnswer;

    private static final long serialVersionUID = 1L;
}