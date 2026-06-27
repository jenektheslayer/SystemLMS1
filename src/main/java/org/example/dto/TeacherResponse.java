package org.example.dto;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherResponse {
    @Id
    private Long id;
    private String name;
    private String surname;

}
