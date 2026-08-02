package com.nexus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostForm {

    @NotBlank(message = "Post content is required")
    @Size(max = 2000, message = "Post content must be at most 2000 characters")
    private String content;

    private MultipartFile image;
}
