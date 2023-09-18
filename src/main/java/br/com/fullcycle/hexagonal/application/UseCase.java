package br.com.fullcycle.hexagonal.application;

public abstract class UseCase<IN, OUT> {
  public abstract OUT execute(IN input);
}
