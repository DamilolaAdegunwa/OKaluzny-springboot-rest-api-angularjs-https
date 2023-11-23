package com.kaluzny.service;

import com.kaluzny.domain.User;
import com.kaluzny.domain.UserRepository;
import com.kaluzny.service.exception.UserAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Service
@Validated
@Transactional
public class UserServiceBean implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceBean.class);

    private UserRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    public UserServiceBean(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isUserExist(User user) {
        return findUserById(user.getId()) != null;
    }

    @Override
    public User saveUser(User user) {
        LOGGER.debug("Save {}", user);
        //User existing = repository.findOne(user.getId());
        Optional<User> existing = repository.findById(user.getId());
        if (!existing.isPresent()) {
            throw new UserAlreadyExistsException(
                    String.format("There already exists a user with id = %s", user.getId()));
        }
        return repository.save(user);
    }

    @Override
    public User findUserById(long id) {
        LOGGER.debug("Search user by id: " + id);
        //return repository.findOne(id);
        return repository.findById(id).get();
    }

    @Override
    public List<User> findAllUsers() {
        LOGGER.debug("Retrieve the list of all users!");
        return repository.findAll();
    }

    @Override
    public User updateUser(User user) {
        LOGGER.debug("User with id: " + user.getId() + " updated! ");
        if (!entityManager.contains(user))
            user = entityManager.merge(user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        LOGGER.debug("User by id: " + id + " Deleted!");
        repository.deleteById(id);
    }

    @Override
    public void deleteAllUsers() {
        LOGGER.debug("The list all users deleted!");
        repository.deleteAll();
    }
}