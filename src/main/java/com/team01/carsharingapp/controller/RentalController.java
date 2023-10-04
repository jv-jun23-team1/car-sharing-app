package com.team01.carsharingapp.controller;

import com.team01.carsharingapp.dto.rental.CreateRentalRequestDto;
import com.team01.carsharingapp.dto.rental.RentalDto;
import com.team01.carsharingapp.service.RentalService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/rentals")
public class RentalController {
    private final RentalService rentalService;

    @GetMapping("/{id}")
    public RentalDto getById(
            @PathVariable Long id
    ) {
        return rentalService.getById(id);
    }

    @GetMapping("/")
    public List<RentalDto> getByUserIdAndStatus(
            @RequestParam(name = "user_id") Long userId,
            @RequestParam(name = "is_active") Boolean isActive,
            Pageable pageable
    ) {
        return rentalService.getByUserIdAndStatus(userId, isActive, pageable);
    }

    @PostMapping
    public RentalDto create(
            @RequestBody @Valid CreateRentalRequestDto requestDto
    ) {
        return rentalService.create(requestDto);
    }

    @PostMapping("/return/{id}")
    public RentalDto returnCarByRentalId(
            @PathVariable Long id
    ) {
        return rentalService.returnCarByRentalId(id);
    }
}
