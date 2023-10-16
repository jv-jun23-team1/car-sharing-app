package com.coffee.jedi.carsharingapp.controller;

import com.coffee.jedi.carsharingapp.dto.rental.CreateRentalRequestDto;
import com.coffee.jedi.carsharingapp.dto.rental.RentalDto;
import com.coffee.jedi.carsharingapp.model.User;
import com.coffee.jedi.carsharingapp.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rental management", description = "Endpoints for managing rentals")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/rentals")
public class RentalController {
    private final RentalService rentalService;

    @GetMapping("/{id}")
    @Operation(summary = "Get rental by ID")
    public RentalDto getById(
            @PathVariable Long id
    ) {
        return rentalService.getById(getCurrentUser(), id);
    }

    @GetMapping("/")
    @Operation(summary = "Get rentals by user ID and status",
            description = """
                    Get list of rentals by user ID and status.
                    Pagination and sorting included.
                    If user set an "user_id" parameter, it'll throw an exception.
                    """)
    public List<RentalDto> getByUserIdAndStatus(
            @RequestParam(name = "user_id") Long userId,
            @RequestParam(name = "is_active") Boolean isActive,
            Pageable pageable
    ) {
        return rentalService.getByUserIdAndStatus(getCurrentUser(), userId, isActive, pageable);
    }

    @PostMapping
    @Operation(summary = "Add a new rental", description = "Create a new rental. "
            + "Validation included.")
    public RentalDto create(
            @RequestBody @Valid CreateRentalRequestDto requestDto
    ) {
        return rentalService.create(getCurrentUser(), requestDto);
    }

    @PostMapping("/return/{id}")
    @Operation(summary = "Set actual return date")
    public RentalDto carReturnByRentalId(
            @PathVariable Long id
    ) {
        return rentalService.carReturnByRentalId(getCurrentUser(), id);
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
