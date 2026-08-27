package com.example.ov_artifact.services;

import com.example.ov_artifact.dto.BOMTemplateDTO;
import com.example.ov_artifact.dto.BOMTemplateItemDTO;
import com.example.ov_artifact.dto.OrderBOMCalculationRequestDTO;
import com.example.ov_artifact.dto.OrderBOMCalculationResultDTO;
import com.example.ov_artifact.dto.OrderItemDetailDTO;
import com.example.ov_artifact.dto.RawMaterialCalculationDetailDTO;
import com.example.ov_artifact.entity.BOMTemplate;
import com.example.ov_artifact.entity.BOMTemplateItem;
import com.example.ov_artifact.entity.FoodItem;
import com.example.ov_artifact.entity.RawMaterial;
import com.example.ov_artifact.entity.RestaurantOrderDetail;
import com.example.ov_artifact.entity.SystemUsers;
import com.example.ov_artifact.repository.AuthRepo;
import com.example.ov_artifact.repository.BOMTemplateItemRepository;
import com.example.ov_artifact.repository.BOMTemplateRepository;
import com.example.ov_artifact.repository.FoodItemRepository;
import com.example.ov_artifact.repository.RawMaterialRepository;
import com.example.ov_artifact.repository.RestaurantOrderDetailRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BOMTemplateService {

    private final BOMTemplateRepository bomTemplateRepository;
    private final BOMTemplateItemRepository bomTemplateItemRepository;
    private final AuthRepo authRepo;
    private final RawMaterialRepository rawMaterialRepository;
    private final FoodItemRepository foodItemRepository;
    private final RestaurantOrderDetailRepository restaurantOrderDetailRepository;

    public BOMTemplateDTO createTemplate(BOMTemplateDTO dto) {
        if (dto.getItemId() == null || dto.getItemId().trim().isEmpty()) {
            throw new IllegalArgumentException("Food Item ID (item_id) is required for BOM Template.");
        }

        if (dto.getCreatedBy() == null || dto.getCreatedBy().trim().isEmpty()) {
            throw new IllegalArgumentException("Created By (User ID) is required.");
        }

        FoodItem foodItem = foodItemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new RuntimeException("Food Item not found with ID: " + dto.getItemId()));

        if (bomTemplateRepository.existsByFoodItem_ItemId(dto.getItemId())) {
            throw new IllegalArgumentException("A BOM Template already exists for Food Item: " + foodItem.getName() + " (ID: " + dto.getItemId() + ")");
        }

        SystemUsers user = authRepo.findById(dto.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("SystemUser not found with ID: " + dto.getCreatedBy()));

        BOMTemplate template = new BOMTemplate();
        template.setTemplateName(dto.getTemplateName());
        template.setCreatedBy(user);
        template.setFoodItem(foodItem);

        BOMTemplate savedTemplate = bomTemplateRepository.save(template);

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (BOMTemplateItemDTO itemDTO : dto.getItems()) {
                RawMaterial material = rawMaterialRepository.findById(itemDTO.getMaterialId())
                        .orElseThrow(() -> new RuntimeException("Raw Material not found with ID: " + itemDTO.getMaterialId()));

                BOMTemplateItem item = new BOMTemplateItem();
                item.setBomTemplate(savedTemplate);
                item.setRawMaterial(material);
                item.setQtyPerPerson(itemDTO.getQtyPerPerson());
                bomTemplateItemRepository.save(item);
            }
        }

        return getTemplateById(savedTemplate.getTemplateId());
    }

    public BOMTemplateDTO updateTemplate(BOMTemplateDTO dto) {
        if (dto.getTemplateId() == null || dto.getTemplateId().trim().isEmpty()) {
            throw new IllegalArgumentException("Template ID is required for updating BOM Template.");
        }

        BOMTemplate template = bomTemplateRepository.findById(dto.getTemplateId())
                .orElseThrow(() -> new RuntimeException("BOMTemplate not found with ID: " + dto.getTemplateId()));

        if (dto.getTemplateName() != null && !dto.getTemplateName().trim().isEmpty()) {
            template.setTemplateName(dto.getTemplateName());
        }

        if (dto.getItemId() != null && !dto.getItemId().trim().isEmpty()) {
            if (bomTemplateRepository.existsByFoodItem_ItemIdAndTemplateIdNot(dto.getItemId(), dto.getTemplateId())) {
                throw new IllegalArgumentException("A BOM Template already exists for Food Item ID: " + dto.getItemId());
            }
            FoodItem foodItem = foodItemRepository.findById(dto.getItemId())
                    .orElseThrow(() -> new RuntimeException("Food Item not found with ID: " + dto.getItemId()));
            template.setFoodItem(foodItem);
        }

        BOMTemplate savedTemplate = bomTemplateRepository.save(template);

        if (dto.getItems() != null) {
            List<BOMTemplateItem> existingItems = bomTemplateItemRepository.findByBomTemplate_TemplateId(dto.getTemplateId());
            bomTemplateItemRepository.deleteAll(existingItems);

            for (BOMTemplateItemDTO itemDTO : dto.getItems()) {
                RawMaterial material = rawMaterialRepository.findById(itemDTO.getMaterialId())
                        .orElseThrow(() -> new RuntimeException("Raw Material not found with ID: " + itemDTO.getMaterialId()));

                BOMTemplateItem item = new BOMTemplateItem();
                item.setBomTemplate(savedTemplate);
                item.setRawMaterial(material);
                item.setQtyPerPerson(itemDTO.getQtyPerPerson());
                bomTemplateItemRepository.save(item);
            }
        }

        return getTemplateById(savedTemplate.getTemplateId());
    }

    public BOMTemplateDTO getTemplateById(String templateId) {
        BOMTemplate template = bomTemplateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("BOMTemplate not found with ID: " + templateId));

        return mapToDTO(template);
    }

    public BOMTemplateDTO getTemplateByFoodItemId(String foodItemId) {
        BOMTemplate template = bomTemplateRepository.findByFoodItem_ItemId(foodItemId)
                .orElseThrow(() -> new RuntimeException("BOMTemplate not found for Food Item ID: " + foodItemId));

        return mapToDTO(template);
    }

    public List<BOMTemplateDTO> getAllTemplates() {
        List<BOMTemplate> templates = bomTemplateRepository.findAll();
        List<BOMTemplateDTO> dtos = new ArrayList<>();
        for (BOMTemplate t : templates) {
            dtos.add(mapToDTO(t));
        }
        return dtos;
    }

    public void deleteTemplate(String templateId) {
        BOMTemplate template = bomTemplateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("BOMTemplate not found with ID: " + templateId));
        List<BOMTemplateItem> items = bomTemplateItemRepository.findByBomTemplate_TemplateId(templateId);
        bomTemplateItemRepository.deleteAll(items);
        bomTemplateRepository.delete(template);
    }

    private BOMTemplateDTO mapToDTO(BOMTemplate template) {
        BOMTemplateDTO dto = new BOMTemplateDTO();
        dto.setTemplateId(template.getTemplateId());
        dto.setTemplateName(template.getTemplateName());
        dto.setCreatedBy(template.getCreatedBy() != null ? template.getCreatedBy().getUserId() : null);
        dto.setCreatorName(template.getCreatedBy() != null ? template.getCreatedBy().getName() : null);
        dto.setItemId(template.getFoodItem() != null ? template.getFoodItem().getItemId() : null);
        dto.setItemName(template.getFoodItem() != null ? template.getFoodItem().getName() : null);

        List<BOMTemplateItem> items = bomTemplateItemRepository.findByBomTemplate_TemplateId(template.getTemplateId());
        List<BOMTemplateItemDTO> itemDTOs = items.stream().map(item -> {
            BOMTemplateItemDTO itemDTO = new BOMTemplateItemDTO();
            itemDTO.setTemplateItemId(item.getTemplateItemId());
            itemDTO.setTemplateId(template.getTemplateId());
            if (item.getRawMaterial() != null) {
                itemDTO.setMaterialId(item.getRawMaterial().getMaterialId());
                itemDTO.setMaterialName(item.getRawMaterial().getMaterialName());
                itemDTO.setUnitOfMeasure(item.getRawMaterial().getUnitOfMeasure());
            }
            itemDTO.setQtyPerPerson(item.getQtyPerPerson());
            return itemDTO;
        }).collect(Collectors.toList());

        dto.setItems(itemDTOs);
        return dto;
    }

    public List<OrderBOMCalculationResultDTO> calculateOrderBOM(OrderBOMCalculationRequestDTO request) {
        List<OrderBOMCalculationResultDTO> resultList = new ArrayList<>();
        if (request == null) {
            return resultList;
        }

        List<OrderItemDetailDTO> details = request.getOrderDetails();
        if ((details == null || details.isEmpty()) && request.getOrderId() != null && !request.getOrderId().trim().isEmpty()) {
            List<RestaurantOrderDetail> dbDetails = restaurantOrderDetailRepository.findByRestaurantOrder_OrderId(request.getOrderId());
            details = dbDetails.stream().map(d -> new OrderItemDetailDTO(
                    d.getFoodItem() != null ? d.getFoodItem().getItemId() : null,
                    d.getOrderedQty()
            )).collect(Collectors.toList());
        }

        if (details == null || details.isEmpty()) {
            return resultList;
        }

        for (OrderItemDetailDTO detail : details) {
            if (detail.getItemId() == null || detail.getItemId().trim().isEmpty()) {
                continue;
            }

            int orderedQty = (detail.getOrderedQty() != null && detail.getOrderedQty() > 0) ? detail.getOrderedQty() : 1;

            Optional<FoodItem> foodItemOpt = foodItemRepository.findById(detail.getItemId());
            String foodItemName = foodItemOpt.map(FoodItem::getName).orElse("Unknown Food Item");

            OrderBOMCalculationResultDTO foodItemResult = new OrderBOMCalculationResultDTO();
            foodItemResult.setItemId(detail.getItemId());
            foodItemResult.setItemName(foodItemName);
            foodItemResult.setRequiredQuantity(orderedQty);
            foodItemResult.setRawMaterialDetails(new ArrayList<>());

            Optional<BOMTemplate> bomTemplateOpt = bomTemplateRepository.findByFoodItem_ItemId(detail.getItemId());
            if (bomTemplateOpt.isPresent()) {
                BOMTemplate template = bomTemplateOpt.get();
                List<BOMTemplateItem> templateItems = bomTemplateItemRepository.findByBomTemplate_TemplateId(template.getTemplateId());

                for (BOMTemplateItem templateItem : templateItems) {
                    RawMaterial rawMaterial = templateItem.getRawMaterial();
                    if (rawMaterial == null) {
                        continue;
                    }

                    BigDecimal qtyPerPerson = templateItem.getQtyPerPerson() != null ? templateItem.getQtyPerPerson() : BigDecimal.ZERO;
                    BigDecimal totalRequired = qtyPerPerson.multiply(BigDecimal.valueOf(orderedQty));
                    BigDecimal warehouseStock = rawMaterial.getQuantityOnHand() != null ? rawMaterial.getQuantityOnHand() : BigDecimal.ZERO;

                    boolean isShortage = totalRequired.compareTo(warehouseStock) > 0;
                    BigDecimal shortageQty = isShortage ? totalRequired.subtract(warehouseStock) : BigDecimal.ZERO;
                    String status = isShortage ? "Shortage" : "In Stock";

                    RawMaterialCalculationDetailDTO materialDTO = new RawMaterialCalculationDetailDTO();
                    materialDTO.setMaterialId(rawMaterial.getMaterialId());
                    materialDTO.setMaterialName(rawMaterial.getMaterialName());
                    materialDTO.setCategory(rawMaterial.getCategory());
                    materialDTO.setUnitOfMeasure(rawMaterial.getUnitOfMeasure());
                    materialDTO.setQtyPerPerson(qtyPerPerson);
                    materialDTO.setOrderedQty(orderedQty);
                    materialDTO.setTotalRequiredQty(totalRequired);
                    materialDTO.setQuantityOnHand(warehouseStock);
                    materialDTO.setStatus(status);
                    materialDTO.setIsShortage(isShortage);
                    materialDTO.setShortageQty(shortageQty);

                    foodItemResult.getRawMaterialDetails().add(materialDTO);
                }
            }

            resultList.add(foodItemResult);
        }

        return resultList;
    }
}
