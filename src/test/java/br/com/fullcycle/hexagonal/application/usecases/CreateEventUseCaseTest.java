package br.com.fullcycle.hexagonal.application.usecases;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.com.fullcycle.hexagonal.application.exception.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.models.Event;
import br.com.fullcycle.hexagonal.infrastructure.models.Partner;
import br.com.fullcycle.hexagonal.infrastructure.services.EventService;
import br.com.fullcycle.hexagonal.infrastructure.services.PartnerService;
import io.hypersistence.tsid.TSID;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateEventUseCaseTest {
  @Test
  @DisplayName("Deve criar um evento")
  public void testCreate() {
    //given
    final var expectedDate = "2021-01-01";
    final var expectedName = "Disney on Ice";
    final var expectedTotalSpots = 10;
    final var expectedPartnerId = TSID.fast().toLong();

    final var createInput =
        new CreateEventUseCase.Input(expectedDate, expectedName, expectedPartnerId,
            expectedTotalSpots);

    //when
    final var eventService = mock(EventService.class);
    final var partnerService = mock(PartnerService.class);

    when(eventService.save(any())).thenAnswer(a -> {
      final var e = a.getArgument(0, Event.class);
      e.setId(TSID.fast().toLong());
      return e;
    });

    when(partnerService.findById(eq(expectedPartnerId))).thenReturn(Optional.of(new Partner()));

    final var useCase = new CreateEventUseCase(eventService, partnerService);
    final var output = useCase.execute(createInput);

    //then
    Assertions.assertNotNull(output.id());
    Assertions.assertEquals(expectedName, output.name());
    Assertions.assertEquals(expectedDate, output.date());
    Assertions.assertEquals(expectedTotalSpots, output.totalSpots());
    Assertions.assertEquals(expectedPartnerId, output.partnerId());
  }

  @Test
  @DisplayName("Não deve criar um evento quando o parceiro não for encontrado")
  void testShouldNotCreateWhenPartnerNotFoundShouldThrowError() {
    //given
    final var expectedDate = "2021-01-01";
    final var expectedName = "Disney on Ice";
    final var expectedTotalSpots = 10;
    final var expectedPartnerId = TSID.fast().toLong();
    final var expectedError = "Partner not found";

    final var createInput =
        new CreateEventUseCase.Input(expectedDate, expectedName, expectedPartnerId,
            expectedTotalSpots);

    //when
    final var eventService = mock(EventService.class);
    final var partnerService = mock(PartnerService.class);


    when(partnerService.findById(eq(expectedPartnerId))).thenReturn(Optional.empty());

    final var useCase = new CreateEventUseCase(eventService, partnerService);
    final var actualException =
        Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

    //then
    Assertions.assertEquals(expectedError, actualException.getMessage());
  }


}