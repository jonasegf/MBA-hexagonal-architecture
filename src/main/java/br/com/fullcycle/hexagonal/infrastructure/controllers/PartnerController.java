package br.com.fullcycle.hexagonal.infrastructure.controllers;

import br.com.fullcycle.hexagonal.application.usecases.CreatePartnerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.GetPartnerByIdUseCase;
import br.com.fullcycle.hexagonal.infrastructure.dtos.PartnerDTO;
import br.com.fullcycle.hexagonal.application.exception.ValidationException;
import java.net.URI;
import java.util.Objects;
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
  private final CreatePartnerUseCase createPartnerUseCase;
  private final GetPartnerByIdUseCase getPartnerByIdUseCase;

  public PartnerController(final CreatePartnerUseCase createPartnerUseCase,
                           final GetPartnerByIdUseCase getPartnerByIdUseCase) {
    this.createPartnerUseCase = Objects.requireNonNull(createPartnerUseCase);
    this.getPartnerByIdUseCase = Objects.requireNonNull(getPartnerByIdUseCase);
  }

  @PostMapping
  public ResponseEntity<?> create(@RequestBody PartnerDTO dto) {
    try {
      final var output = createPartnerUseCase.execute(
          new CreatePartnerUseCase.Input(dto.name(), dto.cnpj(), dto.email()));

      return ResponseEntity.created(URI.create("/partners/" + output.id())).body(output);
    } catch (ValidationException ex) {
      return ResponseEntity.unprocessableEntity().body(ex.getMessage());
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> get(@PathVariable Long id) {
    return getPartnerByIdUseCase.execute(new GetPartnerByIdUseCase.Input(id))
        .map(ResponseEntity::ok)
        .orElseGet(ResponseEntity.notFound()::build);
  }

}
