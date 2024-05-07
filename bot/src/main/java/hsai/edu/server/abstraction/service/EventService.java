package hsai.edu.server.abstraction.service;

import hsai.edu.server.abstraction.service_interfaces.EventServiceInterface;
import hsai.edu.server.repository.EventRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Service
public class EventService implements EventServiceInterface {

	@Autowired
	EventRepo eventRepo;

	@Override
	public Mono<EventDto> getById(Long id) {
		return eventRepo
				.findById(id)
				.map(EventDto::fromDbEntity);
	}

	@Override
	public Flux<EventDto> getByDatetime(Instant datetime) {
		return eventRepo
				.findByDatetime(datetime)
				.map(EventDto::fromDbEntity);
	}

	@Override
	public Mono<List<EventDto>> getByType(Integer type, Long chatId) {
		return eventRepo
				.findByType(type, chatId)
				.map(EventDto::fromDbEntity)
				.collectList();
	}

	@Override
	public Mono<EventDto> getNext() {
		return eventRepo
				.findNext()
				.map(EventDto::fromDbEntity);
	}

	@Override
	public Mono<List<EventDto>> getDay(Instant datetime) {
		return eventRepo
				.findAllByDatetimeLessThen(datetime.plusSeconds(86400))
				.map(EventDto::fromDbEntity)
				.collectList();
	}

	@Override
	public Mono<List<EventDto>> getWeek(Instant datetime) {
		return eventRepo
				.findAllByDatetimeLessThen(datetime.plusSeconds(604800))
				.map(EventDto::fromDbEntity)
				.collectList();
	}

	@Override
	public Mono<Long> addEvent(AddEventDto addEventDto) {
		return eventRepo
				.save(AddEventDto.toDbEntity(addEventDto))
				.map(EventRepo.Event::id);
	}

	@Override
	public Mono<Void> deleteEvent(Long id) {
		return eventRepo
				.deleteById(id);
	}

	@Override
	public Mono<Long> updateEvent(EditEventDto editEventDto, Long id) {
		return eventRepo
				.save(EditEventDto.toDbEntity(editEventDto, id))
				.map(EventRepo.Event::id);
	}
}
