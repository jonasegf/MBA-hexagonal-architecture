package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.IntegrationTestBase;
import br.com.fullcycle.hexagonal.application.exception.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.models.Partner;
import br.com.fullcycle.hexagonal.infrastructure.repositories.EventRepository;
import br.com.fullcycle.hexagonal.infrastructure.repositories.PartnerRepository;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


class CreateEventUseCaseIT extends IntegrationTestBase {
  @Autowired
  private CreateEventUseCase useCase;
  @Autowired
  private EventRepository customerRepository;
  @Autowired
  private PartnerRepository partnerRepository;

  @AfterEach
  void tearDown() {
    customerRepository.deleteAll();
    partnerRepository.deleteAll();
  }

  @Test
  @DisplayName("Deve criar um evento")
  public void testCreate() {
    //given

    final var partner = createPartner("jacare", "111", "jaca-re@gmail.com");

    final var expectedDate = "2021-01-01";
    final var expectedName = "Disney on Ice";
    final var expectedTotalSpots = 10;
    final var expectedPartnerId = partner.getId();

    final var createInput =
        new CreateEventUseCase.Input(expectedDate, expectedName, expectedPartnerId,
            expectedTotalSpots);

    //when
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
    final var actualException =
        Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

    //then
    Assertions.assertEquals(expectedError, actualException.getMessage());
  }

  private Partner createPartner(final String name, final String cnpj, final String email) {
    final var partner = new Partner();
    partner.setEmail(email);
    partner.setCnpj(cnpj);
    partner.setName(name);
    return partnerRepository.save(partner);
  }

}