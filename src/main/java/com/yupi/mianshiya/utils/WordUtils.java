package com.yupi.mianshiya.utils;

import cn.hutool.core.io.resource.ClassPathResource;
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
        try (InputStream inputStream = WordUtils.class.getClassLoader().getResourceAsStream("forbiddenWords.txt")) {
            if (inputStream == null) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "违禁词文件未找到");
            }
            List<String> blackList = loadBlackListFromStream(inputStream);
            WORD_TREE.addWords(blackList);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "违禁词库初始化失败");
        }
    }


    /**
     * 从文件中加载违禁词列表
     *
     * @param inputStream 违禁词文件
     * @return 违禁词列表
     */
    private static List<String> loadBlackListFromStream(InputStream inputStream) throws IOException {
        List<String> blackList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                blackList.add(line.trim());
            }
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
