package com.example.ov_artifact.services;

import com.example.ov_artifact.dto.RoomTypeDTO;
import com.example.ov_artifact.entity.RoomType;
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
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final ModelMapper modelMapper;

    public void addRoomType(RoomTypeDTO roomTypeDTO) {
        RoomType roomType = modelMapper.map(roomTypeDTO, RoomType.class);
        roomTypeRepository.save(roomType);
    }

    public void updateRoomType(RoomTypeDTO roomTypeDTO) {
        if (roomTypeRepository.existsById(roomTypeDTO.getTypeId())) {
            RoomType roomType = modelMapper.map(roomTypeDTO, RoomType.class);
            roomTypeRepository.save(roomType);
        } else {
            throw new RuntimeException("Room Type not found for ID: " + roomTypeDTO.getTypeId());
        }
    }
 
    public void deleteRoomType(String id) {
        if (roomTypeRepository.existsById(id)) {
            roomTypeRepository.deleteById(id);
        } else {
            throw new RuntimeException("Room Type not found for ID: " + id);
        }
    }

    public List<RoomTypeDTO> getAllRoomTypes() {
        List<RoomType> roomTypes = roomTypeRepository.findAll();
        return modelMapper.map(roomTypes, new TypeToken<List<RoomTypeDTO>>() {
        }.getType());
    }
}