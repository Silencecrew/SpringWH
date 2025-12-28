package org.example.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Dto.CreateUserRequest;
import org.example.Dto.UpdateUserRequest;
import org.example.Dto.UserDto;
import org.example.Entity.User;
import org.example.Exception.DuplicateEmailException;
import org.example.Exception.UserNotFoundException;
import org.example.Exception.ValidationException;
import org.example.event.UserCreateEvent;
import org.example.event.UserDeleteEvent;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    private KafkaTemplate<String, UserCreateEvent> kafkaCreateTemplate;
    private KafkaTemplate<String, UserDeleteEvent> kafkaDeleteTemplate;
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public UserServiceImpl(UserRepository userRepository,KafkaTemplate<String, UserCreateEvent> kafkaCreateTemplate,KafkaTemplate<String, UserDeleteEvent> kafkaDeleteTemplate) {
        this.userRepository = userRepository;
        this.kafkaCreateTemplate=kafkaCreateTemplate;
        this.kafkaDeleteTemplate=kafkaDeleteTemplate;
    }

    @Override
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        logger.info("Создание нового пользователя: {}", request.getEmail());

        validateCreateRequest(request);

        if (userRepository.existsByUserEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email " + request.getEmail() + " уже используется");
        }

        String createdAt = LocalDateTime.now().format(DATE_FORMATTER);
        User user = new User(
                request.getName(),
                request.getAge(),
                request.getEmail(),
                createdAt
        );

        User savedUser = userRepository.save(user);

        logger.info("Пользователь успешно создан с ID: {}", savedUser.getUserId());

        UserCreateEvent userCreateEvent = new UserCreateEvent(savedUser.getUserId(),savedUser.getUserEmail());

        SendResult<String, UserCreateEvent> createResult;
        try {
             createResult = kafkaCreateTemplate
                    .send("user-created-event-topic", savedUser.getUserId().toString(), userCreateEvent).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("Topic: {}", createResult.getRecordMetadata().topic());
        logger.info("partition: {}", createResult.getRecordMetadata().partition());
        logger.info("Offset: {}", createResult.getRecordMetadata().offset());
        return convertToDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(int id) {
        logger.debug("Поиск пользователя по ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        logger.debug("Пользователь найден: {}", user.getUserName());
        return convertToDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        logger.debug("Получение списка всех пользователей");

        Iterable<User> users = userRepository.findAll();
        return StreamSupport.stream(users.spliterator(), false)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto updateUser(int id, UpdateUserRequest request) {
        logger.info("Обновление пользователя ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        validateUpdateRequest(request);

        boolean isUpdated = false;

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setUserName(request.getName().trim());
            isUpdated = true;
        }

        if (request.getAge() != null) {
            if (!validateAge(request.getAge())) {
                throw new ValidationException("Возраст должен быть от 0 до 80 лет");
            }
            user.setUserAge(request.getAge());
            isUpdated = true;
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String newEmail = request.getEmail().trim();
            if (!newEmail.equals(user.getUserEmail()) && userRepository.existsByUserEmail(newEmail)) {
                throw new DuplicateEmailException("Email " + newEmail + " уже используется");
            }
            user.setUserEmail(newEmail);
            isUpdated = true;
        }

        if (isUpdated) {
            userRepository.save(user);
            logger.info("Данные пользователя ID: {} обновлены", id);
        } else {
            logger.info("Данные пользователя ID: {} не изменились", id);
        }

        return convertToDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(int id) {
        logger.info("Удаление пользователя ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        String userEmail = user.getUserEmail();

        userRepository.deleteById(id);
        logger.info("Пользователь ID: {} успешно удален", id);

        UserDeleteEvent userDeleteEvent = new UserDeleteEvent(id, userEmail);
        String idTosend= String.valueOf(id);
        SendResult<String, UserDeleteEvent> deleteResult;
        try {
            deleteResult = kafkaDeleteTemplate
                    .send("user-delete-event-topic", idTosend, userDeleteEvent).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        logger.info("Topic: {}", deleteResult.getRecordMetadata().topic());
        logger.info("partition: {}", deleteResult.getRecordMetadata().partition());
        logger.info("Offset: {}", deleteResult.getRecordMetadata().offset());

    }

    @Override
    @Transactional(readOnly = true)
    public void printAllUsers() {
        List<UserDto> users = getAllUsers();

        System.out.println("\nСписок пользователей");
        if (users.isEmpty()) {
            System.out.println("Пользователей не найдено.");
        } else {
            users.forEach(user -> System.out.println(user.toString()));
        }
        System.out.println("_____________________________________\n");
    }

    @Override
    public boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    @Override
    public boolean validateAge(int age) {
        return age >= 0 && age <= 80;
    }

    private void validateCreateRequest(CreateUserRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ValidationException("Имя пользователя не может быть пустым");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email не может быть пустым");
        }

        if (!validateEmail(request.getEmail())) {
            throw new ValidationException("Некорректный формат email");
        }

        if (request.getAge() == null || !validateAge(request.getAge())) {
            throw new ValidationException("Возраст должен быть от 0 до 80 лет");
        }
    }

    private void validateUpdateRequest(UpdateUserRequest request) {
        if (request.getAge() != null && !validateAge(request.getAge())) {
            throw new ValidationException("Возраст должен быть от 0 до 80 лет");
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()
                && !validateEmail(request.getEmail())) {
            throw new ValidationException("Некорректный формат email");
        }
    }

    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getUserId(),
                user.getUserName(),
                user.getUserAge(),
                user.getUserEmail(),
                user.getUserCreatedAt()
        );
    }
}