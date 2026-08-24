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
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
    private final ModelMapper modelMapper;

    public RestaurantOrderDTO createOrder(RestaurantOrderDTO dto) {
        Guest guest = guestRepository.findById(dto.getGuestId())
                .orElseThrow(() -> new RuntimeException("Guest not found with ID: " + dto.getGuestId()));

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

        return getOrderById(savedOrder.getOrderId());
    }

    public RestaurantOrderDTO getOrderById(String orderId) {
        RestaurantOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Restaurant Order not found with ID: " + orderId));

        RestaurantOrderDTO dto = modelMapper.map(order, RestaurantOrderDTO.class);
        List<RestaurantOrderDetail> details = orderDetailRepository.findByRestaurantOrder_OrderId(orderId);
        dto.setOrderDetails(modelMapper.map(details, new TypeToken<List<RestaurantOrderDetailDTO>>() {}.getType()));

        return dto;
    }

    public List<RestaurantOrderDTO> getAllOrders() {
        List<RestaurantOrder> orders = orderRepository.findAll();
        List<RestaurantOrderDTO> dtos = new ArrayList<>();
        for (RestaurantOrder o : orders) {
            dtos.add(getOrderById(o.getOrderId()));
        }
        return dtos;
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
