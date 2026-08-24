package com.example.ov_artifact.dto;

import com.example.ov_artifact.util.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {
    private String roomId;
    private String roomNumber;
    private String typeId;
    private RoomStatus status;
}
