package br.com.fullcycle.hexagonal.controllers;

import static org.springframework.http.HttpStatus.CREATED;

import br.com.fullcycle.hexagonal.dtos.EventDTO;
import br.com.fullcycle.hexagonal.dtos.SubscribeDTO;
import br.com.fullcycle.hexagonal.models.Event;
import br.com.fullcycle.hexagonal.models.Ticket;
import br.com.fullcycle.hexagonal.models.TicketStatus;
import br.com.fullcycle.hexagonal.services.CustomerService;
import br.com.fullcycle.hexagonal.services.EventService;
import br.com.fullcycle.hexagonal.services.PartnerService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
  public Event create(@RequestBody EventDTO dto) {
    var event = new Event();
    event.setDate(LocalDate.parse(dto.getDate(), DateTimeFormatter.ISO_DATE));
    event.setName(dto.getName());
    event.setTotalSpots(dto.getTotalSpots());

    var partner = partnerService.findById(dto.getPartner().getId());
    if (partner.isEmpty()) {
      throw new RuntimeException("Partner not found");
    }
    event.setPartner(partner.get());

    return eventService.save(event);
  }

  @Transactional
  @PostMapping(value = "/{id}/subscribe")
  public ResponseEntity<?> subscribe(@PathVariable Long id, @RequestBody SubscribeDTO dto) {

    var maybeCustomer = customerService.findById(dto.getCustomerId());
    if (maybeCustomer.isEmpty()) {
      return ResponseEntity.unprocessableEntity().body("Customer not found");
    }

    var maybeEvent = eventService.findById(id);
    if (maybeEvent.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    var maybeTicket = eventService.findTicketByEventIdAndCustomerId(id, dto.getCustomerId());
    if (maybeTicket.isPresent()) {
      return ResponseEntity.unprocessableEntity().body("Email already registered");
    }

    var customer = maybeCustomer.get();
    var event = maybeEvent.get();

    if (event.getTotalSpots() < event.getTickets().size() + 1) {
      throw new RuntimeException("Event sold out");
    }

    var ticket = new Ticket();
    ticket.setEvent(event);
    ticket.setCustomer(customer);
    ticket.setReservedAt(Instant.now());
    ticket.setStatus(TicketStatus.PENDING);

    event.getTickets().add(ticket);

    eventService.save(event);

    return ResponseEntity.ok(new EventDTO(event));
  }
}
