package com.yupi.mianshiya.utils;

import cn.hutool.dfa.WordTree;
import com.yupi.mianshiya.common.ErrorCode;
import com.yupi.mianshiya.exception.BusinessException;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 内容工具类
 */
public class WordUtils {
    private static final WordTree WORD_TREE;

    static {
        WORD_TREE = new WordTree();
        try {
            File file = ResourceUtils.getFile("classpath:forbiddenWords.txt");
            List<String> blackList = loadBlackListFromFile(file);
            WORD_TREE.addWords(blackList);
        } catch (FileNotFoundException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "读取违禁词文件出错");
        }
    }

    /**
     * 从文件中加载违禁词列表
     *
     * @param file 违禁词文件
     * @return 违禁词列表
     */
    private static List<String> loadBlackListFromFile(File file) {
        List<String> blackList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                blackList.add(line.trim()); // 去掉首尾空格
            }
        } catch (IOException e) {
            System.err.println("读取违禁词文件时出错: " + e.getMessage());
            e.printStackTrace();
        }
        return blackList;
    }

    /**
     * 检测文本中是否包含违禁词
     *
     * @param content 输入文本
     * @return 是否包含违禁词
     */
    public static boolean containsForbiddenWords(String content) {
        return !WORD_TREE.matchAll(content).isEmpty();
    }

    /**
     * 提取文本中的违禁词列表
     *
     * @param content 输入文本
     * @return 检测到的违禁词列表
     */
    public static List<String> extractForbiddenWords(String content) {
        return WORD_TREE.matchAll(content);
    }

}
