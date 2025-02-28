# 数据库初始化
# @author <a href="https://github.com/liyupi">程序员鱼皮</a>
# @from <a href="https://yupi.icu">编程导航知识星球</a>

-- 创建库
create database if not exists mianshiya;

-- 切换库
use mianshiya;
    -- auto-generated definition
create table user
(
    id             bigint auto_increment comment 'id'
        primary key,
    userAccount    varchar(256)                           not null comment '账号',
    userPassword   varchar(512)                           not null comment '密码',
    unionId        varchar(256)                           null comment '微信开放平台id',
    mpOpenId       varchar(256)                           null comment '公众号openId',
    userName       varchar(256)                           null comment '用户昵称',
    userAvatar     varchar(1024)                          null comment '用户头像',
    userProfile    varchar(512)                           null comment '用户简介',
    userRole       varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    editTime       datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint      default 0                 not null comment '是否删除',
    likeShowAnswer tinyint      default 0                 not null
)
    comment '用户' collate = utf8mb4_unicode_ci;

create index idx_unionId
    on user (unionId);


-- auto-generated definition
create table question
(
    id          bigint auto_increment comment 'id'
        primary key,
    title       varchar(256)                         null comment '标题',
    content     text                                 null comment '内容',
    tags        varchar(1024)                        null comment '标签列表（json 数组）',
    answer      text                                 null comment '推荐答案',
    userId      bigint                               not null comment '创建用户 id',
    editTime    datetime   default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime  datetime   default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint    default 0                 not null comment '是否删除',
    diffity     varchar(1024)                        null comment '难度',
    isVip       tinyint(1) default 0                 null comment '是否为会员专属
',
    viewNum     bigint     default 0                 not null,
    questionNum bigint     default 0                 not null comment '题目编号',
    favourNum   int        default 0                 not null,
    constraint questionNum
        unique (questionNum)
)
    comment '题目表' collate = utf8mb4_unicode_ci;

create index idx_title
    on question (title);

create index idx_userId
    on question (userId);
-- auto-generated definition
create table question_bank
(
    id          bigint auto_increment comment 'id'
        primary key,
    title       varchar(256)                       null comment '标题',
    description text                               null comment '描述',
    picture     varchar(2048)                      null comment '图片',
    userId      bigint                             not null comment '创建用户 id',
    editTime    datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除'
)
    comment '题库' collate = utf8mb4_unicode_ci;

create index idx_title
    on question_bank (title);

-- auto-generated definition
create table question_bank_question
(
    id             bigint auto_increment comment 'id'
        primary key,
    questionBankId bigint                             not null comment '题库 id',
    questionId     bigint                             not null comment '题目 id',
    userId         bigint                             not null comment '创建用户 id',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint questionBankId
        unique (questionBankId, questionId)
)
    comment '题库题目' collate = utf8mb4_unicode_ci;

-- auto-generated definition
create table question_favour
(
    id         bigint auto_increment comment 'id'
        primary key,
    questionId bigint                             not null comment '题目 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '题目收藏';

create index idx_questionId
    on question_favour (questionId);

create index idx_userId
    on question_favour (userId);

-- auto-generated definition
create table question_mark
(
    id         bigint auto_increment comment '标记 ID'
        primary key,
    questionId bigint                             not null comment '题目 ID',
    userId     bigint                             not null comment '用户 ID',
    markType   varchar(512)                       not null comment '标记类型',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '题目标记';

-- auto-generated definition
create table redeem
(
    id             bigint auto_increment comment 'id'
        primary key,
    code           varchar(1024)                      null comment '兑换码',
    userId         bigint                             null comment '使用用户 id',
    useTime        datetime                           null comment '使用时间',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isUsed         tinyint  default 0                 not null comment '是否被使用',
    isDelete       bigint   default 0                 not null,
    expirationTime datetime default CURRENT_TIMESTAMP not null comment '过期时间'
)
    comment '兑换表' collate = utf8mb4_unicode_ci;



create table comment_0
(
    id         bigint auto_increment comment '评论 ID'
        primary key,
    questionId bigint                             not null comment '题目 ID',
    userId     bigint                             not null comment '用户 ID',
    content    text                               not null comment '评论内容',
    parentId   bigint                             null comment '父评论 ID，支持多级嵌套回复',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    ancestorId bigint                             null
);

create index comment_questionId
    on comment_0 (questionId);

-- auto-generated definition
create table comment_1
(
    id         bigint auto_increment comment '评论 ID'
        primary key,
    questionId bigint                             not null comment '题目 ID',
    userId     bigint                             not null comment '用户 ID',
    content    text                               not null comment '评论内容',
    parentId   bigint                             null comment '父评论 ID，支持多级嵌套回复',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    ancestorId bigint                             null
);

create index comment_questionId
    on comment_1 (questionId);

-- 模拟面试表
create table if not exists mock_interview
(
    id             bigint auto_increment comment 'id' primary key,
    workExperience varchar(256)                       not null comment '工作年限',
    jobPosition    varchar(256)                       not null comment '工作岗位',
    difficulty     varchar(50)                        not null comment '面试难度',
    messages       mediumtext                         null comment '消息列表（JSON 对象数组字段，同时包括了总结）',
    status         int      default 0                 not null comment '状态（0-待开始、1-进行中、2-已结束）',
    userId         bigint                             not null comment '创建人（用户 id）',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除（逻辑删除）',
    index idx_userId (userId)
    ) comment '模拟面试' collate = utf8mb4_unicode_ci;
