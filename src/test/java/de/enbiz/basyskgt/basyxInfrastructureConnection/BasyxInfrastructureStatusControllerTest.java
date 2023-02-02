package de.enbiz.basyskgt.basyxInfrastructureConnection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class BasyxInfrastructureStatusControllerTest {

    @Autowired
    BasyxInfrastructureStatusController basyxInfrastructureStatusController;

    @Test
    void isAasServerAccess() {
        assertFalse(basyxInfrastructureStatusController.checkAasServerAccess());
    }

    @Test
    void isRegistryAccess() {
        assertFalse(basyxInfrastructureStatusController.checkRegistryAccess());
    }
}