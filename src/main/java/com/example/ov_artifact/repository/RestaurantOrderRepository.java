package com.example.ov_artifact.repository;

import com.example.ov_artifact.entity.RestaurantOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, String> {
    List<RestaurantOrder> findByGuest_GuestId(String guestId);

    @Query("SELECT DISTINCT rod.restaurantOrder FROM RestaurantOrderDetail rod WHERE rod.foodItem.isKitchenPrepared = :isKitchenPrepared")
    List<RestaurantOrder> findOrdersByKitchenPrepared(@Param("isKitchenPrepared") Boolean isKitchenPrepared);
}
