package org.example.springboot.controller;

import com.github.pagehelper.PageInfo;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Seat;
import org.example.springboot.entity.SeatReservation;
import org.example.springboot.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping
    public ResponseEntity<List<Seat>> getAllSeats(
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String date) {
        return ResponseEntity.ok(seatService.getAllSeats(area, date));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seat> getSeatById(@PathVariable Long id) {
        return ResponseEntity.ok(seatService.getSeatById(id));
    }

    @GetMapping("/areas")
    public ResponseEntity<List<String>> getSeatAreas() {
        return ResponseEntity.ok(seatService.getSeatAreas());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Integer>> getSeatStats(
            @RequestParam(required = false) String date) {
        return ResponseEntity.ok(seatService.getSeatStats(date));
    }

    @PostMapping("/reservations")
    public ResponseEntity<SeatReservation> createReservation(@RequestBody SeatReservation reservation) {
        return ResponseEntity.ok(seatService.createReservation(reservation));
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        seatService.cancelReservation(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reservations/my")
    public ResponseEntity<List<SeatReservation>> getMyReservations() {
        return ResponseEntity.ok(seatService.getMyReservations());
    }
} 