package de.enbiz.basyskgt.basyxInfrastructureConnection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BasyxInfrastructureStatusControllerTest {

    @Autowired
    BasyxInfrastructureStatusController basyxInfrastructureStatusController;

    @Test
    void checkAasServerAccess() {
        basyxInfrastructureStatusController.checkAasServerAccess();
    }

    @Test
    void checkRegistryAccess() {
        basyxInfrastructureStatusController.checkRegistryAccess();
    }
}