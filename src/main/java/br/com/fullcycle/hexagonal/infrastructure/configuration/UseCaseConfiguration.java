package br.com.fullcycle.hexagonal.infrastructure.configuration;


import br.com.fullcycle.hexagonal.application.usecases.CreateCustomerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.CreateEventUseCase;
import br.com.fullcycle.hexagonal.application.usecases.CreatePartnerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.GetCustomerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.GetPartnerByIdUseCase;
import br.com.fullcycle.hexagonal.application.usecases.SubscribeCustomerToEventUseCase;
import br.com.fullcycle.hexagonal.infrastructure.services.CustomerService;
import br.com.fullcycle.hexagonal.infrastructure.services.EventService;
import br.com.fullcycle.hexagonal.infrastructure.services.PartnerService;
import java.util.Objects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfiguration {
  private final CustomerService customerService;
  private final EventService eventService;
  private final PartnerService partnerService;

  public UseCaseConfiguration(final CustomerService customerService,
                              final EventService eventService,
                              final PartnerService partnerService) {
    this.customerService = Objects.requireNonNull(customerService);
    this.eventService = Objects.requireNonNull(eventService);
    this.partnerService = Objects.requireNonNull(partnerService);
  }

  @Bean
  public CreateCustomerUseCase createCustomerUseCase() {
    return new CreateCustomerUseCase(customerService);
  }

  @Bean
  public GetCustomerUseCase getCustomerUseCase() {
    return new GetCustomerUseCase(customerService);
  }

  @Bean
  public CreateEventUseCase createEventUseCase() {
    return new CreateEventUseCase(eventService, partnerService);
  }

  @Bean
  public SubscribeCustomerToEventUseCase subscribeCustomerToEventUseCase() {
    return new SubscribeCustomerToEventUseCase(customerService, eventService);
  }

  @Bean
  public CreatePartnerUseCase createPartnerUseCase() {
    return new CreatePartnerUseCase(partnerService);
  }

  @Bean
  public GetPartnerByIdUseCase getPartnerByIdUseCase() {
    return new GetPartnerByIdUseCase(partnerService);
  }

}
