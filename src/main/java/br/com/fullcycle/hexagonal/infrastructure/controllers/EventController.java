package br.com.fullcycle.hexagonal.infrastructure.controllers;

import static org.springframework.http.HttpStatus.CREATED;

import br.com.fullcycle.hexagonal.application.usecases.CreateEventUseCase;
import br.com.fullcycle.hexagonal.application.usecases.SubscribeCustomerToEventUseCase;
import br.com.fullcycle.hexagonal.infrastructure.dtos.EventDTO;
import br.com.fullcycle.hexagonal.infrastructure.dtos.SubscribeDTO;
import br.com.fullcycle.hexagonal.infrastructure.exception.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.services.CustomerService;
import br.com.fullcycle.hexagonal.infrastructure.services.EventService;
import br.com.fullcycle.hexagonal.infrastructure.services.PartnerService;
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
  private final CustomerService customerService;
  private final EventService eventService;
  private final PartnerService partnerService;

  public EventController(final CustomerService customerService, final EventService eventService,
                         final PartnerService partnerService) {
    this.customerService = Objects.requireNonNull(customerService);
    this.eventService = Objects.requireNonNull(eventService);
    this.partnerService = Objects.requireNonNull(partnerService);
  }

  @PostMapping
  @ResponseStatus(CREATED)
  public ResponseEntity<?> create(@RequestBody EventDTO dto) {
    try {
      Long partnerId = Objects.requireNonNull(dto.getPartner(), "Partner is required").getId();
      final var useCase = new CreateEventUseCase(eventService, partnerService);
      final var output = useCase.execute(
          new CreateEventUseCase.Input(
              dto.getDate(),
              dto.getName(),
              partnerId,
              dto.getTotalSpots()));
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
      final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
      final var output =
          useCase.execute(new SubscribeCustomerToEventUseCase.Input(id, dto.getCustomerId()));
      return ResponseEntity.ok(output);
    } catch (ValidationException ex) {
      return ResponseEntity.unprocessableEntity().body(ex.getMessage());
    }
  }
}
