package com.yupi.mianshiya.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yupi.mianshiya.common.ErrorCode;
import com.yupi.mianshiya.constant.CommonConstant;
import com.yupi.mianshiya.exception.BusinessException;
import com.yupi.mianshiya.exception.ThrowUtils;
import com.yupi.mianshiya.manager.AiManager;
import com.yupi.mianshiya.mapper.QuestionMapper;
import com.yupi.mianshiya.model.dto.question.QuestionEsDTO;
import com.yupi.mianshiya.model.dto.question.QuestionQueryRequest;
import com.yupi.mianshiya.model.dto.question.QuestionRelatedRequest;
import com.yupi.mianshiya.model.entity.Question;
import com.yupi.mianshiya.model.entity.QuestionBankQuestion;
import com.yupi.mianshiya.model.entity.User;
import com.yupi.mianshiya.model.vo.QuestionVO;
import com.yupi.mianshiya.model.vo.UserVO;
import com.yupi.mianshiya.service.QuestionBankQuestionService;
import com.yupi.mianshiya.service.QuestionService;
import com.yupi.mianshiya.service.UserService;
import com.yupi.mianshiya.utils.AiUtils;
import com.yupi.mianshiya.utils.AlgorithmUtils;
import com.yupi.mianshiya.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 题目服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 */
@Service
@Slf4j
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Resource
    private UserService userService;

    @Resource
    private QuestionBankQuestionService questionBankQuestionService;

    @Resource
    private AiManager aiManager;

    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    /**
     * AI 评分系统消息
     */
    private static final String AI_TEST_SCORING_SYSTEM_MESSAGE = "你是一位专业的程序员面试专家，我会给你如下信息：\n" +
            "```\n" +
            "面试题目，\n" +
            "【【【面试题目描述】】】，\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，根据面试题目和面试题目描述来给出答案：\n" +
            "要求：需要给出一个尽量详细，准确，易懂的答案，答案(((((((((开始，答案以)))))))))结束，返回的结果形式为(((((((((你生成的所有Ai答案)))))))));切记不用要有多余的话 ！！！,且内容必须是支持Markdown语法！！！";

    /**
     * 校验数据
     *
     * @param question
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        ThrowUtils.throwIf(question == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String title = question.getTitle();
        String content = question.getContent();
        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
        if (StringUtils.isNotBlank(title)) {
            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content)) {
            ThrowUtils.throwIf(content.length() > 10240, ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = questionQueryRequest.getId();
        Long notId = questionQueryRequest.getNotId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        String searchText = questionQueryRequest.getSearchText();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        List<String> tagList = questionQueryRequest.getTags();
        Long userId = questionQueryRequest.getUserId();
        String answer = questionQueryRequest.getAnswer();
        String diffity = questionQueryRequest.getDiffity();
        Integer isVip = questionQueryRequest.getIsVip();
        // todo 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(diffity), "diffity", diffity);
        queryWrapper.eq(ObjectUtils.isNotEmpty(isVip), "isVip", isVip);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题目封装
     *
     * @param question
     * @param request
     * @return
     */
    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        // 对象转封装类
        QuestionVO questionVO = QuestionVO.objToVo(question);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionVO.setUser(userVO);
//        // 2. 已登录，获取用户点赞、收藏状态
//        long commentId = comment.getId();
//        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            // 获取点赞
//            QueryWrapper<CommentThumb> commentThumbQueryWrapper = new QueryWrapper<>();
//            commentThumbQueryWrapper.in("commentId", commentId);
//            commentThumbQueryWrapper.eq("userId", loginUser.getId());
//            CommentThumb commentThumb = commentThumbMapper.selectOne(commentThumbQueryWrapper);
//            commentVO.setHasThumb(commentThumb != null);
//            // 获取收藏
//            QueryWrapper<CommentFavour> commentFavourQueryWrapper = new QueryWrapper<>();
//            commentFavourQueryWrapper.in("commentId", commentId);
//            commentFavourQueryWrapper.eq("userId", loginUser.getId());
//            CommentFavour commentFavour = commentFavourMapper.selectOne(commentFavourQueryWrapper);
//            commentVO.setHasFavour(commentFavour != null);
//        }
        // endregion
        return questionVO;
    }

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollUtil.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            return QuestionVO.objToVo(question);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        questionVOList.forEach(questionVO -> {
            Long userId = questionVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUser(userService.getUserVO(user));
        });
        // endregion

        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

    /**
     * 分页获取题目列表
     *
     * @param questionQueryRequest
     * @return
     */
    public Page<Question> listQuestionByPage(QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 题目表的查询条件
        QueryWrapper<Question> queryWrapper = this.getQueryWrapper(questionQueryRequest);
        queryWrapper.orderByAsc("questionNum");
        // 根据题库查询题目列表接口
        Long questionBankId = questionQueryRequest.getQuestionBankId();
        if (questionBankId != null) {
            // 查询题库内的题目 id
            LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                    .select(QuestionBankQuestion::getQuestionId)
                    .eq(QuestionBankQuestion::getQuestionBankId, questionBankId);
            List<QuestionBankQuestion> questionList = questionBankQuestionService.list(lambdaQueryWrapper);
            if (CollUtil.isNotEmpty(questionList)) {
                // 取出题目 id 集合
                Set<Long> questionIdSet = questionList.stream()
                        .map(QuestionBankQuestion::getQuestionId)
                        .collect(Collectors.toSet());
                // 复用原有题目表的查询条件
                queryWrapper.in("id", questionIdSet);
            } else {
                // 题库为空，则返回空列表
                return new Page<>(current, size, 0);
            }
        }
        // 查询数据库
        Page<Question> questionPage = this.page(new Page<>(current, size), queryWrapper);
        return questionPage;
    }

    /**
     * 批量删除题目
     *
     * @param questionIdList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteQuestions(List<Long> questionIdList) {
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "要删除的题目列表不能为空");
        //合法的id(题目存在)
        LambdaQueryWrapper<Question> queryWrapper = Wrappers.lambdaQuery(Question.class)
                .select(Question::getId)
                .in(Question::getId, questionIdList);
        // 合法的题目 id 列表
        List<Long> validQuestionIdList = this.listObjs(queryWrapper, obj -> (Long) obj);
        ThrowUtils.throwIf(CollUtil.isEmpty(validQuestionIdList), ErrorCode.PARAMS_ERROR, "合法的题目 id 列表为空");
        for (Long questionId : validQuestionIdList) {
            boolean result = this.removeById(questionId);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除题目失败");
            // 移除题目题库关系
            // 构造查询
            LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                    .eq(QuestionBankQuestion::getQuestionId, questionId);
            result = questionBankQuestionService.remove(lambdaQueryWrapper);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除题目题库关联失败");
        }
    }


    @Override
    public QuestionVO getQuestionByAi(Long questionId) {

        if (questionId == null || questionId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //获取题目
        Question question = this.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        //生成用户信息
        String userMessage = getGenerateUserMessage(question);
        // AI 生成
        String result = aiManager.doSyncStableRequest(AI_TEST_SCORING_SYSTEM_MESSAGE, userMessage);
        //提取答案
        String recomdAnswer = AiUtils.extractAnswersAsString(result);
        question.setAnswer(recomdAnswer);
        //转换
        return QuestionVO.objToVo(question);
    }

    @Override
    public List<QuestionVO> getRelatesQuesions(QuestionRelatedRequest questionRelatedRequest) {
        Long questionId = questionRelatedRequest.getId();
        Integer num = questionRelatedRequest.getNum();
        //参数校验
        if (questionId == null || questionId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (num == null || num <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //获取题目
        Question question = this.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        String tags = question.getTags();
        Gson gson = new Gson();
        List<String> questionTagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());

        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("tags");
        queryWrapper.select("id", "tags");
        List<Question> questions = this.list(queryWrapper);

        //题目列表的下标 =》相似度
        List<Pair<Question, Long>> list = new ArrayList<>();
        Map<String, Integer> map = new TreeMap<>();
        for (Map.Entry<String, Integer> stringIntegerEntry : map.entrySet()) {
            stringIntegerEntry.getValue();
        }

        //依次计算所有用户和当前用户的相似度
        for (int i = 0; i < questions.size(); i++) {
            Question questionTemp = questions.get(i);
            String questionTempTags = questionTemp.getTags();
            //无标签或者为当前用户自己
            if (StringUtils.isBlank(questionTempTags) || questionTemp.getId().equals(questionId)) {
                continue;
            }
            List<String> tempTagList = gson.fromJson(questionTempTags, new TypeToken<List<String>>() {
            }.getType());
            long distance = AlgorithmUtils.minDistance(questionTagList, tempTagList);
            list.add(new Pair<>(questionTemp, distance));
        }
        //按编辑距离升序
        List<Pair<Question, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());

        //原本顺序的userId列表
        List<Long> questionIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());

        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        queryWrapper.in("id", questionIdList);

        Map<Long, List<QuestionVO>> questionIdListMap = this.list(wrapper).stream()
                .map(QuestionVO::objToVo)
                .collect(Collectors.groupingBy(QuestionVO::getId));

        ArrayList<QuestionVO> res = new ArrayList<>();
        for (Long id : questionIdList) {
            res.add(questionIdListMap.get(id).get(0));
        }
        return res;
    }

    @Override
    public Long getMaxQuestionNum() {
        return questionMapper.selectMaxQuestionNum();
    }

    /**
     * 从 ES 查询题目
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public Page<Question> searchFromEs(QuestionQueryRequest questionQueryRequest) {

        // 获取参数
        Long id = questionQueryRequest.getId();
        Long notId = questionQueryRequest.getNotId();
        String searchText = questionQueryRequest.getSearchText();
        List<String> tags = questionQueryRequest.getTags();
        Long questionBankId = questionQueryRequest.getQuestionBankId();
        Long userId = questionQueryRequest.getUserId();
        String diffity = questionQueryRequest.getDiffity();
        Integer isVip = questionQueryRequest.getIsVip();
        Long questionNum = questionQueryRequest.getQuestionNum();
        // 注意，ES 的起始页为 0
        int current = questionQueryRequest.getCurrent() - 1;
        int pageSize = questionQueryRequest.getPageSize();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        //如果查询参数带题库
        Set<Long> questionIdSet = new HashSet<>();
        if (questionBankId != null) {
            // 查询题库内的题目 id
            LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                    .select(QuestionBankQuestion::getQuestionId)
                    .eq(QuestionBankQuestion::getQuestionBankId, questionBankId);
            List<QuestionBankQuestion> questionList2 = questionBankQuestionService.list(lambdaQueryWrapper);

            if (CollUtil.isNotEmpty(questionList2)) {
                // 取出题目 id 集合
                 questionIdSet = questionList2.stream()
                        .map(QuestionBankQuestion::getQuestionId)
                        .collect(Collectors.toSet());
            } else {
                // 题库为空，则返回空列表
                return new Page<>();
            }
        }

        // 构造查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 过滤
        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
        if (id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("id", id));
        }
        if (notId != null) {
            boolQueryBuilder.mustNot(QueryBuilders.termQuery("id", notId));
        }
        if (userId != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("userId", userId));
        }
        if (diffity != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("diffity", diffity));
        }
        if (isVip != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("isVip", isVip));
        }
        if (questionNum != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("questionNum", questionNum));
        }

        // 必须包含所有标签
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("tags", tag));
            }
        }
        // 按关键词检索
        if (StringUtils.isNotBlank(searchText)) {
            // title = '' or content = '' or answer = ''
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("answer", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 排序
        SortBuilder<?> sortBuilder = SortBuilders.scoreSort();
        if (StringUtils.isNotBlank(sortField)) {
            sortBuilder = SortBuilders.fieldSort(sortField);
            sortBuilder.order(CommonConstant.SORT_ORDER_ASC.equals(sortOrder) ? SortOrder.ASC : SortOrder.DESC);
        }
        // 分页
        PageRequest pageRequest = PageRequest.of(current, pageSize);
        // 构造查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withPageable(pageRequest)
                .withSorts(sortBuilder)
                .build();
        SearchHits<QuestionEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, QuestionEsDTO.class);
//        // 复用 MySQL / MyBatis Plus 的分页对象，封装返回结果
        Page<Question> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        List<Question> resourceList = new ArrayList<>();
//        if (searchHits.hasSearchHits()) {
//            List<SearchHit<QuestionEsDTO>> searchHitList = searchHits.getSearchHits();
//            for (SearchHit<QuestionEsDTO> questionEsDTOSearchHit : searchHitList) {
//                resourceList.add(QuestionEsDTO.dtoToObj(questionEsDTOSearchHit.getContent()));
//            }
//        }
//        page.setRecords(resourceList);
//        return page;
        // 查出结果后，从 db 获取最新动态数据（比如点赞数）
        if (searchHits.hasSearchHits()) {
            List<SearchHit<QuestionEsDTO>> searchHitList = searchHits.getSearchHits();
            List<Long> questionIdList = searchHitList.stream().map(searchHit -> searchHit.getContent().getId())
                    .collect(Collectors.toList());
            List<Question> questionList = baseMapper.selectBatchIds(questionIdList);
            if (questionList != null) {
                Map<Long, List<Question>> questionIdMap = questionList.stream().collect(Collectors.groupingBy(Question::getId));
                Set<Long> finalQuestionIdSet = questionIdSet;
                questionIdList.forEach(questionId -> {
                    if (questionIdMap.containsKey(questionId)) {
                        if (finalQuestionIdSet.size()>0&&!finalQuestionIdSet.contains(questionId)){
                            return;
                        }
                        resourceList.add(questionIdMap.get(questionId).get(0));
                    } else {
                        // 从 es 清空 db 已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(questionId), QuestionEsDTO.class);
                        log.info("delete questio {}", delete);
                    }
                });
            }

        }
        page.setRecords(resourceList);
        return page;
    }


    /**
     * 生成题目的用户消息
     *
     * @return
     */
    private String getGenerateUserMessage(Question question) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(question.getTitle()).append("\n");
        userMessage.append(question.getContent()).append("\n");
        return userMessage.toString();
    }

}
