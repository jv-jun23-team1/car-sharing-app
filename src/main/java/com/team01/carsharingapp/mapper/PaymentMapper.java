package com.team01.carsharingapp.mapper;

import com.team01.carsharingapp.config.MapperConfig;
import com.team01.carsharingapp.dto.payment.PaymentDto;
import com.team01.carsharingapp.dto.payment.PaymentRequestDto;
import com.team01.carsharingapp.model.Payment;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentDto toDto(Payment payment);

    Payment toEntity(PaymentRequestDto paymentRequestDto);

    @AfterMapping
    default void setOrderId(@MappingTarget PaymentDto paymentDto, Payment payment) {
        paymentDto.setRentalId(payment.getRental().getId());
    }

    @AfterMapping
    default void setType(@MappingTarget Payment payment, PaymentRequestDto paymentRequestDto) {
        payment.setType(Payment.Type.valueOf(paymentRequestDto.type()));
    }
}
