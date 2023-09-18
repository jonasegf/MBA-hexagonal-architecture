package br.com.fullcycle.hexagonal.application.usecases;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.com.fullcycle.hexagonal.exception.ValidationException;
import br.com.fullcycle.hexagonal.models.Partner;
import br.com.fullcycle.hexagonal.services.PartnerService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreatePartnerUseCaseTest {

  @Test
  @DisplayName("Deve criar um parceiro")
  void testCreatePartner() {
    //given
    final var expectedName = "crocodile";
    final var expectedEmail = "croc.dile@gmail.com";
    final var expectedCNPJ = "01.762.115/0001-86";

    final var createInput =
        new CreatePartnerUseCase.Input(expectedName, expectedCNPJ, expectedEmail);

    //when
    final var partnerService = mock(PartnerService.class);
    when(partnerService.findByCnpj(expectedCNPJ)).thenReturn(Optional.empty());
    when(partnerService.findByEmail(expectedEmail)).thenReturn(Optional.empty());
    when(partnerService.save(any())).thenAnswer(a -> {
      var partner = a.getArgument(0, Partner.class);
      partner.setId(UUID.randomUUID().getMostSignificantBits());
      return partner;
    });
    final var useCase = new CreatePartnerUseCase(partnerService);
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
    final var expectedId = UUID.randomUUID().getMostSignificantBits();

    final var createInput =
        new CreatePartnerUseCase.Input(expectedName, expectedCNPJ, expectedEmail);

    final var aPartner = new Partner();
    aPartner.setCnpj(expectedCNPJ);
    aPartner.setEmail(expectedEmail);
    aPartner.setName(expectedName);
    aPartner.setId(expectedId);

    //when
    final var partnerService = mock(PartnerService.class);
    when(partnerService.findByCnpj(expectedCNPJ)).thenReturn(Optional.of(aPartner));

    final var useCase = new CreatePartnerUseCase(partnerService);

    //then
    final var actualException =
        assertThrows(ValidationException.class, () -> useCase.execute(createInput));

    //then
    Assertions.assertEquals(expectedError, actualException.getMessage());
  }

}