package org.example.springboot.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.springboot.entity.Seat;
import org.example.springboot.entity.SeatReservation;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.SeatMapper;
import org.example.springboot.mapper.SeatReservationMapper;
import org.example.springboot.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private SeatReservationMapper reservationMapper;

    @Override
    public Seat findById(Long id) {
        Seat seat = seatMapper.findById(id);
        if (seat == null) {
            throw new BusinessException(404, "座位不存在");
        }
        return seat;
    }

    @Override
    public Seat findBySeatNumber(String seatNumber) {
        Seat seat = seatMapper.findBySeatNumber(seatNumber);
        if (seat == null) {
            throw new BusinessException(404, "座位不存在");
        }
        return seat;
    }

    @Override
    public PageInfo<Seat> findAll(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Seat> seats = seatMapper.findAll();
        return new PageInfo<>(seats);
    }

    @Override
    public PageInfo<Seat> findByStatus(Integer status, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Seat> seats = seatMapper.findByStatus(status);
        return new PageInfo<>(seats);
    }

    @Override
    public PageInfo<Seat> findByArea(String area, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Seat> seats = seatMapper.findByArea(area);
        return new PageInfo<>(seats);
    }

    @Override
    @Transactional
    public void add(Seat seat) {
        if (seatMapper.findBySeatNumber(seat.getSeatNumber()) != null) {
            throw new BusinessException(400, "座位号已存在");
        }
        seat.setStatus(1); // 1: 可用
        seat.setCreateTime(LocalDateTime.now());
        seatMapper.insert(seat);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Seat seat = seatMapper.findById(id);
        if (seat == null) {
            throw new BusinessException(404, "座位不存在");
        }
        seat.setStatus(status);
        seat.setUpdateTime(LocalDateTime.now());
        seatMapper.update(seat);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Seat seat = seatMapper.findById(id);
        if (seat == null) {
            throw new BusinessException(404, "座位不存在");
        }
        if (seat.getStatus() == 2) { // 2: 已预约
            throw new BusinessException(400, "座位已被预约，无法删除");
        }
        seatMapper.delete(id);
    }

    @Override
    @Transactional
    public void reserve(SeatReservation reservation) {
        Seat seat = seatMapper.findById(reservation.getSeatId());
        if (seat == null) {
            throw new BusinessException(404, "座位不存在");
        }
        if (seat.getStatus() != 1) { // 1: 可用
            throw new BusinessException(400, "座位不可预约");
        }

        // 检查用户是否有未结束的预约
        List<SeatReservation> reservations = reservationMapper.findByUserId(reservation.getUserId());
        if (!reservations.isEmpty() && reservations.stream().anyMatch(r -> r.getEndTime() == null)) {
            throw new BusinessException(400, "您已有未结束的预约");
        }

        // 创建预约记录
        reservation.setStartTime(LocalDateTime.now());
        reservation.setStatus(1); // 1: 预约中
        reservationMapper.insert(reservation);

        // 更新座位状态
        seat.setStatus(2); // 2: 已预约
        seat.setUpdateTime(LocalDateTime.now());
        seatMapper.update(seat);
    }

    @Override
    @Transactional
    public void cancelReservation(Long id) {
        SeatReservation reservation = reservationMapper.findById(id);
        if (reservation == null) {
            throw new BusinessException(404, "预约记录不存在");
        }
        if (reservation.getEndTime() != null) {
            throw new BusinessException(400, "预约已结束");
        }

        // 更新预约记录
        reservation.setEndTime(LocalDateTime.now());
        reservation.setStatus(2); // 2: 已取消
        reservationMapper.update(reservation);

        // 更新座位状态
        Seat seat = seatMapper.findById(reservation.getSeatId());
        seat.setStatus(1); // 1: 可用
        seat.setUpdateTime(LocalDateTime.now());
        seatMapper.update(seat);
    }

    @Override
    public List<SeatReservation> findReservationsByUserId(Long userId) {
        return reservationMapper.findByUserId(userId);
    }

    @Override
    public List<Seat> getAllSeats(String area, String date) {
        return seatMapper.findAll(area, date);
    }

    @Override
    public List<String> getSeatAreas() {
        return seatMapper.findAllAreas();
    }

    @Override
    public Map<String, Integer> getSeatStats(String date) {
        List<Seat> seats = seatMapper.findAll(null, date);
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalSeats", seats.size());
        stats.put("reservedSeats", (int) seats.stream().filter(s -> "reserved".equals(s.getStatus())).count());
        stats.put("availableSeats", (int) seats.stream().filter(s -> "available".equals(s.getStatus())).count());
        return stats;
    }

    @Override
    @Transactional
    public SeatReservation createReservation(SeatReservation reservation) {
        // 检查座位是否可用
        Seat seat = seatMapper.findById(reservation.getSeatId());
        if (seat == null || !"available".equals(seat.getStatus())) {
            throw new RuntimeException("座位不可用");
        }

        // 设置预约信息
        reservation.setReserveTime(LocalDateTime.now());
        reservation.setStatus("confirmed");
        reservation.setCreateTime(LocalDateTime.now());
        reservation.setUpdateTime(LocalDateTime.now());

        // 保存预约记录
        reservationMapper.insert(reservation);

        // 更新座位状态
        seat.setStatus("reserved");
        seatMapper.update(seat);

        return reservation;
    }

    @Override
    @Transactional
    public void cancelReservation(Long id) {
        SeatReservation reservation = reservationMapper.findById(id);
        if (reservation == null) {
            throw new RuntimeException("预约记录不存在");
        }

        // 更新预约状态
        reservation.setStatus("cancelled");
        reservation.setUpdateTime(LocalDateTime.now());
        reservationMapper.update(reservation);

        // 更新座位状态
        Seat seat = seatMapper.findById(reservation.getSeatId());
        if (seat != null) {
            seat.setStatus("available");
            seatMapper.update(seat);
        }
    }

    @Override
    public List<SeatReservation> getMyReservations() {
        // TODO: 从SecurityContext中获取当前用户ID
        Long userId = 1L;
        return reservationMapper.findByUserId(userId);
    }
} 