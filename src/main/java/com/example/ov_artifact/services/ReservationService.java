package com.example.ov_artifact.services;

import com.example.ov_artifact.dto.ReservationDTO;
import com.example.ov_artifact.dto.ReservationDetailDTO;
import com.example.ov_artifact.dto.RoomDTO;
import com.example.ov_artifact.entity.FoodItem;
import com.example.ov_artifact.entity.Guest;
import com.example.ov_artifact.entity.MealPlan;
import com.example.ov_artifact.entity.Reservation;
import com.example.ov_artifact.entity.ReservationDetail;
import com.example.ov_artifact.entity.Room;
import com.example.ov_artifact.repository.FoodItemRepository;
import com.example.ov_artifact.repository.GuestRepository;
import com.example.ov_artifact.repository.MealPlanRepository;
import com.example.ov_artifact.repository.ReservationRepository;
import com.example.ov_artifact.repository.RoomRepository;
import com.example.ov_artifact.util.ReservationStatus;
import com.example.ov_artifact.util.RoomStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;
    private final MealPlanRepository mealPlanRepository;
    private final FoodItemRepository foodItemRepository;
    private final ModelMapper modelMapper;

    public List<RoomDTO> getAvailableRoomsForBooking(String typeId, LocalDate checkIn, LocalDate checkOut) {

        List<Room> availableRooms = roomRepository.findAvailableRooms(typeId, checkIn, checkOut);
        return modelMapper.map(availableRooms, new TypeToken<List<RoomDTO>>() {
        }.getType());
    }

    public BigDecimal calculateBill(ReservationDTO reservationDTO) {
        Room room = roomRepository.findById(reservationDTO.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));
        MealPlan mealPlan = mealPlanRepository.findById(reservationDTO.getPlanId())
                .orElseThrow(() -> new RuntimeException("Meal Plan not found"));

        long nights = ChronoUnit.DAYS.between(reservationDTO.getCheckIn(), reservationDTO.getCheckOut());
        if (nights <= 0)
            nights = 1; // Minimum 1 night charge

        BigDecimal roomTotal = room.getRoomType().getPricePerNight().multiply(BigDecimal.valueOf(nights));
        BigDecimal mealTotal = mealPlan.getPrice().multiply(BigDecimal.valueOf(nights));

        BigDecimal foodTotal = BigDecimal.ZERO;
        if (reservationDTO.getReservationDetails() != null) {
            for (ReservationDetailDTO detailDTO : reservationDTO.getReservationDetails()) {
                FoodItem item = foodItemRepository.findById(detailDTO.getItemId())
                        .orElseThrow(() -> new RuntimeException("Food Item not found"));
                foodTotal = foodTotal.add(item.getUnitPrice().multiply(BigDecimal.valueOf(detailDTO.getOrderedQty())));
            }
        }

        return roomTotal.add(mealTotal).add(foodTotal);
    }

    @Transactional
    public String createReservation(ReservationDTO reservationDTO) {
        // 1. Availability Check
        if (!isRoomAvailable(reservationDTO.getRoomId(), reservationDTO.getCheckIn(), reservationDTO.getCheckOut())) {
            throw new RuntimeException("Room is not available");
        }

        // 2. Guest, Room, Meal Plan Load
        Guest guest = guestRepository.findById(reservationDTO.getGuestId())
                .orElseThrow(() -> new RuntimeException("Guest not found"));
        Room room = roomRepository.findById(reservationDTO.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));
        MealPlan mealPlan = mealPlanRepository.findById(reservationDTO.getPlanId())
                .orElseThrow(() -> new RuntimeException("Meal Plan not found"));

        // 2. Guest count check
        int maxAllowed = room.getRoomType().getMaxOccupancy();
        if (reservationDTO.getGuestCount() > maxAllowed) {
            throw new RuntimeException("The room has a maximum occupancy of " + maxAllowed + " only. " +
                    "You have requested " + reservationDTO.getGuestCount());
        }

        // 3. Reservation Create
        Reservation reservation = new Reservation();
        reservation.setGuest(guest);
        reservation.setRoom(room);
        reservation.setMealPlan(mealPlan);
        reservation.setCheckIn(reservationDTO.getCheckIn());
        reservation.setCheckOut(reservationDTO.getCheckOut());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setGuestCount(reservationDTO.getGuestCount());

        // 4. Food Items Load and Calculate Total
        BigDecimal foodTotal = BigDecimal.ZERO;
        List<ReservationDetail> details = new ArrayList<>();

        if (reservationDTO.getReservationDetails() != null && !reservationDTO.getReservationDetails().isEmpty()) {
            for (ReservationDetailDTO d : reservationDTO.getReservationDetails()) {
                FoodItem item = foodItemRepository.findById(d.getItemId())
                        .orElseThrow(() -> new RuntimeException("Food Item not found: " + d.getItemId()));

                ReservationDetail detail = new ReservationDetail();
                detail.setReservation(reservation);
                detail.setFoodItem(item);
                detail.setOrderedQty(d.getOrderedQty());
                details.add(detail);

                // Food item total calculate
                BigDecimal itemTotal = item.getUnitPrice().multiply(new BigDecimal(d.getOrderedQty()));
                foodTotal = foodTotal.add(itemTotal);
            }
        }
        reservation.setReservationDetails(details);

        // 5. Calculate Total (Room + Meal + Food)
        BigDecimal finalBill = calculateTotalAmount(reservation, foodTotal);
        reservation.setTotalBill(finalBill);

        // 6. Save Reservation
        Reservation savedReservation = reservationRepository.save(reservation);

        // 7. Room Status Update to OCCUPIED
        room.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);

        return savedReservation.getResId();
    }

    @Transactional
    public void checkOutGuest(String resId) {
        // 1. FInd Reservation
        Reservation reservation = reservationRepository.findById(resId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + resId));

        // 2. check weather reservation status is PENDING
        if (!reservation.getStatus().equals(ReservationStatus.PENDING)) {
            throw new RuntimeException("Only Pending reservations can be checked out.");
        }

        // 3. Reservation Status Update to COMPLETED
        reservation.setStatus(ReservationStatus.COMPLETED);

        // 4. Room Status Update to MAINTENANCE
        Room room = reservation.getRoom();
        room.setStatus(RoomStatus.CLEANING);

        reservationRepository.save(reservation);
        roomRepository.save(room);
    }

    private BigDecimal calculateTotalAmount(Reservation res, BigDecimal foodTotal) {
        // 1. Nights Calculate
        long days = ChronoUnit.DAYS.between(res.getCheckIn(), res.getCheckOut());

        // Check if days is less than 1
        if (days <= 0) {
            days = 1;
        }

        BigDecimal totalNights = new BigDecimal(days);
        BigDecimal guestCount = new BigDecimal(res.getGuestCount());

        // 2. Room Charge = (Room Type Price * Nights)
        BigDecimal roomCharge = res.getRoom().getRoomType().getPricePerNight().multiply(totalNights);

        // 3. Meal Charge = (Meal Plan Price * Guest Count * Nights)
        BigDecimal mealCharge = res.getMealPlan().getPrice()
                .multiply(guestCount)
                .multiply(totalNights);

        // 4. Final Bill = Room Charge + Meal Charge + Additional Food Total
        return roomCharge.add(mealCharge).add(foodTotal);
    }

    public Page<ReservationDTO> getAllReservations(int page, int size, ReservationStatus status, String guestName,
            LocalDate start, LocalDate end) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("checkIn").descending());

        String searchName = (guestName != null && !guestName.isEmpty()) ? guestName : null;

        Page<Reservation> resultPage = reservationRepository.findAllWithFilters(status, searchName, start, end,
                pageable);

        return resultPage.map(res -> modelMapper.map(res, ReservationDTO.class));
    }

    public ReservationDTO getReservationById(String id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        return modelMapper.map(reservation, ReservationDTO.class);
    }

    @Transactional
    public void deleteReservation(String id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        Room room = reservation.getRoom();
        room.setStatus(RoomStatus.MAINTENANCE);
        roomRepository.save(room);

        reservationRepository.delete(reservation);
    }

    public boolean isRoomAvailable(String roomId, LocalDate checkIn, LocalDate checkOut) {

        if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }

        boolean isReserved = reservationRepository.existsOverlappingReservation(roomId, checkIn, checkOut);

        return !isReserved;
    }

    public Map<String, BigDecimal> getIncomeStats(int year, int month) {

        Map<String, BigDecimal> stats = new HashMap<>();

        BigDecimal totalAllTime = reservationRepository.calculateTotalAllTimeIncome();
        BigDecimal monthlyIncome = reservationRepository.calculateMonthlyIncome(year, month);

        // Null safety check
        stats.put("totalAllTimeIncome", (totalAllTime != null) ? totalAllTime : BigDecimal.ZERO);
        stats.put("monthlyIncome", (monthlyIncome != null) ? monthlyIncome : BigDecimal.ZERO);

        return stats;
    }
}
