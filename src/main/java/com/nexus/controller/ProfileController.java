package com.nexus.controller;

import com.nexus.dto.ProfileForm;
import com.nexus.dto.UserView;
import com.nexus.entity.User;
import com.nexus.exception.BadRequestException;
import com.nexus.security.UserPrincipal;
import com.nexus.service.FollowService;
import com.nexus.service.PostService;
import com.nexus.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final PostService postService;
    private final FollowService followService;

    @GetMapping("/profile/{username}")
    public String profile(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Long currentUserId = CurrentUser.idOrNull();
        User user = userService.getByUsername(username);
        UserView profile = userService.toView(user, currentUserId);
        Page<?> posts = postService.getProfilePosts(username, currentUserId, page, 20);

        model.addAttribute("profile", profile);
        model.addAttribute("posts", posts.getContent());
        model.addAttribute("page", posts);
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String editForm(Model model) {
        UserPrincipal current = CurrentUser.require();
        User user = userService.getById(current.getId());
        ProfileForm form = new ProfileForm();
        form.setBio(user.getBio());
        model.addAttribute("profileForm", form);
        model.addAttribute("profile", userService.toView(user, current.getId()));
        return "profile-edit";
    }

    @PostMapping("/profile/edit")
    public String edit(
            @Valid @ModelAttribute("profileForm") ProfileForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        UserPrincipal current = CurrentUser.require();
        if (bindingResult.hasErrors()) {
            model.addAttribute("profile", userService.toView(userService.getById(current.getId()), current.getId()));
            return "profile-edit";
        }
        try {
            User updated = userService.updateProfile(current.getId(), form);
            redirectAttributes.addFlashAttribute("success", "Profile updated");
            return "redirect:/profile/" + updated.getUsername();
        } catch (BadRequestException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("profile", userService.toView(userService.getById(current.getId()), current.getId()));
            return "profile-edit";
        }
    }

    @PostMapping("/profile/{username}/follow")
    public String follow(@PathVariable String username) {
        UserPrincipal current = CurrentUser.require();
        followService.follow(current.getId(), username);
        return "redirect:/profile/" + username;
    }

    @PostMapping("/profile/{username}/unfollow")
    public String unfollow(@PathVariable String username) {
        UserPrincipal current = CurrentUser.require();
        followService.unfollow(current.getId(), username);
        return "redirect:/profile/" + username;
    }

    @GetMapping("/profile/{username}/followers")
    public String followers(@PathVariable String username, Model model) {
        Long currentUserId = CurrentUser.idOrNull();
        model.addAttribute("profile", userService.toView(userService.getByUsername(username), currentUserId));
        model.addAttribute("users", userService.listFollowers(username, currentUserId));
        model.addAttribute("listTitle", "Followers");
        return "user-list";
    }

    @GetMapping("/profile/{username}/following")
    public String following(@PathVariable String username, Model model) {
        Long currentUserId = CurrentUser.idOrNull();
        model.addAttribute("profile", userService.toView(userService.getByUsername(username), currentUserId));
        model.addAttribute("users", userService.listFollowing(username, currentUserId));
        model.addAttribute("listTitle", "Following");
        return "user-list";
    }
}
