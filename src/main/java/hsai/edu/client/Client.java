package hsai.edu.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class Client {
    @Autowired
    private OkHttpClient client;

    @Value("${server.hostname}")
    private String hostname;

    public Optional<String> getRequest(String url) throws Exception {
        var request = new Request.Builder()
                .url(url)
                .build();
        try (var response = client.newCall(request).execute()) {
            var body = response.body();
            return body == null ? Optional.empty() : Optional.of(body.string());
        } catch (IOException e) {
            throw new Exception(e);
        }
    }
}
