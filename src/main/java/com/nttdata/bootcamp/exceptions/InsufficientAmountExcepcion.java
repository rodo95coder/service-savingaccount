package com.nttdata.bootcamp.exceptions;


public class InsufficientAmountExcepcion extends InterruptedException {
  public InsufficientAmountExcepcion(String message) {
    super(message);
  }
}