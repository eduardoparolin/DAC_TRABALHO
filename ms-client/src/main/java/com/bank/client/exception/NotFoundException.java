package com.bank.client.exception;

/** Exceção de recurso não encontrado (mapeada para HTTP 404 no handler). */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}
