package com.academy.car_rental.controller;

import com.academy.car_rental.exception.ServiceException;
import com.academy.car_rental.model.entity.Comment;
import com.academy.car_rental.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import static com.academy.car_rental.constant.Constants.*;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/comments")
    public String showCommentList(Model model) {

        List<Comment> comments = commentService.getAll();
        model.addAttribute(COMMENTS_FOR_MODEL, comments);

        return "comment/comments";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/comments/new")
    public String showNewForm(Model model) {
        model.addAttribute(COMMENT_FOR_MODEL, new Comment());
        model.addAttribute(PAGE_TITLE_FOR_MODEL, "Add new comment");

        return "comment/comment_form";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(value = "/comments/createComment")
    public String createComment(@RequestParam("content") String content, Principal principal, RedirectAttributes ra) {
        commentService.createNewComment(content, principal);
        ra.addFlashAttribute(MESSAGE, "The comment has been added successfully.");
        return "redirect:/comments";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/comments/save")
    public String saveComment(Comment comment, Principal principal, RedirectAttributes ra) {
        commentService.save(comment);
        ra.addFlashAttribute(MESSAGE, "The comment has been saved successfully.");
        return "redirect:comment/comments";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/comments/delete/{id}")
    public String deleteComment(@PathVariable("id") Integer id, RedirectAttributes ra) {
        commentService.delete(id);
        ra.addFlashAttribute(MESSAGE, "The comment was deleted successfully.");
        return "redirect:/comments";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/comments/edit/{id}")
    public String showEditCommentForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Comment comment = commentService.getById(id);
            model.addAttribute(COMMENT_FOR_MODEL, comment);
            model.addAttribute(PAGE_TITLE_FOR_MODEL, "Edit comment (id: " + id + ")");
            return "comment/edit";
        } catch (ServiceException e) {
            ra.addFlashAttribute(MESSAGE, "The comment wasn't found." + e.getMessage());
            return "redirect:/comments";
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(value = "/comments/update")
    public String updateComment(@RequestParam("content") String content, Integer commentId, Integer userId, Principal principal,  RedirectAttributes ra) throws ServiceException, IOException {

        commentService.saveUpdatedComment(content, commentId,userId);

        ra.addFlashAttribute(MESSAGE, "The car has been saved successfully.");
        return "redirect:/comments";
    }
}

