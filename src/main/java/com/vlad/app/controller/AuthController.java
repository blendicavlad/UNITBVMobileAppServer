package com.vlad.app.controller;

import com.vlad.app.exception.BadRequestException;
import com.vlad.app.exception.ResourceNotFoundException;
import com.vlad.app.model.AuthProvider;
import com.vlad.app.model.Professor;
import com.vlad.app.model.Student;
import com.vlad.app.model.User;
import com.vlad.app.payload.ApiResponse;
import com.vlad.app.payload.AuthResponse;
import com.vlad.app.payload.LoginRequest;
import com.vlad.app.payload.SignUpRequest;
import com.vlad.app.repository.GroupRepository;
import com.vlad.app.repository.ProfessorRepository;
import com.vlad.app.repository.StudentRepository;
import com.vlad.app.repository.UserRepository;
import com.vlad.app.security.TokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;

	private final UserRepository userRepository;

	private final ProfessorRepository professorRepository;

	private final StudentRepository studentRepository;

	private final GroupRepository groupRepository;

	private final PasswordEncoder passwordEncoder;

	private final TokenProvider tokenProvider;

	public AuthController(AuthenticationManager authenticationManager,
			UserRepository userRepository, ProfessorRepository professorRepository,
			StudentRepository studentRepository, GroupRepository groupRepository,
			PasswordEncoder passwordEncoder,
			TokenProvider tokenProvider) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.professorRepository = professorRepository;
		this.studentRepository = studentRepository;
		this.groupRepository = groupRepository;
		this.passwordEncoder = passwordEncoder;
		this.tokenProvider = tokenProvider;
	}

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		var authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginRequest.email(),
						loginRequest.password()
				)
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		var token = tokenProvider.createToken(authentication);
		return ResponseEntity.ok(new AuthResponse(token));
	}

	@PostMapping("/signup")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
		if (userRepository.existsByEmail(signUpRequest.email())) {
			throw new BadRequestException("Email address already in use.");
		}

		var user = new User(
				signUpRequest.firstName(),
				signUpRequest.lastName(),
				signUpRequest.email(),
				passwordEncoder.encode(signUpRequest.password()),
				AuthProvider.local,
				signUpRequest.userType()
		);

		var childEntity = switch (user.getUserType()) {
			case ADMIN -> null;
			case STUDENT -> new Student();
			case PROFESSOR -> new Professor();
		};

		Long result;

		if (childEntity instanceof Professor prof) {
			prof.setCNP(signUpRequest.CNP());
			prof.setUser(user);
			result = professorRepository.save(prof).getId();
		} else if (childEntity instanceof Student stud) {
			stud.setCNP(signUpRequest.CNP());
			stud.setUser(user);
			stud.setGroup(groupRepository.findById(signUpRequest.Group())
					.orElseThrow(() -> new ResourceNotFoundException("Group", "ID", signUpRequest.Group())));
			result = studentRepository.save(stud).getId();
		} else {
			user = userRepository.save(user);
			result = user.getId();
		}

		if (result == null || result == 0L) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse(false, "Could not create user"));
		}

		Authentication auth;
		try {
			auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							signUpRequest.email(),
							signUpRequest.password()
					)
			);
			SecurityContextHolder.getContext().setAuthentication(auth);
			return ResponseEntity.ok(new AuthResponse(tokenProvider.createToken(auth)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse(false, "User registered successfully"));
		}
	}
}
