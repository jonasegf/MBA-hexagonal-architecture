package br.com.fullcycle.hexagonal.infrastructure.services;

import br.com.fullcycle.hexagonal.infrastructure.models.Partner;
import br.com.fullcycle.hexagonal.infrastructure.repositories.PartnerRepository;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartnerService {

    private final PartnerRepository repository;

    public PartnerService(final PartnerRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Transactional
    public Partner save(Partner customer) {
        return repository.save(customer);
    }

    public Optional<Partner> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Partner> findByCnpj(String cnpj) {
        return repository.findByCnpj(cnpj);
    }

    public Optional<Partner> findByEmail(String email) {
        return repository.findByEmail(email);
    }

}
