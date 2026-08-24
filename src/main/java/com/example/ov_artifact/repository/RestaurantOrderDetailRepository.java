package com.example.ov_artifact.repository;

import com.example.ov_artifact.entity.RestaurantOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantOrderDetailRepository extends JpaRepository<RestaurantOrderDetail, String> {
    List<RestaurantOrderDetail> findByRestaurantOrder_OrderId(String orderId);
}
