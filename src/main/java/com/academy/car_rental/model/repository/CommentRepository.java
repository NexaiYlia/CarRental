package com.academy.car_rental.model.repository;

import com.academy.car_rental.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Long countById(Integer id);
}
