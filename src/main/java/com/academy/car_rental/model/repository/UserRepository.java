package com.academy.car_rental.model.repository;

import com.academy.car_rental.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Page<User> findAll(Pageable pageable);

    User findByUsername(String username);

    User save(User user);

    Long countById(Integer id);

    User findUserByEmail(String email);

    User findByVerificationCode(String verificationCode);

}
