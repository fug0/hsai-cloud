package hsai.edu.client.bot;

import hsai.edu.client.Client;
import hsai.edu.server.abstraction.service.EventService;
import hsai.edu.server.abstraction.service.UserService;
import hsai.edu.server.abstraction.service_interfaces.EventServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
public class EventReminderBot extends TelegramLongPollingBot {
    private static final Logger LOG = LoggerFactory.getLogger(EventReminderBot.class);

    @Autowired
    private UserService userService;
    @Autowired
    private EventService eventService;

    @Autowired
    private Client client;

    private static final String START = "/start";
    private static final String HELP = "/help";
    private static final String ADDEVENT = "/add_event";
    private static final String UPDATEEVENT = "/update_event";
    private static final String DELETEEVENT = "/delete_event";
    private static final String GETNEXTEVENT = "/next_event";
    private static final String GETDAYEVENTS = "/day_event";
    private static final String GETWEEKEVENTS = "/week_event";
    private static final String GETRECEVENTS = "/rec_events";

    private static final String COMMANDS = """
                Command list:
                /start - start working
                
                /help - get help
                
                /add_event {summary} {date} {time} {duration} {type} - add event; 
                types: 0 - non recurring, 1 - hourly, 2 - daily, 3 - weekly, 4 - monthly
                
                /update_event {id} {summary} {date} {time} {duration} {type} - update event by id
                
                /delete_event {id} - delete event by id
                
                /next_event - get next event
                
                /day_event - get events for a day
                
                /week_event - get events for a week
                
                /rec_events - get all recurring events
            """;

    public EventReminderBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        var message = update.getMessage().getText().split(" ");
        var chatId = update.getMessage().getChatId();
        switch (message[0]) {
            case START -> {
                String userName = update.getMessage().getChat().getUserName();
                startCommand(chatId, userName);
            }
            case HELP -> helpCommand(chatId);
            case ADDEVENT -> addEvent(chatId, message[1], message[2], message[3], message[4], message[5]);
            case UPDATEEVENT -> updateEvent(chatId, message[1], message[2], message[3], message[4], message[5], message[6]);
            case DELETEEVENT -> deleteEvent(chatId, message[1]);
            case GETNEXTEVENT -> getNextEvent(chatId);
            case GETDAYEVENTS -> getDayEvents(chatId);
            case GETWEEKEVENTS -> getWeekEvents(chatId);
            case GETRECEVENTS -> getRecEvents(chatId);
        }
    }

    @Override
    public String getBotUsername() {
        return "event_reminder_pb_bot";
    }

    private void startCommand(Long chatId, String name) {
        try {
            userService.addUser(new UserService.AddUserDto(null, "", name, "", chatId));;
        } catch (Exception e) {
            LOG.error("Error of getting user name;", e);
            sendMessage(chatId, "Can't connect to the server.");
        }
        var text = """
                Welcome to Event Reminder Bot, %s.
                """;
        var formattedText = String.format(text, name);
        sendMessage(chatId, formattedText);
    }

    private void helpCommand(Long chatId) {
        var text = """
                %s
                """;
        var formattedText = String.format(text, COMMANDS);
        sendMessage(chatId, formattedText);
    }

    public void addEvent(Long chatId, String summary, String date, String time, String duration, String type) {
        String input = date + "; " + time;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd; HH:mm:ss");
        try {
            Date dateTime = simpleDateFormat.parse(input);
            EventServiceInterface.AddEventDto eventDto = new EventServiceInterface
                    .AddEventDto(chatId , summary, dateTime.toInstant(), Long.valueOf(duration), Integer.valueOf(type));
            eventService.addEvent(eventDto).block();
            sendMessage(chatId, "Addition success!");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateEvent(Long chatId, String id, String summary, String date, String time, String duration, String type) {
        String input = date + "; " + time;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd; HH:mm:ss");
        try {
            Date dateTime = simpleDateFormat.parse(input);
            EventServiceInterface.EditEventDto eventDto  = new EventServiceInterface
                    .EditEventDto(chatId , summary, dateTime.toInstant(), Long.valueOf(duration), Integer.valueOf(type));
            eventService.updateEvent(eventDto, Long.valueOf(id)).block();
            sendMessage(chatId, "Update success!");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void deleteEvent(Long chatId, String id) {
        eventService.deleteEvent(Long.valueOf(id)).block();
        sendMessage(chatId, "Delete success!");
    }

    public void getNextEvent(Long chatId) {
        Mono<EventServiceInterface.EventDto> event = eventService.getNext();
        String msg = "";
        if (Objects.equals(event.block(), null)) {
            msg = "Cannot find next event";
        }
        else {
            String dt = event.block().datetime().toString();
            dt.replace("T", " ");
            dt.replace("Z", "");
            msg = "Next event:\n" +
                    "Id: " + + event.block().id() + "\n" +
                    "Summary: " + event.block().summary() + "\n" +
                    "Date and time: " + dt + "\n" +
                    "Duration: " + event.block().duration() + " seconds";
        }
        sendMessage(chatId, msg);
    }

    public void getDayEvents(Long chatId) {
        Mono<List<EventServiceInterface.EventDto>> eventMonoList = eventService.getDay(Instant.now());
        String msg = "";
        if (Objects.equals(eventMonoList.block(), null)) {
            msg = "Cannot find next event";
        }
        else {
            msg = "Events for a day:";
            for (EventServiceInterface.EventDto event: eventMonoList.block().stream().toList()) {
                String dt = event.datetime().toString();
                dt.replace("T", " ");
                dt.replace("Z", "");
                msg = msg + "\n\n" +
                        "Id: " + + event.id() + "\n" +
                        "Summary: " + event.summary() + "\n" +
                        "Date and time: " + dt + "\n" +
                        "Duration: " + event.duration() + " seconds";
            }
        }
        sendMessage(chatId, msg);
    }

    public void getWeekEvents(Long chatId) {
        Mono<List<EventServiceInterface.EventDto>> eventMonoList = eventService.getWeek(Instant.now());
        String msg = "";
        if (Objects.equals(eventMonoList.block(), null)) {
            msg = "Cannot find next event";
        }
        else {
            msg = "Events for a week:";
            for (EventServiceInterface.EventDto event: eventMonoList.block().stream().toList()) {
                String dt = event.datetime().toString();
                dt.replace("T", " ");
                dt.replace("Z", "");
                msg = msg + "\n\n" +
                        "Id: " + + event.id() + "\n" +
                        "Summary: " + event.summary() + "\n" +
                        "Date and time: " + dt + "\n" +
                        "Duration: " + event.duration() + " seconds";
            }
        }
        sendMessage(chatId, msg);
    }

    private void getRecEvents(Long chatId) {
        String msg = "";
        for (int i = 1; i <= 4; i++) {
            Mono<List<EventServiceInterface.EventDto>> eventMonoList = eventService.getByType(i, chatId);
            if (Objects.equals(eventMonoList.block(), null)) {
                switch (i) {
                    case 1 -> msg += "\nCannot find hourly event\n\n";
                    case 2 -> msg += "\nCannot find daily event\n\n";
                    case 3 -> msg += "\nCannot find weekly event\n\n";
                    case 4 -> msg += "\nCannot find monthly event\n\n";
                }
            }
            else {
                switch (i) {
                    case 1 -> msg += "\nHourly events:\n\n";
                    case 2 -> msg += "\nDaily events:\n\n";
                    case 3 -> msg += "\nWeekly events:\n\n";
                    case 4 -> msg += "\nMonthly events:\n\n";
                }
                for (EventServiceInterface.EventDto event: eventMonoList.block().stream().toList()) {
                    String dt = event.datetime().toString();
                    dt.replace("T", " ");
                    dt.replace("Z", "");
                    msg = msg + "\n\n" +
                            "Id: " + + event.id() + "\n" +
                            "Summary: " + event.summary() + "\n" +
                            "Date and time: " + dt + "\n" +
                            "Duration: " + event.duration() + " seconds";
                }
            }
        }
        sendMessage(chatId, msg);
    }

    private void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOG.error("Sending message error", e);
        }
    }
}
