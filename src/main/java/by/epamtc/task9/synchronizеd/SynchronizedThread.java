package by.epamtc.task9.synchronizеd;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SynchronizedThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger();

    private final Synchronized MATRIX = Synchronized.getInstance();
    private final int iter;

    public SynchronizedThread(int iter) {
        this.iter = iter;
    }

    @Override
    public void run() {
        long sum = MATRIX.performTask(this.getId(), iter);
        LOGGER.info("Thread {} calculated sum {}.", this.getId(), sum);
    }
}