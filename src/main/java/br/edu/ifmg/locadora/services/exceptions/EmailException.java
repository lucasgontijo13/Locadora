package br.edu.ifmg.locadora.services.exceptions;

public class EmailException extends RuntimeException {
    public EmailException(String message) {
        super(message);
    }
}