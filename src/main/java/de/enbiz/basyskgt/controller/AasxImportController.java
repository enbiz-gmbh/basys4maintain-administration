package de.enbiz.basyskgt.controller;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.components.aas.aasx.AASXPackageManager;
import org.eclipse.basyx.support.bundle.AASBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

@Controller
public class AasxImportController {
    private static final Logger logger = LoggerFactory.getLogger(AasxImportController.class);

    private static Set<AASBundle> getAasBundlesFromFile(String aasxPath) throws IOException, ParserConfigurationException, InvalidFormatException, SAXException {
        AASXPackageManager packageManager = new AASXPackageManager(aasxPath);
        Set<AASBundle> aasBundles = packageManager.retrieveAASBundles();

        logger.info("retrieved " + aasBundles.size() + " AASBundles from file");
        return aasBundles;
    }

    /**
     * Provides the AAS for the Ball Screw from the aasx file
     *
     * @return AAS of the ball screw
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws InvalidFormatException
     * @throws SAXException
     */
    @Bean
    IAssetAdministrationShell bsAas(AASBundle bsAasBundle) throws IOException, ParserConfigurationException, InvalidFormatException, SAXException {
        return bsAasBundle.getAAS();
    }

    @Bean
    AASBundle bsAasBundle() throws IOException, ParserConfigurationException, InvalidFormatException, SAXException {
        Collection<AASBundle> aasBundles = AasxImportController.getAasBundlesFromFile("aasx/02_Bosch.aasx");
        if (aasBundles.size() != 1) {
            throw new InvalidFormatException("aasx file must contain EXACTLY one Asset Administration Shell");
        } else {
            return aasBundles.iterator().next();
        }
    }
}