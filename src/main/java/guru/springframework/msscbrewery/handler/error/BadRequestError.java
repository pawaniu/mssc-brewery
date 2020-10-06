package guru.springframework.msscbrewery.handler.error;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BadRequestError extends ErrorDetails {

    public BadRequestError(String message) {
        this.setMessage(message);
        this.setDomain("global");
        this.setReason("badRequest");
    }
}
