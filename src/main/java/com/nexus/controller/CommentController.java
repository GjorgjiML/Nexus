package com.nexus.controller;

import com.nexus.dto.CommentForm;
import com.nexus.dto.CommentView;
import com.nexus.security.UserPrincipal;
import com.nexus.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/post/{postId}/comments")
    public String addCommentForm(
            @PathVariable Long postId,
            @Valid @ModelAttribute("commentForm") CommentForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Comment cannot be empty");
            return "redirect:/post/" + postId;
        }
        UserPrincipal current = CurrentUser.require();
        commentService.addComment(postId, current.getId(), form);
        return "redirect:/post/" + postId;
    }

    @PostMapping("/api/posts/{postId}/comments")
    @ResponseBody
    public ResponseEntity<?> addCommentApi(
            @PathVariable Long postId,
            @Valid @ModelAttribute CommentForm form,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Comment cannot be empty"));
        }
        UserPrincipal current = CurrentUser.require();
        CommentView view = commentService.addComment(postId, current.getId(), form);
        return ResponseEntity.ok(view);
    }

    @PostMapping("/comment/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        UserPrincipal current = CurrentUser.require();
        commentService.delete(id, current.getId());
        redirectAttributes.addFlashAttribute("success", "Comment deleted");
        return "redirect:/";
    }

    @PostMapping("/api/comments/{id}/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteApi(@PathVariable Long id) {
        UserPrincipal current = CurrentUser.require();
        commentService.delete(id, current.getId());
        return ResponseEntity.ok(Map.of("deleted", true));
    }
}
