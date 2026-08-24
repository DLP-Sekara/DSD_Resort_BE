package com.example.ov_artifact.dto;

import com.example.ov_artifact.util.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {
    private String resId;
    private String guestId;
    private String roomId;
    private String planId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private BigDecimal totalBill;
    private ReservationStatus status;
    private Integer guestCount;
    private List<ReservationDetailDTO> reservationDetails;

}
