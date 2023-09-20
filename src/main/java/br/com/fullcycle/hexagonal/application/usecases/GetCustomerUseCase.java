package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.application.UseCase;
import br.com.fullcycle.hexagonal.infrastructure.services.CustomerService;
import java.util.Optional;

public class GetCustomerUseCase
    extends UseCase<GetCustomerUseCase.Input, Optional<GetCustomerUseCase.Output>> {

  private final CustomerService customerService;

  public GetCustomerUseCase(final CustomerService customerService) {
    this.customerService = customerService;
  }

  @Override
  public Optional<Output> execute(final Input input) {
    return customerService.findById(input.id)
        .map(x -> new Output(x.getId(), x.getName(), x.getEmail(), x.getCpf()));
  }

  public record Input(Long id) {
  }

  public record Output(Long id, String name, String email, String cpf) {
  }
}
