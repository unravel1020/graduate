USE library_management;

-- 管理员数据
INSERT INTO admin (account, password, phone, role) VALUES
    ('admin001', '123456', '13800001111', '超级管理员'),
    ('manager002', '654321', '13900002222', '图书管理员');

-- 学生用户数据
INSERT INTO user (student_id, name, gender, college, major, phone, password) VALUES
    ('S1001', '张三', '男', '计算机学院', '软件工程', '13812345678', '123456'),
    ('S1002', '李四', '女', '经济管理学院', '金融学', '13987654321', '123456'),
    ('S1003', '王五', '男', '外国语学院', '英语', '13711112222', '123456');

-- 图书数据
INSERT INTO book (title, author, isbn, publisher, publish_date, category, location, status, total_copies, available_copies) VALUES
    ('Java编程思想', 'Bruce Eckel', '9787111213826', '机械工业出版社', '2007-06-01', '计算机', 'A区-1层-01', 'AVAILABLE', 5, 5),
    ('算法导论', 'Thomas H.Cormen', '9787111187776', '机械工业出版社', '2009-09-01', '计算机', 'A区-1层-02', 'AVAILABLE', 3, 3),
    ('深入理解计算机系统', 'Randal E.Bryant', '9787111321330', '机械工业出版社', '2011-01-01', '计算机', 'A区-1层-03', 'AVAILABLE', 2, 2);

-- 座位数据
INSERT INTO seat (seat_number, status, location) VALUES
    ('A-101', 'AVAILABLE', 'A区-1层'),
    ('A-102', 'AVAILABLE', 'A区-1层'),
    ('A-103', 'AVAILABLE', 'A区-1层'),
    ('B-201', 'AVAILABLE', 'B区-2层'),
    ('B-202', 'AVAILABLE', 'B区-2层'),
    ('B-203', 'AVAILABLE', 'B区-2层');