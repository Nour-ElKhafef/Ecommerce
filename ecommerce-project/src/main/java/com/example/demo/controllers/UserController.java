package com.example.demo.controllers;

import com.example.demo.controllers.exception.InvalidPasswordException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private final UserRepository userRepository;
	private final CartRepository cartRepository;
	private final UserService userService;

	private ModelMapper mapper = new ModelMapper();

	private Logger log = LoggerFactory.getLogger(UserController.class);

	public UserController(UserRepository userRepository,
						  CartRepository cartRepository,
						  UserService userService) {
		this.userRepository = userRepository;
		this.cartRepository = cartRepository;
		this.userService = userService;
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userService.findUser(username);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) throws Exception {
		userService.validatePassword(createUserRequest.getPassword(),
				createUserRequest.getConfirmPassword(),
				createUserRequest.getUsername());
		User user = mapper.map(createUserRequest, User.class);
		user = userService.generatePasswordAndPersistUser(user);

		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		return ResponseEntity.ok(user);
	}

}
