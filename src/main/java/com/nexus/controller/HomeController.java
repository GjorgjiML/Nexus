package com.nexus.controller;

import com.nexus.dto.PostForm;
import com.nexus.dto.PostView;
import com.nexus.security.UserPrincipal;
import com.nexus.service.PostService;
import com.nexus.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PostService postService;
    private final UserService userService;

    @GetMapping("/")
    public String home(
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        UserPrincipal current = CurrentUser.require();
        Page<PostView> feed = postService.getFeed(current.getId(), page, 20);
        model.addAttribute("posts", feed.getContent());
        model.addAttribute("page", feed);
        model.addAttribute("postForm", new PostForm());
        model.addAttribute("currentUser", userService.toView(userService.getById(current.getId()), current.getId()));
        return "index";
    }
}
