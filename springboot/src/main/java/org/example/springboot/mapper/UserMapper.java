package org.example.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.User;
import java.util.List;

@Mapper
public interface UserMapper {
    User findByUsername(String username);
    User findById(Long id);
    int insert(User user);
    int update(User user);
    List<User> findByRole(String role);
    int deleteById(Long id);
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