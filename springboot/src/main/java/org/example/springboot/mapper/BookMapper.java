package org.example.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.Book;
import java.util.List;

@Mapper
public interface BookMapper {
    Book findById(Long id);
    Book findByIsbn(String isbn);
    List<Book> findAll();
    List<Book> search(String keyword);
    void insert(Book book);
    void update(Book book);
    void delete(Long id);
    int decreaseAvailableCopies(Long id);
    int increaseAvailableCopies(Long id);
} 