package scheduled_file_dispatch.v1.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private UUID id;
    private String nome;
    private String email;
    private String senhaHash;
}
