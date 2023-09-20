package br.com.fullcycle.hexagonal.application.usecases;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.com.fullcycle.hexagonal.infrastructure.exception.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.models.Customer;
import br.com.fullcycle.hexagonal.infrastructure.models.Event;
import br.com.fullcycle.hexagonal.infrastructure.models.Ticket;
import br.com.fullcycle.hexagonal.infrastructure.models.TicketStatus;
import br.com.fullcycle.hexagonal.infrastructure.services.CustomerService;
import br.com.fullcycle.hexagonal.infrastructure.services.EventService;
import io.hypersistence.tsid.TSID;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SubscribeCustomerToEventUseCaseTest {
  @Test
  @DisplayName("Deve comprar um ticket de um evento")
  public void testReserveTicket() {
    //given
    final var expectedTicketsSize = 1;
    final var customerID = TSID.fast().toLong();
    final var eventID = TSID.fast().toLong();

    final var aCustomer = new Customer();
    aCustomer.setId(customerID);
    aCustomer.setName("crocodile");
    aCustomer.setEmail("croco.dile@gmail.com");
    aCustomer.setCpf("12345678981");

    final var anEvent = new Event();
    anEvent.setId(eventID);
    anEvent.setName("Show dos jacares");
    anEvent.setTotalSpots(10);

    final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventID, customerID);

    //when
    final var customerService = mock(CustomerService.class);
    final var eventService = mock(EventService.class);

    when(customerService.findById(customerID)).thenReturn(Optional.of(aCustomer));
    when(eventService.findById(eventID)).thenReturn(Optional.of(anEvent));
    when(eventService.findTicketByEventIdAndCustomerId(eventID, customerID)).thenReturn(
        Optional.empty());
    when(eventService.save(any())).thenAnswer(a -> {
      final var e = a.getArgument(0, Event.class);
      Assertions.assertEquals(expectedTicketsSize, e.getTickets().size());
      return e;
    });

    final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
    final var output = useCase.execute(subscribeInput);

    //then
    Assertions.assertEquals(eventID, output.eventId());
    Assertions.assertNotNull(output.reservationDate());
    Assertions.assertEquals(TicketStatus.PENDING.name(), output.ticketStatus());
  }

  @Test
  @DisplayName("Não deve deve comprar um ticket de um evento inexistente")
  public void testReserveTicketShouldNotSubscribeAndThrowException() {
    //given
    final var customerID = TSID.fast().toLong();
    final var eventID = TSID.fast().toLong();
    final var expectedError = "Event not found";

    final var aCustomer = new Customer();
    aCustomer.setId(customerID);
    aCustomer.setName("crocodile");
    aCustomer.setEmail("croco.dile@gmail.com");
    aCustomer.setCpf("12345678981");

    final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventID, customerID);

    //when
    final var customerService = mock(CustomerService.class);
    final var eventService = mock(EventService.class);

    when(customerService.findById(customerID)).thenReturn(Optional.of(aCustomer));
    when(eventService.findById(eventID)).thenReturn(Optional.empty());

    final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
    final var actualException =
        Assertions.assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

    //then
    Assertions.assertEquals(expectedError, actualException.getMessage());
  }

  @Test
  @DisplayName("Cliente não pode comprar ticket repetido")
  public void testReserveTicketAlreadyOwns() {
    //given
    final var customerID = TSID.fast().toLong();
    final var eventID = TSID.fast().toLong();
    final var expectedError = "Email already registered";

    final var aCustomer = new Customer();
    aCustomer.setId(customerID);
    aCustomer.setName("crocodile");
    aCustomer.setEmail("croco.dile@gmail.com");
    aCustomer.setCpf("12345678981");

    final var anEvent = new Event();
    anEvent.setId(eventID);
    anEvent.setName("Show dos jacares");
    anEvent.setTotalSpots(10);

    final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventID, customerID);

    //when
    final var customerService = mock(CustomerService.class);
    final var eventService = mock(EventService.class);

    when(customerService.findById(customerID)).thenReturn(Optional.of(aCustomer));
    when(eventService.findById(eventID)).thenReturn(Optional.of(anEvent));
    when(eventService.findTicketByEventIdAndCustomerId(eventID, customerID)).thenReturn(
        Optional.of(new Ticket()));

    final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
    final var actualException =
        Assertions.assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

    //then
    Assertions.assertEquals(expectedError, actualException.getMessage());
  }

  @Test
  @DisplayName("Não deve comprar um ticket de um evento se não tiver mais tickets")
  public void testReserveTicketOutOfTickets() {
    //given
    final var customerID = TSID.fast().toLong();
    final var eventID = TSID.fast().toLong();
    final var expectedError = "Event sold out";

    final var aCustomer = new Customer();
    aCustomer.setId(customerID);
    aCustomer.setName("crocodile");
    aCustomer.setEmail("croco.dile@gmail.com");
    aCustomer.setCpf("12345678981");

    final var anEvent = new Event();
    anEvent.setId(eventID);
    anEvent.setName("Show dos jacares");
    anEvent.setTotalSpots(0);

    final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventID, customerID);

    //when
    final var customerService = mock(CustomerService.class);
    final var eventService = mock(EventService.class);

    when(customerService.findById(customerID)).thenReturn(Optional.of(aCustomer));
    when(eventService.findById(eventID)).thenReturn(Optional.of(anEvent));
    when(eventService.findTicketByEventIdAndCustomerId(eventID, customerID)).thenReturn(
        Optional.empty());

    final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
    final var actualException =
        Assertions.assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

    //then
    Assertions.assertEquals(expectedError, actualException.getMessage());
  }

}