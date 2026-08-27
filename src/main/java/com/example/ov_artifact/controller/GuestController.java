package com.example.ov_artifact.controller;

import com.example.ov_artifact.dto.GuestDTO;
import com.example.ov_artifact.services.GuestService;
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
@RequestMapping("/api/v1/guests")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
public class GuestController {

    private final GuestService guestService;

    @PostMapping("/add")
    public ResponseEntity<StandardResponse> addGuest(@RequestBody GuestDTO guestDTO) {
        GuestDTO savedGuest = guestService.addGuest(guestDTO);
        return new ResponseEntity<>(
                new StandardResponse(true, 201, "Guest Added Successfully",
                        savedGuest),
                HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<StandardResponse> updateGuest(@RequestBody GuestDTO guestDTO) {
        guestService.updateGuest(guestDTO);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Guest Updated Successfully", null),
                HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<StandardResponse> deleteGuest(@PathVariable String id) {
        guestService.deleteGuest(id);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Guest Deleted Successfully", null),
                HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<StandardResponse> getAllGuests() {
        List<GuestDTO> guests = guestService.getAllGuests();
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Guests Fetched Successfully", guests),
                HttpStatus.OK);
    }

    @GetMapping("/find-by-nic/{nic}")
    public ResponseEntity<StandardResponse> findGuestByNic(@PathVariable String nic) {
        GuestDTO guest = guestService.findGuestByNic(nic);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Guest Fetched Successfully", guest),
                HttpStatus.OK);
    }
}
