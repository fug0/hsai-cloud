package hsai.edu.client.configuration;

import hsai.edu.client.bot.EventReminderBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import okhttp3.OkHttpClient;

@Configuration
public class EventReminderBotConfiguration {
    @Bean
    public TelegramBotsApi telegramBotsApi(EventReminderBot bot) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(bot);
        return api;
    }

    @Bean
    public OkHttpClient okHttpClient(){
        return new OkHttpClient();
    }
}