package de.enbiz.basyskgt.basyx;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.components.aas.aasx.AASXPackageManager;
import org.eclipse.basyx.support.bundle.AASBundle;
import org.eclipse.basyx.support.bundle.AASBundleHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Set;

@Component
public class AasxImporter {
    private static final Logger logger = LoggerFactory.getLogger(AasxImporter.class);

    @Autowired
    private static AASAggregatorProxy aasAggregatorProxy;

    @Autowired
    private static AASRegistryProxy aasRegistryProxy;

    public static Set<AASBundle> getAas(String aasxPath) throws IOException, ParserConfigurationException, InvalidFormatException, SAXException {
        AASXPackageManager packageManager = new AASXPackageManager(aasxPath);
        Set<AASBundle> aasBundles = packageManager.retrieveAASBundles();

        logger.info("retrieved " + aasBundles.size() + " AASBundles from file");
        return aasBundles;
    }

    public static void getAndRegisterAas(String aasxPath) throws IOException, ParserConfigurationException, InvalidFormatException, SAXException {
        Set<AASBundle> aasBundles = getAas(aasxPath);
        AASBundleHelper.integrate(aasAggregatorProxy, aasBundles);
        AASBundleHelper.register(aasRegistryProxy, aasBundles, LocalBasyxServerComponent.AASSERVERPATH);
    }
}
