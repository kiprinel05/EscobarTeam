package org.example.exception;

public class ArtistNotFoundException extends RuntimeException {
    public ArtistNotFoundException(String message) {
        super(message);
    }
    
    public ArtistNotFoundException(Long id) {
        super("Artistul cu ID " + id + " nu a fost gasit");
    }
}
