package br.com.fullcycle.hexagonal.infrastructure.controllers;

import br.com.fullcycle.hexagonal.application.usecases.CreateCustomerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.GetCustomerUseCase;
import br.com.fullcycle.hexagonal.infrastructure.dtos.NewCustomerDTO;
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
@RequestMapping(value = "customers")
public class CustomerController {
  private final CreateCustomerUseCase createCustomerUseCase;
  private final GetCustomerUseCase getCustomerUseCase;

  public CustomerController(final CreateCustomerUseCase createCustomerUseCase,
                            final GetCustomerUseCase getCustomerUseCase) {
    this.createCustomerUseCase = Objects.requireNonNull(createCustomerUseCase);
    this.getCustomerUseCase = Objects.requireNonNull(getCustomerUseCase);
  }

  @PostMapping
  public ResponseEntity<?> create(@RequestBody NewCustomerDTO dto) {
    try {
      final var output = createCustomerUseCase.execute(
          new CreateCustomerUseCase.Input(dto.name(), dto.email(), dto.cpf()));

      return ResponseEntity.created(URI.create("/customers/" + output.id())).body(output);
    } catch (ValidationException ex) {
      return ResponseEntity.unprocessableEntity().body(ex.getMessage());
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> get(@PathVariable Long id) {
    return getCustomerUseCase.execute(new GetCustomerUseCase.Input(id))
        .map(ResponseEntity::ok)
        .orElseGet(ResponseEntity.notFound()::build);
  }
}