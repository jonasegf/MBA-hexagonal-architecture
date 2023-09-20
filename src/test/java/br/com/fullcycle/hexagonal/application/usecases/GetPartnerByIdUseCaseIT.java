package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.IntegrationTestBase;
import br.com.fullcycle.hexagonal.infrastructure.models.Partner;
import br.com.fullcycle.hexagonal.infrastructure.repositories.PartnerRepository;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GetPartnerByIdUseCaseIT extends IntegrationTestBase {
  @Autowired
  private PartnerRepository partnerRepository;
  @Autowired
  private GetPartnerByIdUseCase useCase;

  @AfterEach
  void tearDown() {
    partnerRepository.deleteAll();
  }

  @Test
  @DisplayName("Deve obter um parceiro por id")
  void testGetById() {
    // given
    final var expectedName = "crocodile";
    final var expectedCNPJ = "01762115000186";
    final var expectedEmail = "croco.dile@gmail.com";

    final var aPartner = createPartner(expectedCNPJ, expectedEmail, expectedName);
    final var expectedID = aPartner.getId();

    final var input = new GetPartnerByIdUseCase.Input(expectedID);

    // when
    final var output = useCase.execute(input).get();

    // then
    Assertions.assertEquals(expectedID, output.id());
    Assertions.assertEquals(expectedCNPJ, output.cnpj());
    Assertions.assertEquals(expectedEmail, output.email());
    Assertions.assertEquals(expectedName, output.name());
  }

  @Test
  @DisplayName("Deve retornar vazio ao tentar recuperar parceiro por ID não existente")
  void testGetByIDWithInvalidID() {
    // given
    final var expectedID = UUID.randomUUID().getMostSignificantBits();
    final var input = new GetPartnerByIdUseCase.Input(expectedID);

    // when
    final var output = useCase.execute(input);

    // then
    Assertions.assertTrue(output.isEmpty());
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