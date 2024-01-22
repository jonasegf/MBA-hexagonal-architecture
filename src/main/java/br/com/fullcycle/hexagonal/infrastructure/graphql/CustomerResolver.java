package br.com.fullcycle.hexagonal.infrastructure.graphql;

import br.com.fullcycle.hexagonal.application.usecases.CreateCustomerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.GetCustomerUseCase;
import br.com.fullcycle.hexagonal.infrastructure.dtos.NewCustomerDTO;
import java.util.Objects;
import java.util.Optional;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class CustomerResolver {
  private final CreateCustomerUseCase createCustomerUseCase;
  private final GetCustomerUseCase getCustomerUseCase;

  public CustomerResolver(final CreateCustomerUseCase createCustomerUseCase,
                          final GetCustomerUseCase getCustomerUseCase) {
    this.createCustomerUseCase = Objects.requireNonNull(createCustomerUseCase);
    this.getCustomerUseCase = Objects.requireNonNull(getCustomerUseCase);
  }

  @MutationMapping
  public CreateCustomerUseCase.Output createCustomer(@Argument NewCustomerDTO input) {
    return createCustomerUseCase.execute(
        new CreateCustomerUseCase.Input(input.name(), input.email(), input.cpf()));
  }

  @QueryMapping
  public Optional<GetCustomerUseCase.Output> customerOfId(@Argument Long id) {
    return getCustomerUseCase.execute(new GetCustomerUseCase.Input(id));
  }
}
