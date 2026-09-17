package org.example.learn.java.spi;

public final class DefaultGreeter implements Greeter {
    @Override
    public String hello() {
        return "hello from SPI DefaultGreeter";
    }
}
