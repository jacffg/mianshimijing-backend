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

//    static {
//        WORD_TREE = new WordTree();
//        try {
////            File file = ResourceUtils.getFile("classpath:forbiddenWords.txt");
//            ClassPathResource classPathResource = new ClassPathResource("forbiddenWords.txt");
//            File file = classPathResource.getFile();
//            List<String> blackList = loadBlackListFromFile(file);
//            WORD_TREE.addWords(blackList);
//        } catch (Exception e) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "读取违禁词文件出错");
//        }
//    }
    static {
        WORD_TREE = new WordTree();


        WORD_TREE.addWords("fuck","shit","bitch","cunt","pussy","dick","asshole","bastard","twat","slut","whore","nigger","nigga","nazi","nazis","naziist","nazist","nazis","naz","nazis","nazis","nazis","nazis","nazis","naz");

        String forbiddenWords = "13点\n" +
                "三级片\n" +
                "下三烂\n" +
                "下贱\n" +
                "个老子的\n" +
                "九游\n" +
                "乳\n" +
                "脑残\n" +
                "脑瘫\n" +
                "司马\n" +
                "死妈\n" +
                "尼玛\n" +
                "你妈了逼\n" +
                "乳交\n" +
                "乳头\n" +
                "乳房\n" +
                "乳波臀浪\n" +
                "交配\n" +
                "仆街\n" +
                "他奶奶\n" +
                "他奶奶的\n" +
                "他奶娘的\n" +
                "他妈\n" +
                "他妈ㄉ王八蛋\n" +
                "他妈地\n" +
                "他妈的\n" +
                "他娘\n" +
                "他马的\n" +
                "你个傻比\n" +
                "你他马的\n" +
                "你全家\n" +
                "你奶奶的\n" +
                "你她马的\n" +
                "你妈\n" +
                "你妈的\n" +
                "你娘\n" +
                "你娘卡好\n" +
                "你娘咧\n" +
                "你它妈的\n" +
                "你它马的\n" +
                "你是鸡\n" +
                "你是鸭\n" +
                "你马的\n" +
                "做爱\n" +
                "傻比\n" +
                "傻逼\n" +
                "册那\n" +
                "军妓\n" +
                "几八\n" +
                "几叭\n" +
                "几巴\n" +
                "几芭\n" +
                "刚度\n" +
                "刚瘪三\n" +
                "包皮\n" +
                "十三点\n" +
                "卖B\n" +
                "卖比\n" +
                "卖淫\n" +
                "卵\n" +
                "卵子\n" +
                "双峰微颤\n" +
                "口交\n" +
                "口肯\n" +
                "叫床\n" +
                "吃屎\n" +
                "后庭\n" +
                "吹箫\n" +
                "塞你公\n" +
                "塞你娘\n" +
                "塞你母\n" +
                "塞你爸\n" +
                "塞你老师\n" +
                "塞你老母\n" +
                "处女\n" +
                "外阴\n" +
                "大卵子\n" +
                "大卵泡\n" +
                "大鸡巴\n" +
                "奶\n" +
                "奶奶的熊\n" +
                "奶子\n" +
                "奸\n" +
                "奸你\n" +
                "她妈地\n" +
                "她妈的\n" +
                "她马的\n" +
                "妈B\n" +
                "妈个B\n" +
                "妈个比\n" +
                "妈个老比\n" +
                "妈妈的\n" +
                "妈比\n" +
                "妈的\n" +
                "妈的B\n" +
                "妈逼\n" +
                "妓\n" +
                "妓女\n" +
                "妓院\n" +
                "妳她妈的\n" +
                "妳妈的\n" +
                "妳娘的\n" +
                "妳老母的\n" +
                "妳马的\n" +
                "姘头\n" +
                "姣西\n" +
                "姦\n" +
                "娘个比\n" +
                "娘的\n" +
                "婊子\n" +
                "婊子养的\n" +
                "嫖娼\n" +
                "嫖客\n" +
                "它妈地\n" +
                "它妈的\n" +
                "密洞\n" +
                "射你\n" +
                "射精\n" +
                "小乳头\n" +
                "小卵子\n" +
                "小卵泡\n" +
                "小瘪三\n" +
                "小肉粒\n" +
                "小骚比\n" +
                "小骚货\n" +
                "小鸡巴\n" +
                "小鸡鸡\n" +
                "屁眼\n" +
                "屁股\n" +
                "屄\n" +
                "屌\n" +
                "巨乳\n" +
                "干x娘\n" +
                "干七八\n" +
                "干你\n" +
                "干你妈\n" +
                "干你娘\n" +
                "干你老母\n" +
                "干你良\n" +
                "干妳妈\n" +
                "干妳娘\n" +
                "干妳老母\n" +
                "干妳马\n" +
                "干您娘\n" +
                "干机掰\n" +
                "干死CS\n" +
                "干死GM\n" +
                "干死你\n" +
                "干死客服\n" +
                "幹\n" +
                "强奸\n" +
                "强奸你\n" +
                "性\n" +
                "性交\n" +
                "性器\n" +
                "性无能\n" +
                "性爱\n" +
                "情色\n" +
                "想上你\n" +
                "懆您妈\n" +
                "懆您娘\n" +
                "懒8\n" +
                "懒八\n" +
                "懒叫\n" +
                "懒教\n" +
                "成人\n" +
                "我操你祖宗十八代\n" +
                "扒光\n" +
                "打炮\n" +
                "打飞机\n" +
                "抽插\n" +
                "招妓\n" +
                "插你\n" +
                "插死你\n" +
                "撒尿\n" +
                "操你\n" +
                "操你全家\n" +
                "操你奶奶\n" +
                "操你妈\n" +
                "操你娘\n" +
                "操你祖宗\n" +
                "操你老妈\n" +
                "操你老母\n" +
                "操妳\n" +
                "操妳全家\n" +
                "操妳妈\n" +
                "操妳娘\n" +
                "操妳祖宗\n" +
                "操机掰\n" +
                "操比\n" +
                "操逼\n" +
                "放荡\n" +
                "日他娘\n" +
                "日你\n" +
                "日你妈\n" +
                "日你老娘\n" +
                "日你老母\n" +
                "日批\n" +
                "月经\n" +
                "机八\n" +
                "机巴\n" +
                "机机歪歪\n" +
                "杂种\n" +
                "浪叫\n" +
                "淫\n" +
                "淫乱\n" +
                "淫妇\n" +
                "淫棍\n" +
                "淫水\n" +
                "淫秽\n" +
                "淫荡\n" +
                "淫西\n" +
                "湿透的内裤\n" +
                "激情\n" +
                "灨你娘\n" +
                "烂货\n" +
                "烂逼\n" +
                "爛\n" +
                "狗屁\n" +
                "狗日\n" +
                "狗狼养的\n" +
                "玉杵\n" +
                "王八蛋\n" +
                "瓜娃子\n" +
                "瓜婆娘\n" +
                "瓜批\n" +
                "瘪三\n" +
                "白烂\n" +
                "白痴\n" +
                "白癡\n" +
                "祖宗\n" +
                "私服\n" +
                "笨蛋\n" +
                "精子\n" +
                "老二\n" +
                "老味\n" +
                "老母\n" +
                "老瘪三\n" +
                "老骚比\n" +
                "老骚货\n" +
                "肉壁\n" +
                "肉棍子\n" +
                "肉棒\n" +
                "肉缝\n" +
                "肏\n" +
                "肛交\n" +
                "肥西\n" +
                "色情\n" +
                "花柳\n" +
                "荡妇\n" +
                "賤\n" +
                "贝肉\n" +
                "贱B\n" +
                "贱人\n" +
                "贱货\n" +
                "贼你妈\n" +
                "赛你老母\n" +
                "赛妳阿母\n" +
                "赣您娘\n" +
                "轮奸\n" +
                "迷药\n" +
                "逼\n" +
                "逼样\n" +
                "野鸡\n" +
                "阳具\n" +
                "阳萎\n" +
                "阴唇\n" +
                "阴户\n" +
                "阴核\n" +
                "阴毛\n" +
                "阴茎\n" +
                "阴道\n" +
                "阴部\n" +
                "雞巴\n" +
                "靠北\n" +
                "靠母\n" +
                "靠爸\n" +
                "靠背\n" +
                "靠腰\n" +
                "驶你公\n" +
                "驶你娘\n" +
                "驶你母\n" +
                "驶你爸\n" +
                "驶你老师\n" +
                "驶你老母\n" +
                "骚比\n" +
                "骚货\n" +
                "骚逼\n" +
                "鬼公\n" +
                "鸡8\n" +
                "鸡八\n" +
                "鸡叭\n" +
                "鸡吧\n" +
                "鸡奸\n" +
                "鸡巴\n" +
                "鸡芭\n" +
                "鸡鸡\n" +
                "龟儿子\n" +
                "龟头\n" +
                "柒\n" +
                "閪\n" +
                "仆街\n" +
                "咸家鏟\n" +
                "冚家鏟\n" +
                "咸家伶\n" +
                "冚家拎\n" +
                "笨實\n" +
                "粉腸\n" +
                "屎忽\n" +
                "躝癱\n" +
                "你老闆\n" +
                "你老味\n" +
                "你老母\n" +
                "硬膠\n" +
                "毒品\n" +
                "死全家\n" +
                "变态\n" +
                "恐怖主义\n" +
                "诈骗\n" +
                "母猪\n" +
                "屠戮\n" +
                "傻X\n" +
                "海洛因\n" +
                "吸毒\n" +
                "想死";
    String[] split = forbiddenWords.split("\n");
    WORD_TREE.addWords(split);
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
