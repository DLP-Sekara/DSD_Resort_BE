package com.example.ov_artifact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ov_artifact.dto.ReservationDTO;
import com.example.ov_artifact.entity.MealPlan;
import com.example.ov_artifact.entity.Room;
import com.example.ov_artifact.entity.RoomType;
import com.example.ov_artifact.repository.MealPlanRepository;
import com.example.ov_artifact.repository.RoomRepository;
import com.example.ov_artifact.services.ReservationService;

public class ReservationServiceTest {
@Mock
    private RoomRepository roomRepository;

    @Mock
    private MealPlanRepository mealPlanRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    @ExtendWith(MockitoExtension.class)
    public void testCalculateBill_Success() {
        // 1. Mock Data Setup
        ReservationDTO dto = new ReservationDTO();
        dto.setRoomId("R001");
        dto.setPlanId("P001");
        dto.setCheckIn(LocalDate.now());
        dto.setCheckOut(LocalDate.now().plusDays(2)); // 2 nights

        Room room = new Room();
        RoomType type = new RoomType();
        type.setPricePerNight(BigDecimal.valueOf(1000));
        room.setRoomType(type);

        MealPlan plan = new MealPlan();
        plan.setPrice(BigDecimal.valueOf(500));

        // 2. Defining Mock Behavior (Mocking)
        when(roomRepository.findById("R001")).thenReturn(Optional.of(room));
        when(mealPlanRepository.findById("P001")).thenReturn(Optional.of(plan));

        // 3. Execution
        BigDecimal totalBill = reservationService.calculateBill(dto);

        // 4. Verification (Expected: (1000*2) + (500*2) = 3000)
        assertEquals(BigDecimal.valueOf(3000), totalBill);
    }
}
