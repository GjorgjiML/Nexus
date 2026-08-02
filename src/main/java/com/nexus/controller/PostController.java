package com.nexus.controller;

import com.nexus.dto.LikeResponse;
import com.nexus.dto.PostForm;
import com.nexus.exception.BadRequestException;
import com.nexus.security.UserPrincipal;
import com.nexus.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/post/create")
    public String createForm(Model model) {
        model.addAttribute("postForm", new PostForm());
        return "post-create";
    }

    @PostMapping("/post/create")
    public String create(
            @Valid @ModelAttribute("postForm") PostForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "post-create";
        }
        UserPrincipal current = CurrentUser.require();
        try {
            postService.create(current.getId(), form);
            redirectAttributes.addFlashAttribute("success", "Post published");
            return "redirect:/";
        } catch (BadRequestException ex) {
            model.addAttribute("error", ex.getMessage());
            return "post-create";
        }
    }

    @PostMapping("/post/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        UserPrincipal current = CurrentUser.require();
        postService.delete(id, current.getId());
        redirectAttributes.addFlashAttribute("success", "Post deleted");
        return "redirect:/";
    }

    @PostMapping("/api/posts/{id}/like")
    @ResponseBody
    public ResponseEntity<LikeResponse> toggleLike(@PathVariable Long id) {
        UserPrincipal current = CurrentUser.require();
        return ResponseEntity.ok(postService.toggleLike(id, current.getId()));
    }

    @GetMapping("/post/{id}")
    public String view(@PathVariable Long id, Model model) {
        Long currentUserId = CurrentUser.idOrNull();
        model.addAttribute("post", postService.getPost(id, currentUserId));
        return "post-detail";
    }
}
