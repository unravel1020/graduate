package org.example.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.springboot.entity.Seat;
import java.util.List;

@Mapper
public interface SeatMapper {
    List<Seat> findAll(@Param("area") String area, @Param("date") String date);
    Seat findById(Long id);
    Seat findBySeatNumber(String seatNumber);
    List<Seat> findAll();
    List<Seat> findByStatus(Integer status);
    List<Seat> findByArea(String area);
    void insert(Seat seat);
    void update(Seat seat);
    void delete(Long id);
    List<Seat> findAvailable();
    List<String> findAllAreas();
} 