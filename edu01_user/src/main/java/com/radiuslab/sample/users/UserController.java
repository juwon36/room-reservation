package com.radiuslab.sample.users;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radiuslab.sample.users.dto.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
	@Autowired
	private UserService userService;

	@PostMapping
	public ResponseEntity<Object> save(@Valid @RequestBody UserSave userDto, Errors errors) {
		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors);
		}
		UserItem user = this.userService.save(userDto);

		URI uri = linkTo(UserController.class).slash(user.getId()).toUri();
		return ResponseEntity.created(uri).body(user);
	}

	@PostMapping(value = "/login")
	public ResponseEntity<Object> login(@Valid @RequestBody UserLogin userDto, HttpSession session, Errors errors) {
		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors);
		}
		UserItem user = this.userService.login(userDto);
		if (user == null) {
			errors.reject("LoginFailed", "이메일 혹은 비밀번호가 일치하지 않습니다. 입력한 내용을 다시 확인해 주세요.");
			return ResponseEntity.badRequest().body(errors);
		}
		session.setAttribute("login", user);

		URI uri = linkTo(UserController.class).slash(user.getId()).toUri();
		return ResponseEntity.created(uri).body(user);
	}

	@PutMapping
	public ResponseEntity<Object> update(@Valid @RequestBody UserUpdate userDto, HttpSession session, Errors errors) {
		if (session.getAttribute("login") == null) {
			errors.reject("SessionNull", "로그인이 필요한 서비스입니다.");
			return ResponseEntity.badRequest().body(errors);
		}

		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors);
		}
		UserItem login = (UserItem) session.getAttribute("login");
		userDto.setId(login.getId());
		userDto.setEmail(login.getEmail());
		UserItem user = this.userService.update(userDto);
		session.setAttribute("login", user);

		URI uri = linkTo(UserController.class).slash(user.getId()).toUri();
		return ResponseEntity.created(uri).body(user);
	}

	@GetMapping
	public ResponseEntity<Object> findAll(HttpSession session) {
		if (session.getAttribute("login") == null) {
			return ResponseEntity.badRequest().body("로그인이 필요한 서비스입니다.");
		}

		List<UserItem> users = this.userService.findAll();
		return ResponseEntity.ok().body(users);
	}

	@GetMapping("/logout")
	public ResponseEntity<String> logout(HttpSession session) {
		session.invalidate();
		return new ResponseEntity<String>("로그아웃 되셨습니다.", HttpStatus.OK);
	}

	@DeleteMapping
	public ResponseEntity<User> delete(HttpSession session) {
		UserItem login = (UserItem) session.getAttribute("login");
		this.userService.delete(login);
		session.invalidate();
		return new ResponseEntity<User>(HttpStatus.CREATED);
	}
}
