package com.example.ov_artifact.services;

import com.example.ov_artifact.dto.RestaurantOrderDTO;
import com.example.ov_artifact.dto.RestaurantOrderDetailDTO;
import com.example.ov_artifact.entity.FoodItem;
import com.example.ov_artifact.entity.Guest;
import com.example.ov_artifact.entity.RestaurantOrder;
import com.example.ov_artifact.entity.RestaurantOrderDetail;
import com.example.ov_artifact.entity.SystemUsers;
import com.example.ov_artifact.repository.AuthRepo;
import com.example.ov_artifact.repository.FoodItemRepository;
import com.example.ov_artifact.repository.GuestRepository;
import com.example.ov_artifact.repository.RestaurantOrderDetailRepository;
import com.example.ov_artifact.repository.RestaurantOrderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RestaurantOrderService {

    private final RestaurantOrderRepository orderRepository;
    private final RestaurantOrderDetailRepository orderDetailRepository;
    private final GuestRepository guestRepository;
    private final AuthRepo authRepo;
    private final FoodItemRepository foodItemRepository;
    private final SimpMessagingTemplate messagingTemplate;


    public RestaurantOrderDTO createOrder(RestaurantOrderDTO dto) {
        Guest guest = null;
        if (dto.getGuestId() != null && !dto.getGuestId().trim().isEmpty()) {
            guest = guestRepository.findById(dto.getGuestId()).orElse(null);
        }

        SystemUsers handledBy = authRepo.findById(dto.getHandledBy())
                .orElseThrow(() -> new RuntimeException("SystemUser not found with ID: " + dto.getHandledBy()));

        RestaurantOrder order = new RestaurantOrder();
        order.setGuest(guest);
        order.setHandledBy(handledBy);
        order.setOrderTime(dto.getOrderTime() != null ? dto.getOrderTime() : LocalDateTime.now());
        order.setStatus(dto.getStatus() != null ? dto.getStatus() : "PENDING");

        BigDecimal totalAmount = BigDecimal.ZERO;
        RestaurantOrder savedOrder = orderRepository.save(order);

        if (dto.getOrderDetails() != null && !dto.getOrderDetails().isEmpty()) {
            for (RestaurantOrderDetailDTO detailDTO : dto.getOrderDetails()) {
                FoodItem item = foodItemRepository.findById(detailDTO.getItemId())
                        .orElseThrow(() -> new RuntimeException("Food Item not found with ID: " + detailDTO.getItemId()));

                RestaurantOrderDetail detail = new RestaurantOrderDetail();
                detail.setRestaurantOrder(savedOrder);
                detail.setFoodItem(item);
                detail.setOrderedQty(detailDTO.getOrderedQty());
                orderDetailRepository.save(detail);

                BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(detailDTO.getOrderedQty()));
                totalAmount = totalAmount.add(itemTotal);
            }
        }

        savedOrder.setTotalAmount(totalAmount);
        orderRepository.save(savedOrder);
        messagingTemplate.convertAndSend("/topic/orders", savedOrder);
        return getOrderById(savedOrder.getOrderId());
    }

    public RestaurantOrderDTO getOrderById(String orderId) {
        RestaurantOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Restaurant Order not found with ID: " + orderId));

        RestaurantOrderDTO dto = new RestaurantOrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setGuestId(order.getGuest() != null ? order.getGuest().getGuestId() : null);
        dto.setHandledBy(order.getHandledBy() != null ? order.getHandledBy().getUserId() : null);
        dto.setOrderTime(order.getOrderTime());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());

        List<RestaurantOrderDetail> details = orderDetailRepository.findByRestaurantOrder_OrderId(orderId);
        List<RestaurantOrderDetailDTO> detailDTOs = new ArrayList<>();
        if (details != null) {
            for (RestaurantOrderDetail d : details) {
                RestaurantOrderDetailDTO dDto = new RestaurantOrderDetailDTO();
                dDto.setOrderId(order.getOrderId());
                dDto.setItemId(d.getFoodItem() != null ? d.getFoodItem().getItemId() : null);
                dDto.setOrderedQty(d.getOrderedQty());
                detailDTOs.add(dDto);
            }
        }
        dto.setOrderDetails(detailDTOs);

        return dto;
    }

    public List<RestaurantOrderDTO> getAllOrders(Boolean isKitchenPrepared) {
        List<RestaurantOrder> orders;
        if (isKitchenPrepared != null) {
            orders = orderRepository.findOrdersByKitchenPrepared(isKitchenPrepared);
        } else {
            orders = orderRepository.findAll();
        }
        List<RestaurantOrderDTO> dtos = new ArrayList<>();
        for (RestaurantOrder o : orders) {
            dtos.add(getOrderById(o.getOrderId()));
        }
        return dtos;
    }

    public List<RestaurantOrderDTO> getAllOrders() {
        return getAllOrders(null);
    }

    public void updateOrderStatus(String orderId, String status) {
        RestaurantOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Restaurant Order not found with ID: " + orderId));
        order.setStatus(status);
        orderRepository.save(order);
    }

    public void deleteOrder(String orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new RuntimeException("Restaurant Order not found with ID: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }
}
