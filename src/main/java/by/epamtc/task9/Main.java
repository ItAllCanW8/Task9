package by.epamtc.task9;

import by.epamtc.task9.locked.Locked;
import by.epamtc.task9.locked.LockedThread;
import by.epamtc.task9.synchronizеd.Synchronized;
import by.epamtc.task9.synchronizеd.SynchronizedThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        lockedRealization();
        System.out.println("///////////////////////////////////////////////////////////////////////");
        synchronizedRealization();
    }

    private static void lockedRealization() {
        Locked locked = Locked.getInstance();
        List<List<LockedThread>> lockedThreads = new ArrayList<>();

        for (int i = 0; i < locked.getNUM_OF_ITER(); i++) {
            List<LockedThread> row = new ArrayList<>();

            for (int j = 0; j < locked.getSIZE(); j++)
                row.add(new LockedThread(j));

            lockedThreads.add(row);
        }

        for (var iter : lockedThreads) {
            for (var thread : iter) {
                thread.start();
            }
        }

        for (var iter : lockedThreads) {
            for (var thread : iter) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    LOGGER.error("Locked thread interrupted", e);
                    Thread.currentThread().interrupt();
                }
            }
        }
        try {
            locked.close();
        } catch (Exception e) {
            LOGGER.error("Error closing locked output file");
        }
    }

    private static void synchronizedRealization() {
        Synchronized synchr = Synchronized.getInstance();
        List<List<SynchronizedThread>> synchronizedThreads = new ArrayList<>();

        for (int i = 0; i < synchr.getNUM_OF_ITER(); i++) {

            List<SynchronizedThread> row = new ArrayList<>();

            for (int j = 0; j < synchr.getSIZE(); j++)
                row.add(new SynchronizedThread(j));

            synchronizedThreads.add(row);
        }

        for (var iter : synchronizedThreads) {
            for (var thread : iter) {
                thread.start();
            }
        }

        for (var iter : synchronizedThreads) {
            for (var thread : iter) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    LOGGER.error("Synchronized thread interrupted", e);
                    Thread.currentThread().interrupt();
                }
            }
        }

        try {
            synchr.close();
        } catch (Exception e) {
            LOGGER.error("Error closing synchronized output file");
        }
    }
}
