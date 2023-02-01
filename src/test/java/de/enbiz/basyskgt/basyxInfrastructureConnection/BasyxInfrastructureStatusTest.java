package de.enbiz.basyskgt.basyxInfrastructureConnection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class BasyxInfrastructureStatusTest {

    @Autowired
    BasyxInfrastructureStatus basyxInfrastructureStatus;

    @Test
    void isAasServerAccess() {
        assertFalse(basyxInfrastructureStatus.isAasServerAccess());
    }

    @Test
    void isRegistryAccess() {
        assertFalse(basyxInfrastructureStatus.isRegistryAccess());
    }
}