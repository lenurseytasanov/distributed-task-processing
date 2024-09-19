package edu.java.distributedfileprocessing.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ProcessFileTask implements Serializable {

    @NotNull @Min(0)
    private Long reportId;

    @NotBlank
    private String fileId;

    @NotNull @Email
    private String userEmail;

}
