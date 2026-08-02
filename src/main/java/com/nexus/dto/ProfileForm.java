package com.nexus.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProfileForm {

    @Size(max = 500, message = "Bio must be at most 500 characters")
    private String bio;

    private MultipartFile avatar;
}
