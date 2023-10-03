package com.team01.carsharingapp.service;

import com.team01.carsharingapp.dto.rental.CreateRentalRequestDto;
import com.team01.carsharingapp.dto.rental.RentalDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalDto getByRentalIdAndUser(Long rentalId);

    RentalDto create(CreateRentalRequestDto requestDto);

    List<RentalDto> getByUserIdAndStatus(Long userId, boolean isActive, Pageable pageable);

    RentalDto returnCarByRentalId(Long id);
}
