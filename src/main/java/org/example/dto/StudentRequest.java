package org.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentRequest {
    private String name;
    private String surname;
    private Long groupId;
}
