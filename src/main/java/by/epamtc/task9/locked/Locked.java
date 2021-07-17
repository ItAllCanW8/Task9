package by.epamtc.task9.locked;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Locked implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();

    private final int SIZE;
    private final int NUM_OF_ITER;
    private final long[][] MATRIX;

    private final Lock LOCK = new ReentrantLock();

    private FileWriter writer;
    private final Random RAND = new Random();

    private static class Holder {
        static final Locked INSTANCE = new Locked();
    }

    public static Locked getInstance() {
        return Holder.INSTANCE;
    }

    private Locked() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("matrix-data.properties");

        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            LOGGER.error("Properties file is not valid");
        }

        SIZE = Integer.parseInt(properties.getProperty("N"));

        MATRIX = new long[SIZE][SIZE];
        NUM_OF_ITER = Integer.parseInt(properties.getProperty("Y"));

        String output = properties.getProperty("locked-output");

        try {
            writer = new FileWriter(output, false);
        } catch (IOException e) {
            LOGGER.error("Error opening output file", e);
        }
    }

    public long performTask(long tid, int iter) {
        int pos = RAND.nextInt(MATRIX.length);

        while (pos == iter)
            pos = RAND.nextInt(MATRIX.length);

        try {
            LOCK.lock();

            LOGGER.info("Thread {} started", tid);

            MATRIX[iter][iter] = tid;

            if (RAND.nextBoolean()) MATRIX[iter][pos] = tid;
            else MATRIX[pos][iter] = tid;

            long sum = 0L;

            for (long[] row : MATRIX)
                for (long element : row)
                    sum += element;

            writeToFile(sum);

            LOGGER.info("Thread {} finished", tid);

            return sum;
        } finally {
            LOCK.unlock();
        }
    }

    private void writeToFile(long sum){
        try {
            writer.append(String.valueOf(sum));
            writer.append(System.lineSeparator());

            for (long[] longs : MATRIX) {
                writer.append(Arrays.toString(longs));
                writer.append(System.lineSeparator());
            }
        } catch (IOException e){
            LOGGER.error("Error writing to file", e);
        }
    }

    public int getSIZE() {
        return SIZE;
    }

    public int getNUM_OF_ITER() {
        return NUM_OF_ITER;
    }

    @Override
    public void close() throws Exception {
        if (writer != null) {
            writer.close();
        }
    }
}
