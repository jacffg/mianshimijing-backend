package com.yupi.mianshiya.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.mianshiya.annotation.AuthCheck;
import com.yupi.mianshiya.common.BaseResponse;
import com.yupi.mianshiya.common.DeleteRequest;
import com.yupi.mianshiya.common.ErrorCode;
import com.yupi.mianshiya.common.ResultUtils;
import com.yupi.mianshiya.constant.UserConstant;
import com.yupi.mianshiya.exception.BusinessException;
import com.yupi.mianshiya.exception.ThrowUtils;
import com.yupi.mianshiya.model.dto.question.*;
import com.yupi.mianshiya.model.entity.Question;
import com.yupi.mianshiya.model.entity.User;
import com.yupi.mianshiya.model.enums.UserRoleEnum;
import com.yupi.mianshiya.model.vo.HotTagsVO;
import com.yupi.mianshiya.model.vo.QuestionVO;
import com.yupi.mianshiya.rabbitmq.MyMessageProducer;
import com.yupi.mianshiya.service.QuestionService;
import com.yupi.mianshiya.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.yupi.mianshiya.constant.FileConstant.GLOBAL_TASK_DIR_NAME;
import static com.yupi.mianshiya.constant.RedisConstant.getUserBrowseQuestionKeyPrefix;

/**
 * 题目接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private MyMessageProducer myMessageProducer;

    // region 增删改查

    /**
     * 创建题目
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        // 数据校验
        questionService.validQuestion(question, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);

        // 查询最大的 questionNum
        Long maxQuestionNum = questionService.getMaxQuestionNum();
        question.setQuestionNum(maxQuestionNum + 1);

        question.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除题目
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新题目（仅管理员可用）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        // 数据校验
        questionService.validQuestion(question, false);
        // 判断是否存在
        long id = questionUpdateRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题目（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Question question = questionService.getById(id);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取用户的 IP 地址
        String userIp = request.getRemoteAddr();

        // 通过用户 IP 来区分浏览者，避免重复增加浏览数
        String key = getUserBrowseQuestionKeyPrefix(question.getId(), userIp);

        // 使用缓存框架判断该 IP 是否已经浏览过
        if (!redissonClient.getBucket(key).isExists()) {
            // 该 IP 没有浏览过，增加浏览数并设置过期时间
            question.setViewNum(question.getViewNum() + 1);
            questionService.updateById(question);
            redissonClient.getBucket(key).set(key, 5, TimeUnit.MINUTES);
        }

        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }


    /**
     * 分页获取题目列表（仅管理员可用）
     *
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Question> questionPage = questionService.listQuestionByPage(questionQueryRequest);
        return ResultUtils.success(questionPage);
    }

    /**
     * 分页获取题目列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest request) {
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size >= 200, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Question> questionPage = questionService.listQuestionByPage(questionQueryRequest);
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取当前登录用户创建的题目列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 编辑题目（给用户使用）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        // 数据校验
        questionService.validQuestion(question, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = questionEditRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete/batch")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> batchDeleteQuestions(@RequestBody QuestionBatchDeleteRequest questionBatchDeleteRequest) {
        ThrowUtils.throwIf(questionBatchDeleteRequest == null, ErrorCode.PARAMS_ERROR);
        questionService.batchDeleteQuestions(questionBatchDeleteRequest.getQuestionIdList());
        return ResultUtils.success(true);
    }

    /**
     * 批量导入题目
     *
     * @param file Excel 文件
     * @return 导入结果
     */
    @PostMapping("/import")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> importQuestions(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }
        try {
            User loginUser = userService.getLoginUser(request);
            // 生成任务 ID
            String taskId = UUID.randomUUID().toString();
            // 获取工作目录（适合运行时写入）
            String userDir = System.getProperty("user.dir");
            String globalTaskPathName = userDir + File.separator + GLOBAL_TASK_DIR_NAME;
            // 判断全局代码目录是否存在，没有则新建
            try {
                if (!FileUtil.exist(globalTaskPathName)) {
                    FileUtil.mkdir(globalTaskPathName);
                }
            } catch (Exception e) {
                throw  new BusinessException(ErrorCode.SYSTEM_ERROR,"创建目录失败");
            }
            // 保存文件到指定目录，命名为 taskId.xls
            File savedFile = new File(globalTaskPathName, taskId + ".xls");
            file.transferTo(savedFile);
            QuestionImportTask questionImportTask = new QuestionImportTask();
            questionImportTask.setTaskId(taskId);
            questionImportTask.setUser(loginUser);
            // 发送消息
            myMessageProducer.sendQuestioTaskMessage("task_exchange", "my_routingKey", questionImportTask);
            return ResultUtils.success("任务创建成功任务id为"+taskId);

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "导入失败");
        }
    }

    /**
     * 获取热门标签
     *
     * @param request
     * @return
     */
    @GetMapping("/hot/tags")
    public BaseResponse<List<HotTagsVO>> getHotTags(HttpServletRequest request) {

        //查询浏览数前十的题目
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Question::getViewNum)
                .last("LIMIT 10");  // 添加 LIMIT 子句，限制前 10 条
        List<Question> questions = questionService.list(queryWrapper);

        //获取所有的标签
        Map<String, Long> map = new HashMap<>();
        //题目和标签
        Map<String, Long> mapViewNUm = new HashMap<>();
        questions.stream().forEach(question -> {

            List<String> tags = JSONUtil.toList(question.getTags(), String.class);
            for (String tag : tags) {
                if (mapViewNUm.get(tag) == null) {
                    mapViewNUm.put(tag, question.getViewNum());
                } else {
                    mapViewNUm.put(tag, mapViewNUm.get(tag) + question.getViewNum());
                }
                if (map.get(tag) == null) {
                    map.put(tag, 1L);
                } else {
                    map.put(tag, map.get(tag) + 1);
                }
            }
        });
        for (String s : map.keySet()) {
            map.put(s, (long) (map.get(s) * mapViewNUm.get(s) * 0.001));
        }

        // 按 value 排序并将 key 放入列表中(前十)
        List<String> hotTags = map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))  // 按 value 降序排序
                .limit(10)                                                     // 限制前 10 个
                .map(Map.Entry::getKey)                // 提取 key
                .collect(Collectors.toList());         // 收集到 List 中
        List<HotTagsVO> res = new ArrayList<>();
        hotTags.forEach(tag -> {
            res.add(new HotTagsVO(map.get(tag), tag));
        });
        return ResultUtils.success(res);
    }

    /**
     * 获取热门标签
     *
     * @param request
     * @return
     */
    @GetMapping("/hot/question")
    public BaseResponse<List<QuestionVO>> getHotQuestions(HttpServletRequest request) {

        //查询浏览数前十的题目
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Question::getViewNum)
                .last("LIMIT 10");  // 添加 LIMIT 子句，限制前 10 条
        List<Question> questions = questionService.list(queryWrapper);
        ArrayList<QuestionVO> res = new ArrayList<>();
        questions.stream().forEach(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            res.add(questionVO);
        });
        return ResultUtils.success(res);
    }

    /**
     * ai生成推荐答案
     *
     * @param request
     * @return
     */
    @PostMapping("/ai_generate")
    public BaseResponse<QuestionVO> aiGenerateQuestion(Long questionId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //仅限管理员和vip调用
        if (!loginUser.getUserRole().equals(UserConstant.ADMIN_ROLE) && !loginUser.getUserRole().equals(UserConstant.VIP_ROLE)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(questionService.getQuestionByAi(questionId));
    }

    /**
     * 获取相关题目
     *
     * @param request
     * @return
     */
    @PostMapping("/getRelatedQuestions")
    public BaseResponse<List<QuestionVO>> getRelatedQuestions(@RequestBody QuestionRelatedRequest questionRelatedRequest, HttpServletRequest request) {
        return ResultUtils.success(questionService.getRelatesQuesions(questionRelatedRequest));
    }

    @PostMapping("/search/page/vo")
    public BaseResponse<Page<QuestionVO>> searchQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 200, ErrorCode.PARAMS_ERROR);
        //如果出错了就查数据库
        try {
            Page<Question> questionPage = questionService.searchFromEs(questionQueryRequest);
            return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
        } catch (Exception e) {
            // 查询数据库
            Page<Question> questionPage = questionService.listQuestionByPage(questionQueryRequest);
            // 获取封装类
            return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
        }
    }

    // endregion
}
