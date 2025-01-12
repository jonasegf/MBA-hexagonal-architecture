package br.com.fullcycle.hexagonal.infrastructure.graphql;

import br.com.fullcycle.hexagonal.application.usecases.CreatePartnerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.GetPartnerByIdUseCase;
import br.com.fullcycle.hexagonal.infrastructure.dtos.PartnerDTO;
import java.util.Objects;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class PartnerResolver {
  private final CreatePartnerUseCase createPartnerUseCase;
  private final GetPartnerByIdUseCase getPartnerByIdUseCase;

  public PartnerResolver(final CreatePartnerUseCase createPartnerUseCase,
                         final GetPartnerByIdUseCase getPartnerByIdUseCase) {
    this.createPartnerUseCase = Objects.requireNonNull(createPartnerUseCase);
    this.getPartnerByIdUseCase = Objects.requireNonNull(getPartnerByIdUseCase);
  }

  @MutationMapping
  public CreatePartnerUseCase.Output createPartner(@Argument PartnerDTO input) {
    return createPartnerUseCase.execute(
        new CreatePartnerUseCase.Input(input.name(), input.cnpj(), input.email()));

  }

  @QueryMapping
  public GetPartnerByIdUseCase.Output partnerOfId(@Argument Long id) {
    return getPartnerByIdUseCase.execute(new GetPartnerByIdUseCase.Input(id)).get();
  }
}
