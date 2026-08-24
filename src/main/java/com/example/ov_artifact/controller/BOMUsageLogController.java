package com.example.ov_artifact.controller;

import com.example.ov_artifact.dto.BOMUsageLogDTO;
import com.example.ov_artifact.services.BOMUsageLogService;
import com.example.ov_artifact.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/bom-usage-logs")
@RequiredArgsConstructor
public class BOMUsageLogController {

    private final BOMUsageLogService bomUsageLogService;

    @PostMapping("/log")
    public ResponseEntity<StandardResponse> logUsage(@RequestBody BOMUsageLogDTO dto) {
        BOMUsageLogDTO savedLog = bomUsageLogService.logUsage(dto);
        return new ResponseEntity<>(
                new StandardResponse(true, 201, "BOM Usage Logged Successfully", savedLog),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getUsageLogById(@PathVariable String id) {
        BOMUsageLogDTO usageLog = bomUsageLogService.getUsageLogById(id);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "BOM Usage Log Fetched Successfully", usageLog),
                HttpStatus.OK);
    }

    @GetMapping("/by-template/{templateId}")
    public ResponseEntity<StandardResponse> getUsageLogsByTemplate(@PathVariable String templateId) {
        List<BOMUsageLogDTO> logs = bomUsageLogService.getUsageLogsByTemplate(templateId);
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "BOM Usage Logs Fetched Successfully", logs),
                HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<StandardResponse> getAllUsageLogs() {
        List<BOMUsageLogDTO> logs = bomUsageLogService.getAllUsageLogs();
        return new ResponseEntity<>(
                new StandardResponse(true, 200, "BOM Usage Logs Fetched Successfully", logs),
                HttpStatus.OK);
    }
}
