package de.enbiz.basyskgt.basyx;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.servlet.submodel.SubmodelServlet;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.AASLambdaPropertyHelper;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxHTTPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Prototype of executable package that could be delivered with a Kugelgewindetrieb.
 * The Program automatically creates an Asset administration shell on a central AAS-server and registers it with the central
 * AAS-registry. A submodel containing the KGTs remaining health is hosted on a local submodel-server and registered at
 * the central registry but NOT uploaded to the AAS-server.
 */
public class KgtExecutable {
    // Address of AAS-registry-server; has to be configured by the customer
    public static final String REGISTRYPATH = "http://localhost:4000/registry";
    // Address of AAS-server to which the bundled AAS will be uploaded; has to be configured by the customer
    public static final String AASSERVERPATH = "http://localhost:4001/aasServer";
    // Serial Number of the part. This is instance specific.
    public static final String SERIAL_NO = "1234";
    public static final IIdentifier AAS_ID = new Identifier(IdentifierType.IRI, "https://www.steinmeyer.com/aas/kgt/" + SERIAL_NO);
    public static final IIdentifier KGT_HEALTH_SUBMODEL_ID = new CustomId("de.enbiz.basyx.kgt.submodel.conditionMonitoring");
    private static final Logger log = LoggerFactory.getLogger(KgtExecutable.class);
    private static final String REMAINING_HEALTH_PROPERTY_NAME = "remainingHealth";
    private static final BaSyxContextConfiguration SUBMODEL_SERVER_CONTEXT = new BaSyxContextConfiguration(4005, "submodels");
    private static final IIdentifier ASSET_ID = new Identifier(IdentifierType.IRI, "https://www.steinmeyer.com/asset/kgt/" + SERIAL_NO);

    public static void main(String[] args) {
        // create KGT AAS, push it to remote AAS server and register at remote AAS registry
        Asset kgtAsset = new Asset("kgt1234", ASSET_ID, AssetKind.INSTANCE);
        AssetAdministrationShell kgtAas = new AssetAdministrationShell("kgt1234", AAS_ID, kgtAsset);
        AASRegistryProxy registryProxy = new AASRegistryProxy(REGISTRYPATH);
        ConnectedAssetAdministrationShellManager connectedAasManager = new ConnectedAssetAdministrationShellManager(registryProxy);
        connectedAasManager.createAAS(kgtAas, AASSERVERPATH);
        log.info("Registered KGT AAS at " + REGISTRYPATH);

        // create and populate kgt health submodel
        Submodel conditionMonitoringSubmodel = new Submodel("conditionMonitoring", KGT_HEALTH_SUBMODEL_ID);
        Property kgtHealthProperty = new Property();
        kgtHealthProperty.setIdShort("health");
        Supplier<Object> kgtHealthSupplier = () -> calculateKgtHealth();
        AASLambdaPropertyHelper.setLambdaValue(kgtHealthProperty, kgtHealthSupplier, null);
        conditionMonitoringSubmodel.addSubmodelElement(kgtHealthProperty);
        log.info("KGT health submodel created. Starting up submodel server...");

        // start serving the submodel on dedicated submodel server
        BaSyxHTTPServer submodelServer = hostPreconfiguredSubmodel(SUBMODEL_SERVER_CONTEXT, conditionMonitoringSubmodel);

        registryProxy.register(AAS_ID, new SubmodelDescriptor(conditionMonitoringSubmodel, "http://localhost:4005/submodels/conditionMonitoring/submodel"));
    }

    private static Float calculateKgtHealth() {
        log.info("Calculating KGT Health...\t");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        float health = (float) Math.random();
        log.info("Health is " + health);
        return health;
    }

    /**
     * source: https://git.eclipse.org/r/plugins/gitiles/basyx/basyx/+/master/examples/basys.examples/src/main/java/org/eclipse/basyx/examples/snippets/submodel/HostPreconfiguredSubmodel.java
     *
     * @param serverContext the context configuration of the server
     * @param sm            the submodel to be hosted
     * @return the started server
     */
    private static BaSyxHTTPServer hostPreconfiguredSubmodel(BaSyxContextConfiguration serverContext, Submodel sm) {
        // Create the BaSyx context from the context configuration
        BaSyxContext context = serverContext.createBaSyxContext();
        // Create a new SubmodelServlet containing the passed submodel
        SubmodelServlet smServlet = new SubmodelServlet(sm);
        // Add the SubmodelServlet mapping to the context at the path "$SmIdShort"
        // Here, it possible to add further submodels using the same pattern
        context.addServletMapping("/" + sm.getIdShort() + "/*", smServlet);
        // Create and start a HTTP server with the context created above
        BaSyxHTTPServer preconfiguredSmServer = new BaSyxHTTPServer(context);
        preconfiguredSmServer.start();
        log.info("Submodel server running at " + serverContext.getUrl());
        return preconfiguredSmServer;
    }
}
