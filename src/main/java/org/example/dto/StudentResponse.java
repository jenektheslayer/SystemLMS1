package org.example.dto;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentResponse {
    private Long id;
    private String name;
    private String surname;
    private Long groupId;

}
