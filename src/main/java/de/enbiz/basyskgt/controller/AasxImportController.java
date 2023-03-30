package de.enbiz.basyskgt.controller;

import de.enbiz.basyskgt.exceptions.AASXFileParseException;
import de.enbiz.basyskgt.storage.AasxFile;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.factory.aasx.AASXToMetamodelConverter;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;

@Controller
public class AasxImportController {
    private static final Logger logger = LoggerFactory.getLogger(AasxImportController.class);

    @Deprecated
    private static Set<AASBundle> getAasBundlesFromFile(String aasxPath) throws IOException, ParserConfigurationException, InvalidFormatException, SAXException {
        AASXToMetamodelConverter packageManager = new AASXToMetamodelConverter(aasxPath);
        Set<AASBundle> aasBundles = packageManager.retrieveAASBundles();

        logger.info("retrieved " + aasBundles.size() + " AASBundles from file");
        return aasBundles;
    }

    public IAssetAdministrationShell getAasFromBytes(byte[] aasxFile) throws InvalidFormatException, AASXFileParseException {
        IAssetAdministrationShell aas = getAasBundleFromBytes(aasxFile).getAAS();
        logger.info("retrieved AAS with identifier " + aas.getIdentification() + " from file");
        return aas;
    }

    private AASBundle getAasBundleFromBytes(byte[] aasxFile) throws InvalidFormatException, AASXFileParseException {
        AASXToMetamodelConverter packageManager = new AASXToMetamodelConverter(new ByteArrayInputStream(aasxFile));
        Set<AASBundle> aasBundles = null;
        try {
            aasBundles = packageManager.retrieveAASBundles();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new AASXFileParseException(String.format("AASX file could not be parsed. Reason: %s", e.getMessage()));
        }
        if (aasBundles.size() != 1) {
            throw new InvalidFormatException("Files must contain EXACTLY ONE AASBundle. File contains " + aasBundles.size());
        }

        return aasBundles.iterator().next();
    }

    public AASBundle getAasBundleFromAasx(AasxFile aasxFile) throws AASXFileParseException, InvalidFormatException {
        return getAasBundleFromBytes(aasxFile.getData());
    }
}
