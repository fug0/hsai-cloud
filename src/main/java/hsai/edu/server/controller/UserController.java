package hsai.edu.server.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import hsai.edu.server.abstraction.service_interfaces.UserServiceInterface;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public record UserController(
	UserServiceInterface userService
) {
	@GetMapping("/getUser/{id}")
	public Mono<UserServiceInterface.UserDto> findById(@PathVariable Long id){
		return userService.getById(id);
	}

	@GetMapping("/getUser/{name}")
	public Mono<UserServiceInterface.UserDto> findByName(@PathVariable String name){
		return userService.getByName(name);
	}

	@PostMapping("/signUp")
	public Mono<Long> signUp(@RequestBody UserServiceInterface.AddUserDto addUserDto) {return userService.addUser(addUserDto);}

	@PostMapping("/signIn")
	public Mono<UserServiceInterface.UserDto> signIn(@RequestBody UserServiceInterface.SignInDto signInDto, HttpServletResponse response) {
		return userService.signIn(signInDto)
				.doOnNext(user -> {
					var cookie = new Cookie("userId", String.valueOf(user.id()));
					cookie.setMaxAge(3600);
					cookie.setPath("/");
					response.addCookie(cookie);
				});
	}
}
