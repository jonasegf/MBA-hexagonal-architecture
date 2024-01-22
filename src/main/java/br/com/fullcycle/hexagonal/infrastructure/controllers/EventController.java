package br.com.fullcycle.hexagonal.infrastructure.controllers;

import static org.springframework.http.HttpStatus.CREATED;

import br.com.fullcycle.hexagonal.application.usecases.CreateEventUseCase;
import br.com.fullcycle.hexagonal.application.usecases.SubscribeCustomerToEventUseCase;
import br.com.fullcycle.hexagonal.infrastructure.dtos.NewEventDTO;
import br.com.fullcycle.hexagonal.infrastructure.dtos.SubscribeDTO;
import br.com.fullcycle.hexagonal.application.exception.ValidationException;
import java.net.URI;
import java.util.Objects;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "events")
public class EventController {
  private final CreateEventUseCase createEventUseCase;
  private final SubscribeCustomerToEventUseCase subscribeCustomerToEventUseCase;

  public EventController(final CreateEventUseCase createEventUseCase,
                         final SubscribeCustomerToEventUseCase subscribeCustomerToEventUseCase) {
    this.createEventUseCase = Objects.requireNonNull(createEventUseCase);
    this.subscribeCustomerToEventUseCase = Objects.requireNonNull(subscribeCustomerToEventUseCase);
  }


  @PostMapping
  @ResponseStatus(CREATED)
  public ResponseEntity<?> create(@RequestBody NewEventDTO dto) {
    try {
      final var input =
          new CreateEventUseCase.Input(dto.date(), dto.name(), dto.partnerId(),
              dto.totalSpots());
      final var output = createEventUseCase.execute(input);
      return ResponseEntity.created(URI.create("/events/" + output.id())).body(output);
    } catch (ValidationException ex) {
      return ResponseEntity.unprocessableEntity().body(ex.getMessage());
    }
  }

  @Transactional
  @PostMapping(value = "/{id}/subscribe")
  public ResponseEntity<?> subscribe(@PathVariable Long id,
                                     @RequestBody SubscribeDTO dto) {
    try {
      final var input = new SubscribeCustomerToEventUseCase.Input(id, dto.customerId());
      final var output = subscribeCustomerToEventUseCase.execute(input);
      return ResponseEntity.ok(output);
    } catch (ValidationException ex) {
      return ResponseEntity.unprocessableEntity().body(ex.getMessage());
    }
  }
}
