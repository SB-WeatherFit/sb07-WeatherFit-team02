package com.codeit.weatherfit.global.util.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class LogS3Service {
    private final S3Service s3Service;

    public void uploadLogFile() {
        Path logDir = Paths.get("./logs");

        String targetDate = LocalDate.now()
                .minusDays(1)
                .toString();

        try (
                Stream<Path> files = Files.list(logDir)) {
            files
                    .filter(p -> p.getFileName().toString()
                            .equals("app-" + targetDate + ".log.gz"))
                    .forEach(path -> {
                        try {
                            byte[] bytes = Files.readAllBytes(path);
                            String key = "logs/" + path.getFileName();

                            s3Service.put(bytes, key);

                        } catch (IOException e) {
                            throw new RuntimeException("로그 업로드 실패: " + path, e);
                        }
                    });

        } catch (
                IOException e) {
            throw new RuntimeException("로그 디렉토리 접근 실패", e);
        }

    }
}
