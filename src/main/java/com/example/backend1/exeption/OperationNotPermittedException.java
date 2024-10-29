package com.example.backend1.exeption;

public class OperationNotPermittedException  extends RuntimeException  {

    public OperationNotPermittedException() {
    }

    public OperationNotPermittedException(String message) {
        super(message);
    }
}
