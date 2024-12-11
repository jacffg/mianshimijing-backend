package com.yupi.mianshiya.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户视图（脱敏）
 *
 * @author <a href="https://github.com/jacffg">码羊</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotTagsVO implements Serializable {


    /**
     * 热度
     */
    private Long hotNum;
    /**
    标签
     */
    private  String tag ;


}