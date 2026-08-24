package com.example.ov_artifact.repository;

import com.example.ov_artifact.entity.RestaurantOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, String> {
    List<RestaurantOrder> findByGuest_GuestId(String guestId);
}
