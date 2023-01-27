package de.enbiz.basyskgt.controller;

import de.enbiz.basyskgt.configuration.PortConfiguration;
import de.enbiz.basyskgt.dto.HealthDTO;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HealthController {
    public static final int MAX_BUFFER_SIZE = 100;
    private CircularFifoQueue<HealthDTO> buffer = new CircularFifoQueue<>(MAX_BUFFER_SIZE);

    private PortConfiguration portConfiguration;

    @Autowired
    public HealthController(PortConfiguration portConfiguration) {
        this.portConfiguration = portConfiguration;
    }

    public void setHealth(int portNumber, short health) {
        if (health < 0 || health > 100) {
            throw new IllegalArgumentException("Health value has to be a percentage between 0 and 100");
        }
        if (portNumber >= PortConfiguration.NUM_PORTS || portNumber < 0) {
            throw new IllegalArgumentException(String.format("Port number %d does not exist.", portNumber));
        }
        if (portConfiguration.getMappedIdentifier(portNumber) != null) {
            throw new IllegalArgumentException("There is no device configured for the given port.");
        }
        buffer.add(new HealthDTO(health, portConfiguration.getMappedIdentifier(portNumber)));
    }

    public HealthDTO getMostRecent() {
        return buffer.get(buffer.size() - 1);
    }

    public List<HealthDTO> getMostRecent(int count) {
        count = Math.min(count, size());
        if (count == size()) {
            return getAll();
        }
        return getAll().subList(size() - count, size());
    }

    public List<HealthDTO> getAll() {
        List<HealthDTO> result = new ArrayList<>(size());
        result.addAll(buffer);
        return result;
    }

    public void flush() {
        buffer = new CircularFifoQueue<>(MAX_BUFFER_SIZE);
    }

    public int size() {
        return buffer.size();
    }
}
