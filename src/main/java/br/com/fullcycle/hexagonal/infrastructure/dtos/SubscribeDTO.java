package br.com.fullcycle.hexagonal.infrastructure.dtos;

public record SubscribeDTO(
    Long eventId,
    Long customerId) {
}
