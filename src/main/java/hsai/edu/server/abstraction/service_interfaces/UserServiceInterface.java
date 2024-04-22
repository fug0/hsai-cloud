package hsai.edu.server.abstraction.service_interfaces;

import hsai.edu.server.repository.UserRepo;
import reactor.core.publisher.Mono;

public interface UserServiceInterface {
	Mono<UserDto> getById(Long id);
	Mono<UserDto> getByName(String name);
	Mono<Long> addUser(UserServiceInterface.AddUserDto addUserDto);
	Mono<UserDto> signIn(UserServiceInterface.SignInDto signInDto);

	record UserDto(
		Long id,
		String name,
		String login,
		String password,
		Long chatId
	){
		public static UserDto fromDbEntity(UserRepo.User user){
			return new UserDto(
				user.id(),
				user.name(),
				user.login(),
				user.password(),
				user.chatId()
			);
		}
	}

	record AddUserDto(
			Long id,
			String name,
			String login,
			String password,
			Long chatId
	){
		public static UserRepo.User toDbEntity(UserServiceInterface.AddUserDto addUserDto){
			return new UserRepo.User(
					addUserDto.id(),
					addUserDto.name(),
					addUserDto.login(),
					addUserDto.password(),
					addUserDto.chatId()
			);
		}
	}

	record SignInDto(
			String login,
			String password
	){}
}
