package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.application.UseCase;
import br.com.fullcycle.hexagonal.infrastructure.exception.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.models.Customer;
import br.com.fullcycle.hexagonal.infrastructure.services.CustomerService;
import jakarta.inject.Named;

public class CreateCustomerUseCase
    extends UseCase<CreateCustomerUseCase.Input, CreateCustomerUseCase.Output> {

  private final CustomerService customerService;

  public CreateCustomerUseCase(final CustomerService customerService) {
    this.customerService = customerService;
  }

  @Override
  public Output execute(final Input input) {
    if (customerService.findByCpf(input.cpf).isPresent()) {
      throw new ValidationException("Customer already exists");
    }
    if (customerService.findByEmail(input.email).isPresent()) {
      throw new ValidationException("Customer already exists");
    }

    var customer = new Customer();
    customer.setCpf(input.cpf);
    customer.setEmail(input.email);
    customer.setName(input.name);

    customer = customerService.save(customer);

    return new Output(customer.getId(), customer.getName(), customer.getEmail(), customer.getCpf());
  }

  public record Input(String name, String email, String cpf) {
  }

  public record Output(Long id, String name, String email, String cpf) {
  }
}
