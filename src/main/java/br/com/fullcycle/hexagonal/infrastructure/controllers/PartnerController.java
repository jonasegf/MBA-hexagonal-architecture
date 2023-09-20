package br.com.fullcycle.hexagonal.infrastructure.controllers;

import br.com.fullcycle.hexagonal.application.usecases.CreatePartnerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.GetPartnerByIdUseCase;
import br.com.fullcycle.hexagonal.infrastructure.dtos.PartnerDTO;
import br.com.fullcycle.hexagonal.infrastructure.exception.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.services.PartnerService;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "partners")
public class PartnerController {
  private final PartnerService partnerService;

  public PartnerController(final PartnerService partnerService) {
    this.partnerService = partnerService;
  }

  @PostMapping
  public ResponseEntity<?> create(@RequestBody PartnerDTO dto) {
    try {
      final var useCase = new CreatePartnerUseCase(partnerService);
      final var output = useCase.execute(
          new CreatePartnerUseCase.Input(dto.getName(), dto.getCnpj(), dto.getEmail()));

      return ResponseEntity.created(URI.create("/partners/" + output.id())).body(output);
    } catch (ValidationException ex) {
      return ResponseEntity.unprocessableEntity().body(ex.getMessage());
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> get(@PathVariable Long id) {
    final var useCase = new GetPartnerByIdUseCase(partnerService);
    return useCase.execute(new GetPartnerByIdUseCase.Input(id))
        .map(ResponseEntity::ok)
        .orElseGet(ResponseEntity.notFound()::build);
  }

}
