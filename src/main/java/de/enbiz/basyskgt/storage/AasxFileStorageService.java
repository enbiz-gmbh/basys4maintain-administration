package de.enbiz.basyskgt.storage;

import de.enbiz.basyskgt.controller.AasxImportController;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
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

    public AasxFile store(MultipartFile file) throws IOException, ParserConfigurationException, InvalidFormatException, SAXException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        byte[] fileContent = file.getBytes();
        IIdentifier aasIdentifier = aasxImportController.getAasFromBytes(fileContent).getIdentification();
        AasxFile aasxFile = new AasxFile(fileName, file.getContentType(), fileContent, aasIdentifier.getIdType(), aasIdentifier.getId());

        return aasxFileRepository.save(aasxFile);
    }

    public AasxFile getFile(String id) throws NoSuchElementException {
        return aasxFileRepository.findById(id).orElseThrow();
    }

    public Iterable<AasxFileMetaData> getAllFilesMetaData() {
        return aasxFileRepository.findAllMetaData();
    }

    public void deleteFile(String id) {
        // TODO check if the file is mapped to a port and only allow deletion if it is not in use
        aasxFileRepository.deleteById(id);
    }
}


