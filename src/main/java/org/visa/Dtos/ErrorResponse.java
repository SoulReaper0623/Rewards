package org.visa.Dtos;

import lombok.*;

import java.time.Instant;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    public int status;
    public String error;
    public String message;
    public Instant timestamp;
}
