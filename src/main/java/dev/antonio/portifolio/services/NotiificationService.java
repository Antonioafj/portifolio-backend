package dev.antonio.portifolio.services;

public interface NotiificationService {

    void send(String destination, String subject, String body);

}
