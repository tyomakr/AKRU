package ru.aikr.akru.cli;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FileRenamerCliIntegrationTest {

    @Test
    void testCliWithSampleRequest() throws Exception {
        // 1) загружаем sample JSON из ресурсов
        String jsonReq = Files.readString(Path.of("src/test/resources/sample-request.json"));

        // 2) подменяем System.in/out
        ByteArrayInputStream in  = new ByteArrayInputStream(jsonReq.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new java.io.PrintStream(out));

        // 3) вызываем CLI
        FileRenamerCli.main(new String[0]);

        // 4) читаем ответ
        String jsonRes = out.toString().trim();
        ObjectMapper mapper = new ObjectMapper();
        List<CliResult> results = mapper.readValue(jsonRes, new TypeReference<>() {});

        // 5) проверяем
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getOrig()).endsWith("photo1.jpeg");
        assertThat(results.get(0).getStatus()).isIn("ok", "skipped");
    }
}
