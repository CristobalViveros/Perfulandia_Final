
package com.example.pedido_ms.dto;

import java.time.LocalDateTime;

public class ErrorResponseDTO {

    private String error;
    private String message;
    private int status;
    private LocalDateTime timestamp;

    public ErrorResponseDTO(String error, String message, int status) {
        this.error = error;
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    // getters
}
