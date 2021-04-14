package de.vsc.coi;

import static de.vsc.coi.config.ProjectConfig.project;
import static java.net.http.HttpClient.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class VersionChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionChecker.class);

    public static String getLatestVersion() {
        try {
            final HttpRequest request = HttpRequest.newBuilder().uri(new URI(project().getLookupUrl())).GET().build();
            final String[] uri = newBuilder().followRedirects(HttpClient.Redirect.NORMAL)
                    .build()
                    .send(request, ofString())
                    .uri()
                    .toString()
                    .split("/");
            if (uri.length == 0) {
                LOGGER.warn("Could not retrieve version.");
                return null;
            }
            return uri[uri.length - 1];
        } catch (final InterruptedException | IOException | URISyntaxException e) {
            LOGGER.error("Could not retrieve version.", e);
            return null;
        }
    }

    public static boolean needsUpdate(final String latestVersion, final String currentVersion) {
        return needsUpdate(toVersionQueue(latestVersion), toVersionQueue(currentVersion));
    }

    public static boolean needsUpdate(final Queue<Integer> latestVersion, final Queue<Integer> currentVersion) {
        while (!(currentVersion.isEmpty() && latestVersion.isEmpty())) {
            final int l = pollOrZero(latestVersion);
            final int c = pollOrZero(currentVersion);
            if (l > c) {
                return true;
            } else if (c > l) {
                return false;
            }
        }
        return false;
    }

    public static int pollOrZero(final Queue<Integer> queue) {
        if (queue.isEmpty()) {
            return 0;
        }
        return queue.poll();
    }

    public static Queue<Integer> toVersionQueue(final String version) {
        return Stream.of(version)
                .map(x -> x.split("\\."))
                .flatMap(Arrays::stream)
                .map(x->x.replaceAll("\\D",""))
                .map(Integer::parseInt)
                .collect(Collectors.toCollection(LinkedList::new));
    }

}
