package pl.konradboniecki.budget.mail.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Family {

    private Long id;
    private String title;
}
