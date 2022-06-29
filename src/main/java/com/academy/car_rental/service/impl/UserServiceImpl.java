package com.academy.car_rental.service.impl;

import com.academy.car_rental.dto.UserDto;
import com.academy.car_rental.exception.ServiceException;
import com.academy.car_rental.model.entity.Order;
import com.academy.car_rental.model.entity.User;
import com.academy.car_rental.model.entity.type.Role;
import com.academy.car_rental.model.repository.UserRepository;
import com.academy.car_rental.service.EmailService;
import com.academy.car_rental.service.OrderService;
import com.academy.car_rental.service.UUIDService;
import com.academy.car_rental.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.academy.car_rental.constant.Constants.LOCALHOST;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final OrderService orderService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UUIDService uuidService;
    ;

    public Page<User> findAll(int pageNumber, String sortField, String sortDirection) throws ServiceException {
        Sort sort = Sort.by(sortField);
        sort = sortDirection.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, 5, sort);
        var pageOFUsers = userRepository.findAll(pageable);
        if (pageOFUsers.getContent().isEmpty()) {
            throw new ServiceException("Wrong page");
        }
        return pageOFUsers;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getById(Integer id) throws ServiceException {
        Optional<User> result = userRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        }
        throw new ServiceException("User not found");
    }

    @Override
    public void save(UserDto userDto) throws MessagingException, UnsupportedEncodingException {
        User user = User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .verificationCode(createRandomCode())
                .verificationStatus(false)
                .password(passwordEncoder.encode(userDto.getPassword()))
                .phoneNumber(userDto.getPhoneNumber())
                .surname(userDto.getSurname())
                .build();
        emailService.sendEmail(user, LOCALHOST);
        List<Role> roles = new ArrayList<>();
        roles.add(Role.ROLE_USER);
        user.setRoles(roles);
        userRepository.save(user);

    }

    @Override
    public boolean checkHasUserOrder(Integer userId) {
        var user = userRepository.findById(userId).get();
        List<Order> orders = orderService.findByUser(user);
        if (orders.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public void update(UserDto userDto) throws MessagingException, UnsupportedEncodingException {
        User user = userRepository.findById(userDto.getId()).get();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setSurname(userDto.getSurname());
        user.setVerificationCode(createRandomCode());
        user.setVerificationStatus(false);
        emailService.sendEmail(user, LOCALHOST);
        List<Role> roles = new ArrayList<>();
        roles.add(Role.ROLE_USER);
        user.setRoles(roles);
        userRepository.save(user);
    }


    public void makeAdmin(User user) throws MessagingException, UnsupportedEncodingException {
        List<Role> roles = new ArrayList<>();
        roles.add(Role.ROLE_ADMIN);
        user.setRoles(roles);
        userRepository.save(user);
        emailService.sendEmailAboutChangeStatus(user);
    }

    @Override
    public User findByVerificationCode(String verificationCode) {
        return userRepository.findByVerificationCode(verificationCode);
    }


    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public void delete(Integer id) throws ServiceException {
        Long count = userRepository.countById(id);
        if (count == null || count == 0) {
            throw new ServiceException("Could not find any users with id: " + id);

        }
        userRepository.deleteById(id);
    }

    public boolean saveAndFlush(User item) {
        userRepository.saveAndFlush(item);
        return true;
    }


    @Transactional
    public boolean checkAndChangeVerificationStatus(String code) {
        var user = findByVerificationCode(code);
        if (user == null || user.getVerificationStatus()) {
            return false;
        } else {
            user.setVerificationStatus(true);
            List<Role> roles = new ArrayList<>();
            roles.add(Role.ROLE_USER);
            user.setRoles(roles);
            saveAndFlush(user);
            return true;
        }
    }

    @Transactional
    public boolean updatePasswordOfUser(Integer userId, String password) throws ServiceException {
        var updatedUser = getById(userId);
        updatedUser.setPassword(passwordEncoder.encode(password));
        saveAndFlush(updatedUser);
        return true;
    }

    private String createRandomCode() {
        return uuidService.getRandomString();
    }
}
