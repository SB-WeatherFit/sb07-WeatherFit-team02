package com.codeit.weatherfit.global.s3;

import com.codeit.weatherfit.global.s3.exception.S3UploadException;
import com.codeit.weatherfit.global.s3.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogS3Service {
    private final S3Service s3Service;
    private final S3Properties s3Properties;

    public void uploadLogFile() {
        Path logDir = Paths.get("./logs");

        String targetDate = LocalDate.now()
                .minusDays(1)
                .toString();

        try (Stream<Path> files = Files.list(logDir)) {
            files
                    .filter(p -> p.getFileName().toString()
                            .equals("app-" + targetDate + ".log.gz"))
                    .forEach(path -> {
                        try {
                            byte[] bytes = Files.readAllBytes(path);
                            String key = "logs/" + path.getFileName();
                            s3Service.put(bytes, key);
                        } catch (IOException e) {
                            log.warn("로그 파일 읽기 실패: {}", path);
                            throw new S3UploadException(path.toString());
                        }
                    });
        } catch (IOException e) {
            log.warn("로그 디렉토리 접근 실패: {}", logDir);
            throw new S3UploadException(logDir.toString());
        }
    }


}
