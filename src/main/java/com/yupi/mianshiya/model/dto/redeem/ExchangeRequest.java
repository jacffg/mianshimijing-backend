package com.yupi.mianshiya.model.dto.redeem;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 *
 * @author <a href="https://github.com/jacffg">码羊</a>
 */
@Data
public class ExchangeRequest implements Serializable {

    /**
     * 兑换码
     */
    private String code;

    private static final long serialVersionUID = 1L;
}