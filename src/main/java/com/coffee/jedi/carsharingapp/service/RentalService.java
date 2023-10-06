package com.coffee.jedi.carsharingapp.service;

import com.coffee.jedi.carsharingapp.dto.rental.CreateRentalRequestDto;
import com.coffee.jedi.carsharingapp.dto.rental.RentalDto;
import com.coffee.jedi.carsharingapp.model.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalDto getById(User user, Long rentalId);

    RentalDto create(User user, CreateRentalRequestDto requestDto);

    List<RentalDto> getByUserIdAndStatus(
            User user,
            Long userId,
            boolean isActive,
            Pageable pageable);

    RentalDto carReturnByRentalId(User user, Long id);
}
