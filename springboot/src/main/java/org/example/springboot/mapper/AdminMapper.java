package org.example.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.springboot.entity.User;
import java.util.List;

@Mapper
public interface AdminMapper {
    User getAdminByUsername(String username);
    User login(@Param("username") String username, @Param("password") String password);
    int updatePassword(@Param("username") String username, @Param("newPassword") String newPassword);
    List<User> getAllUsers();
    List<User> getUserByStudentId(String studentId);
    int insertUser(User user);
    int deleteUserByStudentId(String studentId);
    int updateUser(User user);
    int batchDeleteUsers(List<String> studentIds);
} 