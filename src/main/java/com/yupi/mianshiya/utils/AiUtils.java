package com.yupi.mianshiya.utils;

import com.yupi.mianshiya.common.ErrorCode;
import com.yupi.mianshiya.exception.BusinessException;


/**
 * Ai 提取数据工具
 */
public class AiUtils {

    public static String extractAnswersAsString(String content) {
        try {
            String startTag = "(((((((((";
            String endMarker = "))))))))))))))";  // 最后
            String endMarker2 = "))))))))))))";  // 最后
            String endMarker3 = ")))))))))))";  // 最后
            String endMarker4 = "))))))))))";  // 最后
            String endMarker5 = ")))))))))";  // 最后

            int startIndex = content.indexOf(startTag);
            int endIndex = content.indexOf(endMarker);
            int endIndex2 = content.indexOf(endMarker2);
            int endIndex3 = content.indexOf(endMarker3);
            int endIndex4 = content.indexOf(endMarker4);
            int endIndex5 = content.indexOf(endMarker5);

            if (startIndex != -1 && endIndex != -1 && startIndex <= endIndex) {
                return content.substring(startIndex + startTag.length(), endIndex ).trim();
            }
            if (startIndex != -1 && endIndex2 != -1 && startIndex <= endIndex2) {
                return content.substring(startIndex + startTag.length(), endIndex2 ).trim();
            }
            if (startIndex != -1 && endIndex3 != -1 && startIndex <= endIndex3) {
                return content.substring(startIndex + startTag.length(), endIndex3 ).trim();
            }
            if (startIndex != -1 && endIndex4 != -1 && startIndex <= endIndex4) {
                return content.substring(startIndex + startTag.length(), endIndex4 ).trim();
            }
            if (startIndex != -1 && endIndex5 != -1 && startIndex <= endIndex5) {
                return content.substring(startIndex + startTag.length(), endIndex5 ).trim();
            }
            return  "Ai生成内提取失败";


        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }
}
