package com.yupi.mianshiya.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.mianshiya.model.entity.Comment;
import com.yupi.mianshiya.service.CommentService;
import com.yupi.mianshiya.mapper.CommentMapper;
import org.springframework.stereotype.Service;

/**
* @author 万佳羊
* @description 针对表【comment】的数据库操作Service实现
* @createDate 2024-12-02 10:41:09
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

}




