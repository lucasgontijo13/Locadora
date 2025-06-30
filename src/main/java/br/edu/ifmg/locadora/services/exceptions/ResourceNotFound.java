package br.edu.ifmg.locadora.services.exceptions;

public class ResourceNotFound extends RuntimeException {

    public ResourceNotFound() {
        super();
    }

    public ResourceNotFound(String message) {
        super(message);
    }
}

