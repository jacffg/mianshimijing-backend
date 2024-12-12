package com.yupi.mianshiya.model.dto.mail;


import lombok.Data;

import java.io.Serializable;

/**
 * @author 万佳羊
 * {@code @date}  2024-12-12  18:34
 * @version 1.0
 */
@Data
public class Message implements Serializable {
    private String toEmail;
    private String subject;
    private String content;
    private static final long serialVersionUID = 1L;
}
