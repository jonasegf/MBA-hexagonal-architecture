package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.models.Partner;
import br.com.fullcycle.hexagonal.services.PartnerService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class GetPartnerByIdUseCaseTest {

  @Test
  @DisplayName("Deve obter um parceiro por id")
  void testGetById() {
    // given
    final var expectedID = UUID.randomUUID().getMostSignificantBits();
    final var expectedName = "crocodile";
    final var expectedCNPJ = "01762115000186";
    final var expectedEmail = "croco.dile@gmail.com";

    final var aPartner = new Partner(expectedID, expectedName, expectedCNPJ, expectedEmail);

    final var input = new GetPartnerByIdUseCase.Input(expectedID);

    // when
    final var partnerService = Mockito.mock(PartnerService.class);
    Mockito.when(partnerService.findById(expectedID)).thenReturn(Optional.of(aPartner));

    final var useCase = new GetPartnerByIdUseCase(partnerService);
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
    final var partnerService = Mockito.mock(PartnerService.class);
    Mockito.when(partnerService.findById(expectedID)).thenReturn(Optional.empty());

    final var useCase = new GetPartnerByIdUseCase(partnerService);
    final var output = useCase.execute(input);

    // then
    Assertions.assertTrue(output.isEmpty());
  }
}