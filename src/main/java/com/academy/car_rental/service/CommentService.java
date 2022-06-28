package com.academy.car_rental.service;

import com.academy.car_rental.exception.ServiceException;
import com.academy.car_rental.model.entity.Comment;

import java.security.Principal;
import java.util.List;

public interface CommentService {
    List<Comment> getAll();

    Comment getById(Integer id) throws ServiceException;


    void save(Comment comment);

    void delete(Integer id);

    void createNewComment(String context, Principal principal);

    boolean saveUpdatedComment(String content, Integer commentId, Integer userId) throws ServiceException;
}

