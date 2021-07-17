package by.epamtc.task9.synchronizеd;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

public class Synchronized implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();

    private final int SIZE;
    private final int NUM_OF_ITER;
    private final long[][] MATRIX;

    private FileWriter writer;
    private final Random RAND = new Random();

    private static class Holder {
        static final Synchronized INSTANCE = new Synchronized();
    }

    public static Synchronized getInstance() {
        return Holder.INSTANCE;
    }

    private Synchronized() {
        InputStream propertyFileStream = getClass().getClassLoader().getResourceAsStream("matrix-data.properties");

        Properties properties = new Properties();

        try {
            properties.load(propertyFileStream);
        } catch (IOException e) {
            LOGGER.error("Properties file is not valid");
        }

        SIZE = Integer.parseInt(properties.getProperty("N"));
        MATRIX = new long[SIZE][SIZE];

        NUM_OF_ITER = Integer.parseInt(properties.getProperty("Y"));

        String outputFile = properties.getProperty("synchronized-output");

        try {
            writer = new FileWriter(outputFile, false);
        } catch (IOException e) {
            LOGGER.error("Error opening output file", e);
        }
    }

    public long performTask(long tid, int iter) {
        int pos = RAND.nextInt(MATRIX.length);

        while (pos == iter) {
            pos = RAND.nextInt(MATRIX.length);
        }

        synchronized (this) {
            LOGGER.info("Thread {} started", tid);
            MATRIX[iter][iter] = tid;

            if (RAND.nextBoolean()) MATRIX[iter][pos] = tid;
            else MATRIX[pos][iter] = tid;

            long sum = 0L;

            for (long[] row : MATRIX)
                for (long element : row)
                    sum += element;

            writeToFile(sum);

            LOGGER.info("Thread {} finished.", tid);

            return sum;
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
        } catch (IOException e) {
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
