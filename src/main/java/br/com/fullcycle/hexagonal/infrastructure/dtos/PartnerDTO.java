package br.com.fullcycle.hexagonal.infrastructure.dtos;

public record PartnerDTO(
    String name,
    String cnpj,
    String email) {
}
