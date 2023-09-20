package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.IntegrationTestBase;
import br.com.fullcycle.hexagonal.infrastructure.exception.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.models.Customer;
import br.com.fullcycle.hexagonal.infrastructure.models.Event;
import br.com.fullcycle.hexagonal.infrastructure.models.TicketStatus;
import br.com.fullcycle.hexagonal.infrastructure.repositories.CustomerRepository;
import br.com.fullcycle.hexagonal.infrastructure.repositories.EventRepository;
import br.com.fullcycle.hexagonal.infrastructure.repositories.PartnerRepository;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SubscribeCustomerToEventUseCaseIT extends IntegrationTestBase {
  @Autowired
  private PartnerRepository partnerRepository;
  @Autowired
  private CustomerRepository customerRepository;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private SubscribeCustomerToEventUseCase useCase;

  @AfterEach
  @BeforeEach
  void tearDown() {
    eventRepository.deleteAll();
    customerRepository.deleteAll();
    partnerRepository.deleteAll();
  }

  @Test
  @DisplayName("Deve comprar um ticket de um evento")
  public void testReserveTicket() {
    //given
    final var customerID =
        createCustomer("12345678981", "croco.dile@gmail.com", "crocodile").getId();
    final var eventID = createEvent("Show dos jacares", 10).getId();

    final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventID, customerID);

    //when
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
    final var eventID = TSID.fast().toLong();
    final var expectedError = "Event not found";

    final var customerID =
        createCustomer("12345678981", "croco.dile@gmail.com", "crocodile").getId();

    final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventID, customerID);

    //when
    final var actualException =
        Assertions.assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

    //then
    Assertions.assertEquals(expectedError, actualException.getMessage());
  }

  @Test
  @DisplayName("Cliente não pode comprar ticket repetido")
  public void testReserveTicketAlreadyOwns() {
    //given
    final var expectedError = "Email already registered";

    final var customerID =
        createCustomer("12345678981", "croco.dile@gmail.com", "crocodile").getId();

    final var eventID = createEvent("Show dos jacares", 10).getId();

    final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventID, customerID);

    useCase.execute(subscribeInput);

    //when
    final var actualException =
        Assertions.assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

    //then
    Assertions.assertEquals(expectedError, actualException.getMessage());
  }

  @Test
  @DisplayName("Não deve comprar um ticket de um evento se não tiver mais tickets")
  public void testReserveTicketOutOfTickets() {
    //given
    final var expectedError = "Event sold out";

    final var customerID =
        createCustomer("12345678981", "croco.dile@gmail.com", "crocodile").getId();

    final var eventID = createEvent("Show dos jacares", 0).getId();

    final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventID, customerID);

    //when
    final var actualException =
        Assertions.assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

    //then
    Assertions.assertEquals(expectedError, actualException.getMessage());
  }

  private Customer createCustomer(final String cpf, final String email,
                                  final String name) {
    final var aCustomer = new Customer();
    aCustomer.setCpf(cpf);
    aCustomer.setEmail(email);
    aCustomer.setName(name);
    return customerRepository.save(aCustomer);
  }

  private Event createEvent(final String name, final Integer totalSpots) {
    final var event = new Event();
    event.setName(name);
    event.setTotalSpots(totalSpots);
    return eventRepository.save(event);
  }

}