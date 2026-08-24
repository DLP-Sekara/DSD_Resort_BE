package com.example.ov_artifact.controller;

import com.example.ov_artifact.dto.RawMaterialDTO;
import com.example.ov_artifact.services.RawMaterialService;
import com.example.ov_artifact.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/raw-materials")
@RequiredArgsConstructor
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;

    @PostMapping("/add")
    public ResponseEntity<StandardResponse> addRawMaterial(@RequestBody RawMaterialDTO rawMaterialDTO) {
        RawMaterialDTO savedMaterial = rawMaterialService.addRawMaterial(rawMaterialDTO);
        return new ResponseEntity<>(
                new StandardResponse(true, 201, "Raw Material Added Successfully", savedMaterial),
                HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<StandardResponse> updateRawMaterial(@RequestBody RawMaterialDTO rawMaterialDTO) {
        rawMaterialService.updateRawMaterial(rawMaterialDTO);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Raw Material Updated Successfully", null),
                HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<StandardResponse> deleteRawMaterial(@PathVariable String id) {
        rawMaterialService.deleteRawMaterial(id);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Raw Material Deleted Successfully", null),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getRawMaterialById(@PathVariable String id) {
        RawMaterialDTO material = rawMaterialService.getRawMaterialById(id);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Raw Material Fetched Successfully", material),
                HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<StandardResponse> getAllRawMaterials() {
        List<RawMaterialDTO> rawMaterials = rawMaterialService.getAllRawMaterials();
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "Raw Materials Fetched Successfully", rawMaterials),
                HttpStatus.OK);
    }
}
