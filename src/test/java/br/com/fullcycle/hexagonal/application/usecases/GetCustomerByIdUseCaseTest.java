package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.models.Customer;
import br.com.fullcycle.hexagonal.services.CustomerService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class GetCustomerByIdUseCaseTest {
  @Test
  @DisplayName("Deve obter um cliente por id")
  void testGetById() {
    // given
    final var expectedID = UUID.randomUUID().getMostSignificantBits();
    final var expectedName = "crocodile";
    final var expectedCPF = "12345678981";
    final var expectedEmail = "croco.dile@gmail.com";

    final var aCustomer = new Customer(expectedID, expectedName, expectedEmail, expectedCPF);

    final var input = new GetCustomerUseCase.Input(expectedID);

    // when
    final var customerService = Mockito.mock(CustomerService.class);
    Mockito.when(customerService.findById(expectedID)).thenReturn(Optional.of(aCustomer));

    final var useCase = new GetCustomerUseCase(customerService);
    final var output = useCase.execute(input).get();

    // then
    Assertions.assertEquals(expectedID, output.id());
    Assertions.assertEquals(expectedCPF, output.cpf());
    Assertions.assertEquals(expectedEmail, output.email());
    Assertions.assertEquals(expectedName, output.name());
  }

  @Test
  @DisplayName("Deve retornar vazio ao tentar recuperar cliente por ID não existente")
  void testGetByIDWithInvalidID() {
    // given
    final var expectedID = UUID.randomUUID().getMostSignificantBits();

    final var input = new GetCustomerUseCase.Input(expectedID);

    // when
    final var customerService = Mockito.mock(CustomerService.class);
    Mockito.when(customerService.findById(expectedID)).thenReturn(Optional.empty());

    final var useCase = new GetCustomerUseCase(customerService);
    final var output = useCase.execute(input);

    // then
    Assertions.assertTrue(output.isEmpty());
  }

}