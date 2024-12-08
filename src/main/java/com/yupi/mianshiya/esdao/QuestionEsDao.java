package com.yupi.mianshiya.esdao;

import com.yupi.mianshiya.model.dto.question.QuestionEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * 题目 ES 操作
 */
public interface QuestionEsDao extends ElasticsearchRepository<QuestionEsDTO, Long> {

    List<QuestionEsDTO> findByUserId(Long userId);
}