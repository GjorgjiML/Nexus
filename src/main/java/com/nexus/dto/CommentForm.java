package com.nexus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentForm {

    @NotBlank(message = "Comment cannot be empty")
    @Size(max = 1000, message = "Comment must be at most 1000 characters")
    private String content;
}
