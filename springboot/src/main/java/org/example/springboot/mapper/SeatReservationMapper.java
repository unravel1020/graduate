package org.example.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.springboot.entity.SeatReservation;
import java.util.List;

@Mapper
public interface SeatReservationMapper {
    void insert(SeatReservation reservation);
    void update(SeatReservation reservation);
    SeatReservation findById(Long id);
    List<SeatReservation> findByUserId(@Param("userId") Long userId);
    List<SeatReservation> findBySeatId(Long seatId);
    List<SeatReservation> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);
    List<SeatReservation> findAll();
    void deleteById(Long id);
    List<SeatReservation> findActive();
} 