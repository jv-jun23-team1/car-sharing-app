package com.team01.carsharingapp.event;

import com.team01.carsharingapp.model.Rental;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreateRentalEvent extends ApplicationEvent {
    private final Rental rental;

    public CreateRentalEvent(Object source, Rental rental) {
        super(source);
        this.rental = rental;
    }

    public static CreateRentalEvent of(Object source, Rental rental) {
        return new CreateRentalEvent(source, rental);
    }
}
