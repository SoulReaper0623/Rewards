package org.visa.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Request body for creating a new member")
public class MemberRequest {

    @NotBlank(message = "name must not be blank")
    @Schema(description = "Full name of the member", example = "Alice Johnson")
    public String name;

    @NotBlank(message = "email must not be blank")
    @Schema(description = "Email address (must be unique)", example = "alice@example.com")
    public String email;
}
