package com.team01.carsharingapp.mapper;

import com.team01.carsharingapp.config.MapperConfig;
import com.team01.carsharingapp.dto.payment.PaymentDto;
import com.team01.carsharingapp.dto.payment.PaymentRequestDto;
import com.team01.carsharingapp.model.Payment;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(target = "rentalId", source = "payment.rental.id")
    PaymentDto toDto(Payment payment);

    @Mapping(target = "rental.id", source = "rentalId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sessionUrl", ignore = true)
    @Mapping(target = "sessionId", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    Payment toEntity(PaymentRequestDto paymentRequestDto);

    @AfterMapping
    default void setType(@MappingTarget Payment payment, PaymentRequestDto paymentRequestDto) {
        payment.setType(Payment.Type.valueOf(paymentRequestDto.type()));
    }
}
