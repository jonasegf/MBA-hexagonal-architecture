package br.com.fullcycle.hexagonal.controllers;

import br.com.fullcycle.hexagonal.application.usecases.CreateCustomerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.GetCustomerUseCase;
import br.com.fullcycle.hexagonal.dtos.CustomerDTO;
import br.com.fullcycle.hexagonal.exception.ValidationException;
import br.com.fullcycle.hexagonal.services.CustomerService;
import java.net.URI;
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

  private final CustomerService customerService;

  public CustomerController(final CustomerService customerService) {
    this.customerService = customerService;
  }

  @PostMapping
  public ResponseEntity<?> create(@RequestBody CustomerDTO dto) {
    try {
      final var useCase = new CreateCustomerUseCase(customerService);
      final var output = useCase.execute(
          new CreateCustomerUseCase.Input(dto.getName(), dto.getEmail(), dto.getCpf()));

      return ResponseEntity.created(URI.create("/customers/" + output.id())).body(output);
    } catch (ValidationException ex) {
      return ResponseEntity.unprocessableEntity().body(ex.getMessage());
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> get(@PathVariable Long id) {
    final var useCase = new GetCustomerUseCase(customerService);
    return useCase.execute(new GetCustomerUseCase.Input(id))
        .map(ResponseEntity::ok)
        .orElseGet(ResponseEntity.notFound()::build);
  }
}