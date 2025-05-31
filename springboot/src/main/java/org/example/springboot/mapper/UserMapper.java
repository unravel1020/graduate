package org.example.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.User;
import java.util.List;

@Mapper
public interface UserMapper {
    User findById(Long id);
    User findByUsername(String username);
    void insert(User user);
    void update(User user);
    void delete(Long id);
    List<User> findByRole(String role);
    User getUser(String studentId);
    User login(String studentId, String password);
    User selectByStudentId(String studentId);
    User findByStudentId(String studentId);
    String getAvatarUrlByStudentId(String studentId);
    void updateAvatar(User user);
    void updateUser(User user);
    void updatePassword(String studentId, String newPassword);
    List<User> findAll();
} 