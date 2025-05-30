package org.example.springboot.controller;

import com.github.pagehelper.PageInfo;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Seat;
import org.example.springboot.entity.SeatReservation;
import org.example.springboot.service.SeatService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/{id}")
    public Result<Seat> findById(@PathVariable Long id) {
        return Result.success(seatService.findById(id));
    }

    @GetMapping("/number/{seatNumber}")
    public Result<Seat> findBySeatNumber(@PathVariable String seatNumber) {
        return Result.success(seatService.findBySeatNumber(seatNumber));
    }

    @GetMapping
    public Result<PageInfo<Seat>> findAll(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(seatService.findAll(pageNum, pageSize));
    }

    @GetMapping("/status/{status}")
    public Result<PageInfo<Seat>> findByStatus(
            @PathVariable Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(seatService.findByStatus(status, pageNum, pageSize));
    }

    @GetMapping("/area/{area}")
    public Result<PageInfo<Seat>> findByArea(
            @PathVariable String area,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(seatService.findByArea(area, pageNum, pageSize));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> add(@RequestBody Seat seat) {
        seatService.add(seat);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        seatService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        seatService.delete(id);
        return Result.success();
    }

    @PostMapping("/reserve")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<Void> reserve(@RequestBody SeatReservation reservation) {
        // 从SecurityContext中获取当前用户ID
        Long userId = 1L; // TODO: 从SecurityContext中获取
        reservation.setUserId(userId);
        seatService.reserve(reservation);
        return Result.success();
    }

    @PostMapping("/reservations/{id}/cancel")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<Void> cancelReservation(@PathVariable Long id) {
        seatService.cancelReservation(id);
        return Result.success();
    }

    @GetMapping("/reservations")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<List<SeatReservation>> findReservations() {
        // 从SecurityContext中获取当前用户ID
        Long userId = 1L; // TODO: 从SecurityContext中获取
        return Result.success(seatService.findReservationsByUserId(userId));
    }
} 