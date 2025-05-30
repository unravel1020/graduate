package org.example.springboot.service;

import com.github.pagehelper.PageInfo;
import org.example.springboot.entity.Seat;
import org.example.springboot.entity.SeatReservation;

import java.util.List;

public interface SeatService {
    Seat findById(Long id);
    Seat findBySeatNumber(String seatNumber);
    PageInfo<Seat> findAll(int pageNum, int pageSize);
    PageInfo<Seat> findByStatus(Integer status, int pageNum, int pageSize);
    PageInfo<Seat> findByArea(String area, int pageNum, int pageSize);
    void add(Seat seat);
    void updateStatus(Long id, Integer status);
    void delete(Long id);
    void reserve(SeatReservation reservation);
    void cancelReservation(Long id);
    List<SeatReservation> findReservationsByUserId(Long userId);
} 