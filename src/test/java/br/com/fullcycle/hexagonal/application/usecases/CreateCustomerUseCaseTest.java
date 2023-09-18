package br.com.fullcycle.hexagonal.application.usecases;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.com.fullcycle.hexagonal.exception.ValidationException;
import br.com.fullcycle.hexagonal.models.Customer;
import br.com.fullcycle.hexagonal.services.CustomerService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateCustomerUseCaseTest {
  @Test
  @DisplayName("Deve criar um cliente")
  void testCreateCustomer() {
    //given
    final var expectedName = "crocodile";
    final var expectedEmail = "croc.dile@gmail.com";
    final var expectedCPF = "12345678981";

    final var createInput =
        new CreateCustomerUseCase.Input(expectedName, expectedEmail, expectedCPF);

    //when
    final var customerService = mock(CustomerService.class);
    when(customerService.findByCpf(expectedCPF)).thenReturn(Optional.empty());
    when(customerService.findByEmail(expectedEmail)).thenReturn(Optional.empty());
    when(customerService.save(any())).thenAnswer(a -> {
      var customer = a.getArgument(0, Customer.class);
      customer.setId(UUID.randomUUID().getMostSignificantBits());
      return customer;
    });
    final var useCase = new CreateCustomerUseCase(customerService);
    final var output = useCase.execute(createInput);

    //then
    Assertions.assertNotNull(output.id());
    Assertions.assertEquals(expectedCPF, output.cpf());
    Assertions.assertEquals(expectedEmail, output.email());
    Assertions.assertEquals(expectedName, output.name());
  }

  @Test
  @DisplayName("Não deve cadastrar um cliente com CPF duplicado")
  void testCreateWithDuplicatedCPFShouldFail() {
    //given
    final var expectedName = "crocodile";
    final var expectedEmail = "croc.dile@gmail.com";
    final var expectedCPF = "12345678981";
    final var expectedError = "Customer already exists";

    final var createInput =
        new CreateCustomerUseCase.Input(expectedName, expectedEmail, expectedCPF);

    final var aCustomer = new Customer();
    aCustomer.setId(UUID.randomUUID().getMostSignificantBits());
    aCustomer.setCpf(expectedCPF);
    aCustomer.setEmail(expectedEmail);
    aCustomer.setName(expectedName);

    //when
    final var customerService = mock(CustomerService.class);
    when(customerService.findByCpf(expectedCPF)).thenReturn(Optional.of(aCustomer));

    final var useCase = new CreateCustomerUseCase(customerService);
    final var actualException =
        assertThrows(ValidationException.class, () -> useCase.execute(createInput));

    //then
    Assertions.assertEquals(expectedError, actualException.getMessage());
  }

  @Test
  @DisplayName("Não deve cadastrar um cliente com e-mail duplicado")
  public void testCreateWithDuplicatedEmailShouldFail() {
    //given
    final var expectedName = "crocodile";
    final var expectedEmail = "croc.dile@gmail.com";
    final var expectedCPF = "12345678981";
    final var expectedError = "Customer already exists";

    final var createInput =
        new CreateCustomerUseCase.Input(expectedName, expectedEmail, expectedCPF);

    final var aCustomer = new Customer();
    aCustomer.setId(UUID.randomUUID().getMostSignificantBits());
    aCustomer.setCpf(expectedCPF);
    aCustomer.setEmail(expectedEmail);
    aCustomer.setName(expectedName);

    //when
    final var customerService = mock(CustomerService.class);
    when(customerService.findByEmail(expectedEmail)).thenReturn(Optional.of(aCustomer));

    final var useCase = new CreateCustomerUseCase(customerService);
    final var actualException =
        assertThrows(ValidationException.class, () -> useCase.execute(createInput));

    //then
    Assertions.assertEquals(expectedError, actualException.getMessage());
  }

}