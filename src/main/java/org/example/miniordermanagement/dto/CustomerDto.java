package org.example.miniordermanagement.dto;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CustomerDto {
    private String name;
    private String email;

    public CustomerDto(String email, String name) {
        this.name = name;
        this.email = email;
    }

}
