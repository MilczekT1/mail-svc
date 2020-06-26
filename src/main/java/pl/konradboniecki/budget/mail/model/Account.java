package pl.konradboniecki.budget.mail.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@NoArgsConstructor
public class Account {

    private String firstName;
    private String lastName;
    private String email;
    private Long id;
}
