package hsai.edu.server.controller;

import hsai.edu.server.abstraction.service_interfaces.EventServiceInterface;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@RestController
public record EventController(
	EventServiceInterface eventService
) {
	@GetMapping("/getEvent/{id}")
	public Mono<EventServiceInterface.EventDto> findById(@PathVariable Long id){
		return eventService.getById(id);
	}

	@GetMapping("/getEvents/{datetime}")
	public Flux<EventServiceInterface.EventDto> findByDatetime(@PathVariable Instant datetime){
		return eventService.getByDatetime(datetime);
	}

	@GetMapping("/getEvents/{type}")
	public Mono<List<EventServiceInterface.EventDto>> findByType(@PathVariable Integer type, @PathVariable Long chatId){
		return eventService.getByType(type, chatId);
	}

	@GetMapping("/getNext")
	public Mono<EventServiceInterface.EventDto> findNext(){
		return eventService.getNext();
	}

	@GetMapping("/getDay")
	public Mono<List<EventServiceInterface.EventDto>> findDay(){
		return eventService.getDay(Instant.now());
	}

	@GetMapping("/getWeek")
	public Mono<List<EventServiceInterface.EventDto>> findWeek(){
		return eventService.getWeek(Instant.now());
	}

	@PostMapping("/addEvent")
	public Mono<Long> addEvent(@RequestBody EventServiceInterface.AddEventDto addEventDto) {return eventService.addEvent(addEventDto);}

	@DeleteMapping("/deleteEvent/{id}")
	public Mono<Void> deleteById(@PathVariable Long id) {return eventService.deleteEvent(id);}

	@PostMapping("/updateEvent/{id}")
	public Mono<Long> updateById(@RequestBody EventServiceInterface.EditEventDto editEventDto, @PathVariable Long id) {return eventService.updateEvent(editEventDto, id);}
}