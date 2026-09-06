package com.example.ov_artifact.services;

import com.example.ov_artifact.dto.RoomDTO;
import com.example.ov_artifact.entity.Room;
import com.example.ov_artifact.entity.RoomType;
import com.example.ov_artifact.repository.RoomRepository;
import com.example.ov_artifact.repository.RoomTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final ModelMapper modelMapper;

    public void addRoom(RoomDTO roomDTO) {
        if (roomRepository.existsByRoomNumber(roomDTO.getRoomNumber())) {
            throw new IllegalArgumentException("Room number '" + roomDTO.getRoomNumber() + "' already exists!");
        }
        RoomType roomType = roomTypeRepository.findById(roomDTO.getTypeId())
                .orElseThrow(() -> new RuntimeException("Room Type not found for ID: " + roomDTO.getTypeId()));

        Room room = modelMapper.map(roomDTO, Room.class);
        room.setRoomType(roomType);
        roomRepository.save(room);
    }

    public void updateRoom(RoomDTO roomDTO) {
        if (roomRepository.existsById(roomDTO.getRoomId())) {
            RoomType roomType = roomTypeRepository.findById(roomDTO.getTypeId())
                    .orElseThrow(() -> new RuntimeException("Room Type not found for ID: " + roomDTO.getTypeId()));

            Room room = modelMapper.map(roomDTO, Room.class);
            room.setRoomType(roomType);
            roomRepository.save(room);
        } else {
            throw new RuntimeException("Room not found for ID: " + roomDTO.getRoomId());
        }
    }

    public void deleteRoom(String id) {
        if (roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
        } else {
            throw new RuntimeException("Room not found for ID: " + id);
        }
    }

    public List<RoomDTO> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        return modelMapper.map(rooms, new TypeToken<List<RoomDTO>>() {
        }.getType());
    }

    public RoomDTO getRoomById(String id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found for ID: " + id));
        return modelMapper.map(room, RoomDTO.class);
    }
}
