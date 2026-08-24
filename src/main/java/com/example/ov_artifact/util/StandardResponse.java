package com.example.ov_artifact.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StandardResponse {
    private boolean success;
    private int statusCode;
    private String message;
    private Object data;
}