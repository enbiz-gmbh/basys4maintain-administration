package de.enbiz.basyskgt.controller.rest;

import de.enbiz.basyskgt.exceptions.AASXFileParseException;
import de.enbiz.basyskgt.storage.AasxFile;
import de.enbiz.basyskgt.storage.AasxFileMetaData;
import de.enbiz.basyskgt.storage.AasxFileStorageService;
import de.enbiz.basyskgt.storage.StorageFileNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;
import java.util.zip.ZipException;

/**
 * Modified version of <a href="https://spring.io/guides/gs/uploading-files/">Spring "uploading files" guide</a>
 */
@Controller
public class FileRestController {
    private final AasxFileStorageService fileStorageService;
    Logger log = LoggerFactory.getLogger(FileRestController.class);

    @Autowired
    public FileRestController(AasxFileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * Get a list of all files uploaded to the server.
     *
     * @return Iterable of {@link AasxFile} objects. Data field will be null for all of them regardless of actual file content. Use {@link #serveFile} to get a single file with content.
     * @throws IOException
     */
    @GetMapping("/api/files")
    @Operation(summary = "Get a list of all files uploaded to the server", responses = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    public ResponseEntity<Iterable<AasxFileMetaData>> listUploadedFiles() throws IOException {
        Iterable<AasxFileMetaData> result = fileStorageService.getAllFilesMetaData();
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
        AasxFile file;
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
            @ApiResponse(responseCode = "500", description = "the file could not be created"),
            @ApiResponse(responseCode = "409", description = "an AAS file for the same device already exists on the server")
    })
    @PostMapping(value = "/api/files", consumes = {"multipart/form-data"})
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) throws URISyntaxException {
        AasxFile createdFile;
        try {
            createdFile = fileStorageService.store(file);
        } catch (ZipException e) {
            log.error(String.format("An exception has occurred during a post request to /files: %s", e));
            return ResponseEntity.badRequest().body("The file could not be opened. Please make sure it is a valid AASX file.");
        } catch (AASXFileParseException | IOException e) {
            log.error("An exception has occurred during a post request to /files: %s", e);
            return ResponseEntity.internalServerError().body("The file could not be saved to the server. Please contact the server administrator.");
        } catch (InvalidFormatException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("An AAS file with the same identifier already exists on the server.");
        }
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return ResponseEntity.created(new URI(baseUrl + "/api/files/" + createdFile.getId())).build();
    }

    @Operation(summary = "delete a saved file", responses = {
            @ApiResponse(responseCode = "200", description = "success")
    })
    @DeleteMapping("/api/files/{fileId:.+}")
    public ResponseEntity deleteFile(@PathVariable String fileId) {
        try {
            fileStorageService.deleteFile(fileId);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
