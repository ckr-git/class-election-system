-- 班级干部评选系统数据库

CREATE DATABASE IF NOT EXISTS election_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE election_system;

-- 用户表
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '学号/工号',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
  `nickname` VARCHAR(50) COMMENT '姓名',
  `avatar` VARCHAR(255) COMMENT '头像',
  `class_id` BIGINT COMMENT '班级ID',
  `phone` VARCHAR(20) COMMENT '手机号',
  `email` VARCHAR(100) COMMENT '邮箱',
  `role` VARCHAR(20) DEFAULT 'STUDENT' COMMENT '角色：STUDENT-学生 ADMIN-管理员',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_username (`username`),
  INDEX idx_class (`class_id`),
  INDEX idx_role (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 班级表
CREATE TABLE `class` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '班级ID',
  `name` VARCHAR(100) NOT NULL COMMENT '班级名称',
  `grade` VARCHAR(20) COMMENT '年级',
  `major` VARCHAR(100) COMMENT '专业',
  `teacher` VARCHAR(50) COMMENT '班主任',
  `student_count` INT DEFAULT 0 COMMENT '学生人数',
  `deleted` TINYINT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 职位表
CREATE TABLE `position` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '职位ID',
  `name` VARCHAR(50) NOT NULL COMMENT '职位名称',
  `description` VARCHAR(255) COMMENT '职位描述',
  `responsibilities` TEXT COMMENT '职责说明',
  `max_count` INT DEFAULT 1 COMMENT '职位人数',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `deleted` TINYINT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职位表';

-- 选举活动表
CREATE TABLE `election` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '选举ID',
  `title` VARCHAR(200) NOT NULL COMMENT '选举标题',
  `description` TEXT COMMENT '选举描述',
  `class_id` BIGINT COMMENT '班级ID，NULL表示全校选举',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `apply_start_time` DATETIME COMMENT '报名开始时间',
  `apply_end_time` DATETIME COMMENT '报名结束时间',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-未开始 1-报名中 2-投票中 3-已结束',
  `vote_limit` INT DEFAULT 1 COMMENT '每人可投票数',
  `creator_id` BIGINT COMMENT '创建人ID',
  `deleted` TINYINT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_class (`class_id`),
  INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选举活动表';

-- 候选人表
CREATE TABLE `candidate` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '候选人ID',
  `election_id` BIGINT NOT NULL COMMENT '选举ID',
  `position_id` BIGINT NOT NULL COMMENT '职位ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `slogan` VARCHAR(500) COMMENT '竞选口号',
  `intro` TEXT COMMENT '个人简介',
  `achievements` TEXT COMMENT '成就列表',
  `photo` VARCHAR(255) COMMENT '竞选照片',
  `vote_count` INT DEFAULT 0 COMMENT '得票数',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待审核 1-审核通过 2-审核拒绝',
  `review_opinion` VARCHAR(255) COMMENT '审核意见',
  `deleted` TINYINT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_election (`election_id`),
  INDEX idx_position (`position_id`),
  INDEX idx_user (`user_id`),
  INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='候选人表';

-- 投票记录表
CREATE TABLE `vote_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '投票ID',
  `election_id` BIGINT NOT NULL COMMENT '选举ID',
  `voter_id` BIGINT NOT NULL COMMENT '投票人ID',
  `candidate_id` BIGINT NOT NULL COMMENT '候选人ID',
  `ip_address` VARCHAR(50) COMMENT 'IP地址',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_election_voter_candidate (`election_id`, `voter_id`, `candidate_id`),
  INDEX idx_election (`election_id`),
  INDEX idx_voter (`voter_id`),
  INDEX idx_candidate (`candidate_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投票记录表';

-- 申请记录表
CREATE TABLE `application` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
  `election_id` BIGINT NOT NULL COMMENT '选举ID',
  `position_id` BIGINT NOT NULL COMMENT '职位ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `reason` TEXT COMMENT '申请理由',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待审核 1-通过 2-拒绝',
  `review_opinion` VARCHAR(255) COMMENT '审核意见',
  `reviewer_id` BIGINT COMMENT '审核人ID',
  `review_time` DATETIME COMMENT '审核时间',
  `deleted` TINYINT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_election (`election_id`),
  INDEX idx_user (`user_id`),
  INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申请记录表';

-- 公告表
CREATE TABLE `announcement` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容',
  `type` TINYINT DEFAULT 1 COMMENT '类型：1-系统公告 2-选举公告',
  `priority` TINYINT DEFAULT 0 COMMENT '优先级：0-普通 1-重要',
  `publisher_id` BIGINT COMMENT '发布人ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-草稿 1-已发布',
  `publish_time` DATETIME COMMENT '发布时间',
  `deleted` TINYINT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_type (`type`),
  INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 反馈意见表
CREATE TABLE `feedback` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '反馈ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` TINYINT NOT NULL COMMENT '类型：1-建议 2-投诉 3-咨询',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容',
  `contact` VARCHAR(100) COMMENT '联系方式',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待处理 1-处理中 2-已处理',
  `reply` TEXT COMMENT '回复内容',
  `handler_id` BIGINT COMMENT '处理人ID',
  `handle_time` DATETIME COMMENT '处理时间',
  `deleted` TINYINT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_user (`user_id`),
  INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='反馈意见表';

-- 角色权限表
CREATE TABLE `role` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
  `name` VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
  `code` VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
  `description` VARCHAR(255) COMMENT '角色描述',
  `deleted` TINYINT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
CREATE TABLE `permission` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
  `name` VARCHAR(50) NOT NULL COMMENT '权限名称',
  `code` VARCHAR(50) NOT NULL UNIQUE COMMENT '权限编码',
  `type` TINYINT NOT NULL COMMENT '类型：1-菜单 2-按钮',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父权限ID',
  `path` VARCHAR(200) COMMENT '路径',
  `icon` VARCHAR(50) COMMENT '图标',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `deleted` TINYINT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE `role_permission` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `permission_id` BIGINT NOT NULL COMMENT '权限ID',
  UNIQUE KEY uk_role_permission (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 用户角色关联表
CREATE TABLE `user_role` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  UNIQUE KEY uk_user_role (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 系统日志表
CREATE TABLE `system_log` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
  `user_id` BIGINT COMMENT '用户ID',
  `username` VARCHAR(50) COMMENT '用户名',
  `operation` VARCHAR(100) COMMENT '操作',
  `method` VARCHAR(200) COMMENT '请求方法',
  `params` TEXT COMMENT '请求参数',
  `ip` VARCHAR(50) COMMENT 'IP地址',
  `time` BIGINT COMMENT '耗时（毫秒）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user (`user_id`),
  INDEX idx_create_time (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';
