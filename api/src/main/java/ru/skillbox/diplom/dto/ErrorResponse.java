package ru.skillbox.diplom.dto;

import lombok.Data;

@Data
public class ErrorResponse {
    private boolean result;
    private String error;
}
