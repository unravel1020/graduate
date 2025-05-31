package org.example.springboot.service;

import com.github.pagehelper.PageInfo;
import org.example.springboot.entity.Seat;
import org.example.springboot.entity.SeatReservation;

import java.util.List;
import java.util.Map;

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
    List<Seat> getAllSeats(String area, String date);
    Seat getSeatById(Long id);
    List<String> getSeatAreas();
    Map<String, Integer> getSeatStats(String date);
    SeatReservation createReservation(SeatReservation reservation);
    List<SeatReservation> getMyReservations();
} 