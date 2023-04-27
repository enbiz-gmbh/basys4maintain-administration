package de.enbiz.basyskgt.controller.frontend;

import de.enbiz.basyskgt.configuration.BasyxInfrastructureConfig;
import de.enbiz.basyskgt.configuration.PortConfiguration;
import de.enbiz.basyskgt.controller.RegistrationController;
import de.enbiz.basyskgt.controller.RegistrationStatusController;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final Logger log = LoggerFactory.getLogger(IndexController.class);

    final RegistrationController registrationController;

    final RegistrationStatusController registrationStatusController;

    final BasyxInfrastructureConfig basyxInfrastructureConfig;
    final PortConfiguration portConfiguration;

    @Autowired
    public IndexController(RegistrationController registrationController, RegistrationStatusController registrationStatusController, BasyxInfrastructureConfig basyxInfrastructureConfig, PortConfiguration portConfiguration) {
        this.registrationController = registrationController;
        this.registrationStatusController = registrationStatusController;
        this.basyxInfrastructureConfig = basyxInfrastructureConfig;
        this.portConfiguration = portConfiguration;
    }

    @GetMapping("/")
    public String index(Model model) {
        int numPorts = portConfiguration.getNumPorts();
        RegistrationStatusController.RegistrationStatus[] registrationStatus = registrationStatusController.refreshAndGetAllRegistrationStatus();
        PortStatusRecord[] portStatusRecords = new PortStatusRecord[numPorts];
        for (int i = 0; i < numPorts; i++) {
            IIdentifier identifier = portConfiguration.getMappedAasIdentifier(i);
            PortStatusRecord portStatusRecord = new PortStatusRecord(
                    i,
                    registrationStatus[i],
                    portConfiguration.getMappedFileId(i),
                    identifier != null ? identifier.getId() : null
            );
            portStatusRecords[i] = portStatusRecord;
        }

        model.addAttribute("portStatusRecords", portStatusRecords);
        model.addAttribute("aasServerPath", basyxInfrastructureConfig.getAasServerPath());
        model.addAttribute("registryPath", basyxInfrastructureConfig.getRegistryServerPath());
        return "index";
    }

    private record PortStatusRecord(int portNumber, RegistrationStatusController.RegistrationStatus registrationStatus, String fileId, String aasId) {
    }

    ;
}
