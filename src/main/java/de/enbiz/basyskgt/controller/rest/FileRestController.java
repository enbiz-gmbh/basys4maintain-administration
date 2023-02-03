package de.enbiz.basyskgt.controller.rest;

import de.enbiz.basyskgt.storage.DbFile;
import de.enbiz.basyskgt.storage.DbFileMetadataDto;
import de.enbiz.basyskgt.storage.DbFileStorageService;
import de.enbiz.basyskgt.storage.StorageFileNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

/**
 * Modified version of <a href="https://spring.io/guides/gs/uploading-files/">Spring "uploading files" guide</a>
 */
@Controller
public class FileRestController {
    private final DbFileStorageService fileStorageService;
    Logger log = LoggerFactory.getLogger(FileRestController.class);

    @Autowired
    public FileRestController(DbFileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * Get a list of all files uploaded to the server.
     *
     * @return Iterable of {@link DbFile} objects. Data field will be null for all of them regardless of actual file content. Use {@link #serveFile} to get a single file with content.
     * @throws IOException
     */
    @GetMapping("/api/files")
    @Operation(summary = "Get a list of all files uploaded to the server", responses = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    public ResponseEntity<Iterable<DbFileMetadataDto>> listUploadedFiles() throws IOException {
        Iterable<DbFileMetadataDto> result = fileStorageService.getAllFilesMetaData();
        return ResponseEntity.ok(result);
    }

    /**
     * Download a file.
     *
     * @param fileId ID of the file to download. Use {@link #listUploadedFiles} to get a list of available files and their IDs.
     * @return
     */
    @GetMapping("/api/files/{fileId:.+}")
    @Operation(summary = "Download a file", responses = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "404", description = "file does not exist")
    })
    @ResponseBody
    public ResponseEntity<byte[]> serveFile(@PathVariable String fileId) {
        DbFile file;
        try {
            file = fileStorageService.getFile(fileId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getName() + "\"").body(file.getData());
    }

    @Operation(summary = "upload a new file", responses = {
            @ApiResponse(responseCode = "201", description = "file uploaded successfully"),
            @ApiResponse(responseCode = "500", description = "the file could not be created")
    })
    @PostMapping("/api/files")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) throws URISyntaxException {
        DbFile createdFile;
        try {
            createdFile = fileStorageService.store(file);
        } catch (IOException e) {
            log.error(String.format("An exception has occurred during a post request to /files:\n%s", e));
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return ResponseEntity.created(new URI(baseUrl + "/api/files/" + createdFile.getName())).build();
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
