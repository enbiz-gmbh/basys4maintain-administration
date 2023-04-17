package de.enbiz.basyskgt.storage;

import de.enbiz.basyskgt.controller.AasxImportController;
import de.enbiz.basyskgt.exceptions.AASXFileParseException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;

@Service
public class AasxFileStorageService {

    private final AasxFileRepository aasxFileRepository;
    private final AasxImportController aasxImportController;

    @Autowired
    public AasxFileStorageService(AasxFileRepository aasxFileRepository, AasxImportController aasxImportController) {
        this.aasxFileRepository = aasxFileRepository;
        this.aasxImportController = aasxImportController;
    }

    public AasxFile store(MultipartFile file) throws IOException, AASXFileParseException, InvalidFormatException, IllegalStateException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        byte[] fileContent = file.getBytes();
        IIdentifier aasIdentifier = aasxImportController.getAasFromBytes(fileContent).getIdentification();
        AasxFile aasxFile = new AasxFile(fileName, file.getContentType(), fileContent, aasIdentifier.getIdType(), aasIdentifier.getId());

        try {
            return aasxFileRepository.save(aasxFile);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("File already exists");
        }
    }

    public AasxFile getFile(String id) throws NoSuchElementException {
        return aasxFileRepository.findById(id).orElseThrow();
    }

    public Iterable<AasxFileMetaData> getAllFilesMetaData() {
        return aasxFileRepository.findAllMetaData();
    }

    public void deleteFile(String id) throws IllegalStateException, FileNotFoundException {
        try {
            aasxFileRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("File is currently mapped to a port. Unmap it before reattempting deletion.");
        } catch (EmptyResultDataAccessException e) {
            throw new FileNotFoundException(String.format("file with ID %s does not exist", id));
        }
    }
}


