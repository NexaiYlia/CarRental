package com.academy.car_rental.service.impl;

import com.academy.car_rental.exception.ServiceException;
import com.academy.car_rental.model.entity.Comment;
import com.academy.car_rental.model.repository.CommentRepository;
import com.academy.car_rental.model.repository.UserRepository;
import com.academy.car_rental.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Override
    public List<Comment> getAll() {
        return commentRepository.findAll();
    }

    @Override
    public Comment getById(Integer id) throws ServiceException {
        Comment result = commentRepository.findById(id).get();
        return result;

    }

    @Override
    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public void delete(Integer id) {
        commentRepository.delete(commentRepository.findById(id).get());
    }

    @Override
    public void createNewComment(String context, Principal principal) {
        var user = userRepository.findByUsername(principal.getName());
        Comment comment = Comment.builder()
                .user(user)
                .content(context)
                .date(LocalDate.now())
                .build();

        save(comment);
    }

    @Transactional
    public boolean saveUpdatedComment(String content, Integer commentId, Integer userId) throws ServiceException {
        var updatedComment = getById(commentId);
        updatedComment.setContent(content);
        updatedComment.setDate(LocalDate.now());
        updatedComment.setUser(userRepository.findById(userId).get());
        commentRepository.save(updatedComment);

        return true;
    }
}
