package br.com.fullcycle.hexagonal.application.usecases;

import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.fullcycle.hexagonal.IntegrationTestBase;
import br.com.fullcycle.hexagonal.infrastructure.exception.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.models.Customer;
import br.com.fullcycle.hexagonal.infrastructure.repositories.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


class CreateCustomerUseCaseIT extends IntegrationTestBase {
  @Autowired
  private CreateCustomerUseCase useCase;
  @Autowired
  private CustomerRepository customerRepository;

  @AfterEach
  void tearDown() {
    customerRepository.deleteAll();
  }

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

    createCustomer(expectedCPF, expectedEmail, expectedName);

    final var createInput =
        new CreateCustomerUseCase.Input(expectedName, expectedEmail, expectedCPF);

    //when
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

    createCustomer(expectedCPF, expectedEmail, expectedName);

    //when
    final var actualException =
        assertThrows(ValidationException.class, () -> useCase.execute(createInput));

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

}