package by.epamtc.task9.locked;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LockedThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger();

    private final Locked MATRIX = Locked.getInstance();
    private final int iter;

    public LockedThread(int iter) {
        this.iter = iter;
    }

    @Override
    public void run() {
        long sum = MATRIX.performTask(this.getId(), iter);
        LOGGER.info("Thread {} calculated sum {}.", this.getId(), sum);
    }
}
