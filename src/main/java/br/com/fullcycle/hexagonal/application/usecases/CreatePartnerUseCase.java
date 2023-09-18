package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.application.UseCase;
import br.com.fullcycle.hexagonal.exception.ValidationException;
import br.com.fullcycle.hexagonal.models.Partner;
import br.com.fullcycle.hexagonal.services.PartnerService;

public class CreatePartnerUseCase extends UseCase<CreatePartnerUseCase.Input, CreatePartnerUseCase.Output> {

  private final PartnerService partnerService;

  public CreatePartnerUseCase(final PartnerService partnerService) {
    this.partnerService = partnerService;
  }

  @Override
  public Output execute(final Input input) {
    if (partnerService.findByCnpj(input.cnpj).isPresent()) {
      throw new ValidationException("Partner already exists");
    }
    if (partnerService.findByEmail(input.email).isPresent()) {
      throw new ValidationException("Partner already exists");
    }

    var partner = new Partner();
    partner.setName(input.name);
    partner.setCnpj(input.cnpj);
    partner.setEmail(input.email);

    partner = partnerService.save(partner);

    return new Output(partner.getId(),partner.getName(),partner.getCnpj(),partner.getEmail());
  }

  public record Input(String name, String cnpj, String email){};
  public record Output(Long id, String name, String cnpj, String email){};
}
