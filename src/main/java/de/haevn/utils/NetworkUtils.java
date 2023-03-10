package de.haevn.utils;

import de.haevn.exceptions.NetworkException;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class NetworkUtils {
    private static final PropertyHandler propertyHandler = PropertyHandler.getInstance("config");

    private NetworkUtils() {
    }


    //----------------------------------------------------------------------------------------------------------------------
    //  Downloader
    //----------------------------------------------------------------------------------------------------------------------

    public static HttpResponse<String> download(String url) throws NetworkException {
        try {
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .timeout(java.time.Duration.ofSeconds(propertyHandler.getLong("network.timeout")))
                    .build();
            return HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException | IOException ex) {
            throw new NetworkException(ex);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NetworkException(e);
        }
    }

    public static CompletableFuture<Optional<HttpResponse<String>>> downloadAsync(String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Optional.of(download(url));
            } catch (NetworkException e) {
                return Optional.empty();
            }
        });
    }


    //----------------------------------------------------------------------------------------------------------------------
    // Helper methods
    //----------------------------------------------------------------------------------------------------------------------


    public static boolean is2xx(int code) {
        return code >= 200 && code < 300;
    }

    public static boolean isNot2xx(int code) {
        return !is2xx(code);
    }


    public static boolean is3xx(int code) {
        return code >= 300 && code < 400;
    }

    public static boolean is4xx(int code) {
        return code >= 400 && code < 500;
    }

    public static boolean is5xx(int code) {
        return code >= 500 && code < 600;
    }

    public static boolean isUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    public static void openWebsite(String url) {
        if (isUrl(url)) {
            try {
                Desktop.getDesktop().browse(java.net.URI.create(url));
            } catch (IOException ignored) {
            }
        }
    }
}
