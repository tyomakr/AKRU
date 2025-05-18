// FileRenamerCli.java
package ru.aikr.akru.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import ru.aikr.akru.core.api.CoreService;
import ru.aikr.akru.core.api.DuplicateStrategy;
import ru.aikr.akru.core.api.RenameResult;
import ru.aikr.akru.core.impl.CoreServiceImpl;

public class FileRenamerCli {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try (InputStream in = System.in; OutputStream out = System.out) {
            CliRequest req = mapper.readValue(in, CliRequest.class);

            List<Path> paths = req.getFiles().stream()
                    .map(Path::of)
                    .collect(Collectors.toList());

            CoreService service = new CoreServiceImpl();
            List<RenameResult> results = service.processFiles(
                    paths,
                    req.getTemplate(),
                    DuplicateStrategy.valueOf(req.getOptions().getHandleDuplicates().toUpperCase()),
                    ZoneId.of(req.getOptions().getTz())
            );

            List<CliResult> cli = results.stream().map(r -> {
                CliResult cr = new CliResult();
                cr.setOrig(r.getSource() != null ? r.getSource().toString() : null);
                cr.setOrig(r.getSource().toString());
                cr.setNewName(r.isSuccess()
                        ? r.getTarget().getFileName().toString()
                        : null);
                cr.setStatus(r.isSuccess() ? "ok" : "skipped");
                cr.setError(r.isSuccess() ? null : r.getErrorMessage());
                return cr;
            }).collect(Collectors.toList());

            mapper.writeValue(out, cli);

        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
            // убрали System.exit, просто позволяем main завершиться
        } catch (Exception e) {
            e.printStackTrace(System.err);
            // убрали System.exit
        }
    }
}