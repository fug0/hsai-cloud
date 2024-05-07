package hsai.edu.server.abstraction.service;

import hsai.edu.server.abstraction.service_interfaces.UserServiceInterface;
import hsai.edu.server.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService implements UserServiceInterface {

	@Autowired
	UserRepo userRepo;

	@Override
	public Mono<UserDto> getById(Long id) {
		return userRepo
			.findById(id)
			.map(UserDto::fromDbEntity);
	}

	@Override
	public Mono<UserDto> getByName(String name) {
		return userRepo
				.findByName(name)
				.map(UserDto::fromDbEntity);
	}

	@Override
	public Mono<Long> addUser(UserServiceInterface.AddUserDto addUserDto) {
		return userRepo
				.save(UserServiceInterface.AddUserDto.toDbEntity(addUserDto))
				.map(UserRepo.User::id);
	}

	@Override
	public Mono<UserDto> signIn(UserServiceInterface.SignInDto signInDto) {
		return userRepo
				.signIn(signInDto.login(), signInDto.password())
				.map(UserDto::fromDbEntity);
	}
}
