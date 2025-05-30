-- 创建数据库
CREATE DATABASE IF NOT EXISTS library DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE library;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL COMMENT '用户名/学号',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    role VARCHAR(20) NOT NULL COMMENT '角色：STUDENT/ADMIN',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username)
) COMMENT '用户表';

-- 图书分类表
CREATE TABLE IF NOT EXISTS book_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT COMMENT '父分类ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_parent_id (parent_id)
) COMMENT '图书分类表';

-- 图书表
CREATE TABLE IF NOT EXISTS book (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    isbn VARCHAR(20) NOT NULL COMMENT 'ISBN',
    title VARCHAR(100) NOT NULL COMMENT '书名',
    author VARCHAR(50) COMMENT '作者',
    publisher VARCHAR(100) COMMENT '出版社',
    category_id BIGINT COMMENT '分类ID',
    total_copies INT NOT NULL COMMENT '总册数',
    available_copies INT NOT NULL COMMENT '可借册数',
    location VARCHAR(50) COMMENT '馆藏位置',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_category_id (category_id),
    KEY idx_isbn (isbn)
) COMMENT '图书表';

-- 借阅记录表
CREATE TABLE IF NOT EXISTS borrow_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    borrow_time DATETIME NOT NULL COMMENT '借阅时间',
    due_time DATETIME NOT NULL COMMENT '应还时间',
    return_time DATETIME COMMENT '实际归还时间',
    status TINYINT NOT NULL COMMENT '状态：1借阅中 2已归还 3逾期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_user_id (user_id),
    KEY idx_book_id (book_id)
) COMMENT '借阅记录表';

-- 座位表
CREATE TABLE IF NOT EXISTS seat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    seat_number VARCHAR(20) NOT NULL COMMENT '座位编号',
    area VARCHAR(50) NOT NULL COMMENT '区域',
    status TINYINT NOT NULL COMMENT '状态：1可用 2已预约 3使用中',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_seat_number (seat_number)
) COMMENT '座位表';

-- 座位预约记录表
CREATE TABLE IF NOT EXISTS seat_reservation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    seat_id BIGINT NOT NULL COMMENT '座位ID',
    reservation_time DATETIME NOT NULL COMMENT '预约时间',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    status TINYINT NOT NULL COMMENT '状态：1预约中 2使用中 3已结束',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_user_id (user_id),
    KEY idx_seat_id (seat_id)
) COMMENT '座位预约记录表';

-- 插入默认管理员账号
INSERT INTO user (username, password, real_name, role) 
VALUES ('admin', '123456', '系统管理员', 'ADMIN'); 