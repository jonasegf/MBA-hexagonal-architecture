package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.IntegrationTestBase;
import br.com.fullcycle.hexagonal.infrastructure.models.Customer;
import br.com.fullcycle.hexagonal.infrastructure.repositories.CustomerRepository;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GetCustomerByIdUseCaseIT extends IntegrationTestBase {
  @Autowired
  private GetCustomerUseCase useCase;
  @Autowired
  private CustomerRepository customerRepository;

  @AfterEach
  void tearDown() {
    customerRepository.deleteAll();
  }

  @Test
  @DisplayName("Deve obter um cliente por id")
  void testGetById() {
    // given
    final var expectedName = "crocodile";
    final var expectedCPF = "12345678981";
    final var expectedEmail = "croco.dile@gmail.com";

    final var aCustomer = createCustomer(expectedName, expectedEmail, expectedCPF);
    final var expectedID = aCustomer.getId();

    final var input = new GetCustomerUseCase.Input(expectedID);

    // when
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
    final var output = useCase.execute(input);

    // then
    Assertions.assertTrue(output.isEmpty());
  }

  private Customer createCustomer(final String name, final String email, final String cpf) {
    final var customer = new Customer();
    customer.setName(name);
    customer.setEmail(email);
    customer.setCpf(cpf);
    return customerRepository.save(customer);
  }

}