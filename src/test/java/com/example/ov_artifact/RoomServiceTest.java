package com.example.ov_artifact;

import com.example.ov_artifact.dto.RoomDTO;
import com.example.ov_artifact.entity.Room;
import com.example.ov_artifact.entity.RoomType;
import com.example.ov_artifact.repository.RoomRepository;
import com.example.ov_artifact.repository.RoomTypeRepository;
import com.example.ov_artifact.services.RoomService;
import com.example.ov_artifact.util.RoomStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomTypeRepository roomTypeRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoomService roomService;

    private RoomDTO roomDTO;
    private Room room;
    private RoomType roomType;

    @BeforeEach
    void setUp() {
        roomType = new RoomType();
        roomType.setTypeId("type-123");
        roomType.setTypeName("Deluxe");

        roomDTO = new RoomDTO();
        roomDTO.setRoomId("room-uuid");
        roomDTO.setRoomNumber("101");
        roomDTO.setTypeId("type-123");
        roomDTO.setStatus(RoomStatus.AVAILABLE);

        room = new Room();
        room.setRoomId("room-uuid");
        room.setRoomNumber("101");
        room.setRoomType(roomType);
        room.setStatus(RoomStatus.AVAILABLE);
    }

    @Test
    void testAddRoom_Success() {
        
        when(roomRepository.existsByRoomNumber(roomDTO.getRoomNumber())).thenReturn(false);
        when(roomTypeRepository.findById(roomDTO.getTypeId())).thenReturn(Optional.of(roomType));
        when(modelMapper.map(any(RoomDTO.class), eq(Room.class))).thenReturn(room);

        assertDoesNotThrow(() -> roomService.addRoom(roomDTO));

        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void testAddRoom_DuplicateRoomNumber() {
        
        when(roomRepository.existsByRoomNumber(roomDTO.getRoomNumber())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> roomService.addRoom(roomDTO));
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void testDeleteRoom_Success() {
        when(roomRepository.existsById("room-uuid")).thenReturn(true);

        assertDoesNotThrow(() -> roomService.deleteRoom("room-uuid"));

        verify(roomRepository, times(1)).deleteById("room-uuid");
    }

    @Test
    void testDeleteRoom_NotFound() {
        when(roomRepository.existsById("invalid-id")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> roomService.deleteRoom("invalid-id"));
    }

    @Test
    void testGetRoomById_Success() {
        when(roomRepository.findById("room-uuid")).thenReturn(Optional.of(room));
        when(modelMapper.map(any(Room.class), eq(RoomDTO.class))).thenReturn(roomDTO);

        RoomDTO result = roomService.getRoomById("room-uuid");

        assertNotNull(result);
        assertEquals("101", result.getRoomNumber());
    }
}