package com.yupi.mianshiya.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.yupi.mianshiya.model.dto.question.QuestionImportDTO;
import com.yupi.mianshiya.model.entity.Question;
import com.yupi.mianshiya.model.entity.User;
import com.yupi.mianshiya.service.QuestionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author 万佳羊
 * {@code @date}  2024-12-09  17:13
 * @version 1.0
 */
public class QuestionDataListener extends AnalysisEventListener<QuestionImportDTO> {

    private  QuestionService questionService;

    private final User loginUser;
    private AtomicLong maxQuestionNum;

    private final List<Question> questionList = new ArrayList<>();
    private final List<CompletableFuture<Void>>futures = new ArrayList<>();
    // 自定义线程池
    ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(
            4,                         // 核心线程数
            10,                        // 最大线程数
            60L,                       // 线程空闲存活时间
            TimeUnit.SECONDS,           // 存活时间单位
            new LinkedBlockingQueue<>(1000),  // 阻塞队列容量
            new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略：由调用线程处理任务
    );



    public QuestionDataListener(QuestionService questionService,User loginUser, Long maxQuestionNum) {
        this.loginUser = loginUser;
        this.maxQuestionNum = new AtomicLong(maxQuestionNum);
        this.questionService = questionService;
    }

    @Override
    public void invoke(QuestionImportDTO data, AnalysisContext context) {
        Question question = new Question();
        question.setTitle(data.getTitle());
        question.setContent(data.getContent());
        question.setTags(data.getTags());
        question.setAnswer(data.getAnswer());
        question.setDiffity(data.getDiffity());
        question.setIsVip(data.getIsVip());
        question.setUserId(loginUser.getId()); // 默认设置创建用户 ID，可根据业务修改
        long questionNum = maxQuestionNum.getAndIncrement();
        question.setQuestionNum(questionNum+1);
        maxQuestionNum.set(questionNum+1);
        questionService.validQuestion(question,true);
        // 合法的题目 id 列表
        questionList.add(question);
        // 批量保存每 100 条记录
        if (questionList.size() >= 500) {
            List<Question> questionListCopy = new ArrayList<>(questionList);
            questionList.clear();  // 清空原列表
            //异步，使用线程池添加
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                //使用事务防止长事务
                questionService.batchAddQuestionsInner(questionListCopy);
            }, customExecutor);
            futures.add(future);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        //添加剩余的
        if (!questionList.isEmpty()) {
            List<Question> questionListCopy = new ArrayList<>(questionList);
            questionList.clear();
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                questionService.batchAddQuestionsInner(questionListCopy);
            }, customExecutor);
            futures.add(future);
        }
        //等待所有批次操作完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        //关闭线程池
        customExecutor.shutdown();
    }

}
