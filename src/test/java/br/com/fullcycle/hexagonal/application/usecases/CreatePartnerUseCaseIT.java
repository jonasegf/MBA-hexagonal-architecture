package br.com.fullcycle.hexagonal.application.usecases;

import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.fullcycle.hexagonal.IntegrationTestBase;
import br.com.fullcycle.hexagonal.infrastructure.exception.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.models.Partner;
import br.com.fullcycle.hexagonal.infrastructure.repositories.PartnerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CreatePartnerUseCaseIT extends IntegrationTestBase {
  @Autowired
  private CreatePartnerUseCase useCase;
  @Autowired
  private PartnerRepository partnerRepository;

  @AfterEach
  void tearDown() {
    partnerRepository.deleteAll();
  }

  @Test
  @DisplayName("Deve criar um parceiro")
  void testCreatePartner() {
    //given
    final var expectedName = "crocodile";
    final var expectedEmail = "croc.dile@gmail.com";
    final var expectedCNPJ = "01762115000186";

    final var createInput =
        new CreatePartnerUseCase.Input(expectedName, expectedCNPJ, expectedEmail);

    //when
    final var output = useCase.execute(createInput);

    //then
    Assertions.assertNotNull(output.id());
    Assertions.assertEquals(expectedCNPJ, output.cnpj());
    Assertions.assertEquals(expectedEmail, output.email());
    Assertions.assertEquals(expectedName, output.name());
  }

  @Test
  @DisplayName("Não deve criar um parceiro com CNPJ duplicado")
  void testCreatePartnerWithDuplicateCnpjShouldFail() {
    //given
    final var expectedName = "crocodile";
    final var expectedEmail = "croc.dile@gmail.com";
    final var expectedCNPJ = "01762115000186";
    final var expectedError = "Partner already exists";
    createPartner(expectedCNPJ, expectedEmail, expectedName);

    final var createInput =
        new CreatePartnerUseCase.Input(expectedName, expectedCNPJ, expectedEmail);
    //when

    //then
    final var actualException =
        assertThrows(ValidationException.class, () -> useCase.execute(createInput));

    Assertions.assertEquals(expectedError, actualException.getMessage());
  }

  private Partner createPartner(final String cnpj, final String email,
                                final String name) {
    final var aPartner = new Partner();
    aPartner.setCnpj(cnpj);
    aPartner.setEmail(email);
    aPartner.setName(name);
    return partnerRepository.save(aPartner);
  }

}