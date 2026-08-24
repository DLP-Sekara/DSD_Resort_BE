package com.example.ov_artifact.controller;

import com.example.ov_artifact.dto.ReservationDTO;
import com.example.ov_artifact.dto.RoomDTO;
import com.example.ov_artifact.services.ReservationService;
import com.example.ov_artifact.util.ReservationStatus;
import com.example.ov_artifact.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/create")
    public ResponseEntity<StandardResponse> createReservation(@RequestBody ReservationDTO reservationDTO) {
        String resId = reservationService.createReservation(reservationDTO);
        return new ResponseEntity<>(
                new StandardResponse(true, 201, "Reservation Created Successfully", resId),
                HttpStatus.CREATED);
    }

    @GetMapping("/available-rooms")
    public ResponseEntity<StandardResponse> getAvailableRooms(
            @RequestParam String typeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {

        List<RoomDTO> availableRooms = reservationService.getAvailableRoomsForBooking(typeId, checkIn, checkOut);

        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Available Rooms Fetched", availableRooms),
                HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<StandardResponse> getAllReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) String guestName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Page<ReservationDTO> reservations = reservationService.getAllReservations(
                page, size, status, guestName, startDate, endDate);

        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Fetched Successfully", reservations),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getReservationById(@PathVariable String id) {
        ReservationDTO reservationDTO = reservationService.getReservationById(id);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Reservation Fetched Successfully", reservationDTO),
                HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<StandardResponse> deleteReservation(@PathVariable String id) {
        reservationService.deleteReservation(id);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Reservation Deleted Successfully", null),
                HttpStatus.OK);
    }

    @PutMapping("/checkout/{resId}")
    public ResponseEntity<StandardResponse> checkOutGuest(@PathVariable String resId) {
        reservationService.checkOutGuest(resId);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Guest Checked Out Successfully. Room moved to Maintenance.", null),
                HttpStatus.OK);
    }

    @GetMapping("/income-stats")
    public ResponseEntity<StandardResponse> getIncomeStats(
            @RequestParam int year,
            @RequestParam int month) {

        Map<String, BigDecimal> stats = reservationService.getIncomeStats(year, month);

        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Income Stats Fetched", stats),
                HttpStatus.OK);
    }
}
