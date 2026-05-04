package gov.uk.ets.registry.api.file.upload.services;

import lombok.extern.log4j.Log4j2;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Log4j2
@Service
public class FileNameResolver {

    public String resolveFileContentType(byte[] content, String fileName) {
        Detector detector = new DefaultDetector();
        Metadata metadata = new Metadata();
        metadata.set("resourceName", fileName);

        try (TikaInputStream contentTikaStream = TikaInputStream.get(content)) {
            return detector.detect(contentTikaStream, metadata).toString();
        } catch (IOException e) {
            log.error("Error occurred when detecting content type of file: {}", fileName, e);
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }
}
