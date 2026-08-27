package com.example.ov_artifact.controller;

import com.example.ov_artifact.dto.BOMTemplateDTO;
import com.example.ov_artifact.dto.OrderBOMCalculationRequestDTO;
import com.example.ov_artifact.dto.OrderBOMCalculationResultDTO;
import com.example.ov_artifact.services.BOMTemplateService;
import com.example.ov_artifact.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/bom-templates")
@RequiredArgsConstructor
public class BOMTemplateController {

    private final BOMTemplateService bomTemplateService;

    @PostMapping("/calculate-order-bom")
    public ResponseEntity<StandardResponse> calculateOrderBOM(@RequestBody OrderBOMCalculationRequestDTO requestDTO) {
        List<OrderBOMCalculationResultDTO> results = bomTemplateService.calculateOrderBOM(requestDTO);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "BOM Calculation Completed Successfully", results),
                HttpStatus.OK);
    }

    @PostMapping("/calculate")
    public ResponseEntity<StandardResponse> calculateBOMAlias(@RequestBody OrderBOMCalculationRequestDTO requestDTO) {
        List<OrderBOMCalculationResultDTO> results = bomTemplateService.calculateOrderBOM(requestDTO);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "BOM Calculation Completed Successfully", results),
                HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<StandardResponse> createTemplate(@RequestBody BOMTemplateDTO bomTemplateDTO) {
        BOMTemplateDTO createdTemplate = bomTemplateService.createTemplate(bomTemplateDTO);
        return new ResponseEntity<>(
                new StandardResponse(true, 201, "BOM Template Created Successfully", createdTemplate),
                HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<StandardResponse> updateTemplate(@RequestBody BOMTemplateDTO bomTemplateDTO) {
        BOMTemplateDTO updatedTemplate = bomTemplateService.updateTemplate(bomTemplateDTO);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "BOM Template Updated Successfully", updatedTemplate),
                HttpStatus.OK);
    }

    @GetMapping("/food-item/{foodItemId}")
    public ResponseEntity<StandardResponse> getTemplateByFoodItemId(@PathVariable String foodItemId) {
        BOMTemplateDTO template = bomTemplateService.getTemplateByFoodItemId(foodItemId);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "BOM Template Fetched Successfully for Food Item", template),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getTemplateById(@PathVariable String id) {
        BOMTemplateDTO template = bomTemplateService.getTemplateById(id);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "BOM Template Fetched Successfully", template),
                HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<StandardResponse> getAllTemplates() {
        List<BOMTemplateDTO> templates = bomTemplateService.getAllTemplates();
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "BOM Templates Fetched Successfully", templates),
                HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<StandardResponse> deleteTemplate(@PathVariable String id) {
        bomTemplateService.deleteTemplate(id);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "BOM Template Deleted Successfully", null),
                HttpStatus.OK);
    }
}
