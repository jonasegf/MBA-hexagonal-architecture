package br.com.fullcycle.hexagonal.services;

import br.com.fullcycle.hexagonal.models.Event;
import br.com.fullcycle.hexagonal.models.Ticket;
import br.com.fullcycle.hexagonal.repositories.EventRepository;
import br.com.fullcycle.hexagonal.repositories.TicketRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {

    private final CustomerService customerService;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    public EventService(final CustomerService customerService,
                        final EventRepository eventRepository,
                        final TicketRepository ticketRepository) {
        this.customerService = customerService;
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }
    
    public Optional<Ticket> findTicketByEventIdAndCustomerId(Long id, Long customerId) {
        return ticketRepository.findByEventIdAndCustomerId(id, customerId);
    }
}
