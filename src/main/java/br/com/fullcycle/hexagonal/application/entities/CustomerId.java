package br.com.fullcycle.hexagonal.application.entities;

import java.util.UUID;

public record CustomerId(UUID value) {
  public static CustomerId unique(){
    return new CustomerId(UUID.randomUUID());

  }
}
