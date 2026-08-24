package com.example.ov_artifact.services;

import com.example.ov_artifact.dto.GuestDTO;
import com.example.ov_artifact.entity.Guest;
import com.example.ov_artifact.repository.GuestRepository;
import com.example.ov_artifact.repository.ReservationRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;
    private final ReservationRepository reservationRepository;

    public GuestDTO addGuest(GuestDTO guestDTO) {
        if (guestRepository.existsByNic(guestDTO.getNic())) {
            throw new IllegalArgumentException("Room number '" + guestDTO.getName() + "' already exists!");
        }

        Guest guest = modelMapper.map(guestDTO, Guest.class);
        Guest savedGuest =guestRepository.save(guest);
        return modelMapper.map(savedGuest, GuestDTO.class);
    }

    public void updateGuest(GuestDTO guestDTO) {
        if (guestRepository.existsById(guestDTO.getGuestId())) {
            Guest guest = modelMapper.map(guestDTO, Guest.class);
            guestRepository.save(guest);
        } else {
            throw new RuntimeException("Guest not found for ID: " + guestDTO.getGuestId());
        }
    }

    public void deleteGuest(String id) {
        if (!guestRepository.existsById(id)) {
            throw new RuntimeException("Guest not found for ID: " + id);
        }
 
        if (reservationRepository.existsByGuest_GuestId(id)) {
            throw new RuntimeException("Cannot delete Guest: This guest has active or past reservations.");
        }

        guestRepository.deleteById(id);
    }

    public List<GuestDTO> getAllGuests() {
        List<Guest> guests = guestRepository.findAll();
        return modelMapper.map(guests, new TypeToken<List<GuestDTO>>() {
        }.getType());
    }

    public GuestDTO findGuestByNic(String nic) {
        Guest guest = guestRepository.findByNic(nic)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found with NIC: " + nic));

        return modelMapper.map(guest, GuestDTO.class);
    }
}
