package de.enbiz.basyskgt.controller;

import de.enbiz.basyskgt.model.HealthEntity;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HealthController {
    public static final int MAX_BUFFER_SIZE = 100;
    private CircularFifoQueue<HealthEntity> buffer = new CircularFifoQueue<>(MAX_BUFFER_SIZE);

    public void insert(int health) {
        insert(new HealthEntity(health));
    }

    public void insert(HealthEntity healthEntity) {
        buffer.add(healthEntity);
    }

    public HealthEntity getMostRecent() {
        return buffer.get(buffer.size() - 1);
    }

    public List<HealthEntity> getMostRecent(int count) {
        count = Math.min(count, size());
        if (count == size()) {
            return getAll();
        }
        return getAll().subList(size() - count, size());
    }

    public List<HealthEntity> getAll() {
        List<HealthEntity> result = new ArrayList<>(size());
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
