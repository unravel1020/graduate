package org.example.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.springboot.entity.BorrowRecord;
import java.util.List;

@Mapper
public interface BorrowRecordMapper {
    int insert(BorrowRecord record);
    int update(BorrowRecord record);
    BorrowRecord findById(Long id);
    List<BorrowRecord> findByUserId(Long userId);
    List<BorrowRecord> findByBookId(Long bookId);
    List<BorrowRecord> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);
    List<BorrowRecord> findByUserIdAndBookId(Long userId, Long bookId);
    List<BorrowRecord> findAll();
    List<BorrowRecord> findOverdue();
} 