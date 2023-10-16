package com.coffee.jedi.carsharingapp.event;

import com.coffee.jedi.carsharingapp.model.Rental;
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
