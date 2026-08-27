package com.example.ov_artifact.controller;

import com.example.ov_artifact.dto.RoomDTO;
import com.example.ov_artifact.services.RoomService;
import com.example.ov_artifact.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<StandardResponse> addRoom(@RequestBody RoomDTO roomDTO) {
        roomService.addRoom(roomDTO);
        return new ResponseEntity<>(
                new StandardResponse(true, 201, "Room Added Successfully", null),
                HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update")
    public ResponseEntity<StandardResponse> updateRoom(@RequestBody RoomDTO roomDTO) {
        roomService.updateRoom(roomDTO);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Room Updated Successfully", null),
                HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<StandardResponse> deleteRoom(@PathVariable String id) {
        roomService.deleteRoom(id);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Room Deleted Successfully", null),
                HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @GetMapping("/all")
    public ResponseEntity<StandardResponse> getAllRooms() {
        List<RoomDTO> rooms = roomService.getAllRooms();
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Rooms Fetched Successfully", rooms),
                HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<StandardResponse> getRoomById(@PathVariable String id) {
        RoomDTO roomDTO = roomService.getRoomById(id);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Room Fetched Successfully", roomDTO),
                HttpStatus.OK);
    }
}
