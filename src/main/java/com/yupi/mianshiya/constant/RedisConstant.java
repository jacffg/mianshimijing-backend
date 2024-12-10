package com.yupi.mianshiya.constant;

/**
 * @author 万佳羊
 * {@code @date}  2024-12-07  16:03
 * @version 1.0
 */
public interface RedisConstant {

    /**
     * 用户签到记录的 Redis Key 前缀
     */
    String USER_SIGN_IN_REDIS_KEY_PREFIX = "user:signins";
    /**
     * 用户签到记录的 Redis Key 前缀
     */
    String USER_BROWSE_QUESTION_KEY_PREFIX = "user:browser";
    /**
     * 热门题目
     */
    String QUESTION_HOT_QUESTIONS = "question:hot:questions";
    /**
     * 热门标签
     */
    String QUESTION_HOT_TAGS= "question:hot:tags";
    /**
     * Ai答案
     */
    String QUESTION_AIGENERATE_PREFIX_= "question:ai";

    /**
     热门题目和热门标签分布式锁的前缀
      */
     String HOT_LOCK = "HOT_LOCK";

    /**
     AI生成答案分布式锁的前缀
     */
    String QUESTION_AI_LOCK = "QUESTION_AI_LOCK";
    /**
     * 获取用户签到记录的 Redis Key
     * @param year 年份
     * @param userId 用户 id
     * @return 拼接好的 Redis Key
     */
    static String getUserSignInRedisKey(int year, long userId) {
        return String.format("%s:%s:%s", USER_SIGN_IN_REDIS_KEY_PREFIX, year, userId);
    }
    /**
     * 获取用户浏览题目的 Redis Key
     * @param questionId 题目id
     * @param userIp 用户 ip
     * @return 拼接好的 Redis Key
     */
    static String getUserBrowseQuestionKeyPrefix(long questionId, String userIp) {
        return String.format("%s:%s:%s", USER_BROWSE_QUESTION_KEY_PREFIX, questionId, userIp);
    }
    /**
     * 获取AI生成题目的 Redis Key
     * @param questionId 题目id
     * @return 拼接好的 Redis Key
     */
    static String getQuestionAigeneratePrefix(long questionId) {
        return String.format("%s:%s", QUESTION_AIGENERATE_PREFIX_, questionId);
    }
 }
