package com.example.ov_artifact.controller;

import com.example.ov_artifact.dto.RestaurantOrderDTO;
import com.example.ov_artifact.services.RestaurantOrderService;
import com.example.ov_artifact.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/restaurant-orders")
@RequiredArgsConstructor
public class RestaurantOrderController {

    private final RestaurantOrderService restaurantOrderService;

    @PostMapping("/create")
    public ResponseEntity<StandardResponse> createOrder(@RequestBody RestaurantOrderDTO dto) {
        RestaurantOrderDTO createdOrder = restaurantOrderService.createOrder(dto);
        return new ResponseEntity<>(
                new StandardResponse(true, 201, "Restaurant Order Created Successfully", createdOrder),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getOrderById(@PathVariable String id) {
        RestaurantOrderDTO order = restaurantOrderService.getOrderById(id);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Restaurant Order Fetched Successfully", order),
                HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<StandardResponse> getAllOrders(
            @RequestParam(required = false) Boolean isKitchenPrepared,
            @RequestParam(value = "is_kitchen_prepared", required = false) Boolean isKitchenPreparedSnake) {
        Boolean filterParam = isKitchenPrepared != null ? isKitchenPrepared : isKitchenPreparedSnake;
        List<RestaurantOrderDTO> orders = restaurantOrderService.getAllOrders(filterParam);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Restaurant Orders Fetched Successfully", orders),
                HttpStatus.OK);
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<StandardResponse> updateOrderStatus(@PathVariable String id, @RequestParam String status) {
        restaurantOrderService.updateOrderStatus(id, status);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Order Status Updated Successfully", null),
                HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<StandardResponse> deleteOrder(@PathVariable String id) {
        restaurantOrderService.deleteOrder(id);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Restaurant Order Deleted Successfully", null),
                HttpStatus.OK);
    }
}
