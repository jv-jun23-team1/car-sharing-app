package com.team01.carsharingapp.dto.stripe;

import lombok.Data;

@Data
public class StripeDto {
    private String sessionId;
    private String sessionUrl;
}
