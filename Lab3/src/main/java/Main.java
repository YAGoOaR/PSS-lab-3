package main.java;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    static String url = "jdbc:mysql://localhost:6000/mydb";
    static String username = "root";
    static String password = "mysecretpass";
    static String queryUpdatePattern = "UPDATE `table1` SET `col1` = %s WHERE id = %s";
    static String queryDeletePattern = "DELETE FROM `table1` WHERE id = (\n" +
            "  SELECT * FROM (\n" +
            "    SELECT MAX(id) FROM `table1`\n" +
            "  ) AS t\n" +
            ");";
    static String queryInsertPattern = "INSERT INTO `table1` (`col1`) VALUES (%s);";
    static String querySelectPattern = "SELECT `col1` FROM `table1` WHERE id = %s";

    static long measureTime (Runnable f) {
        long startTime = System.nanoTime();
        f.run();
        return System.nanoTime() - startTime;
    }
    static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    static AtomicReference<Statement> smtAtomic = null;

    static void runTransaction (Connection connection, int i) {
        String queryUpdate = String.format(queryUpdatePattern, i, getRandomNumber(1, 6));
        String queryDelete = String.format(queryDeletePattern);
        String queryInsert = String.format(queryInsertPattern, i);
        String queryUpdate2 = String.format(queryUpdatePattern, i, getRandomNumber(1, 6));
        try {
            Statement smt = smtAtomic.get();
            smt.executeUpdate(queryUpdate);
            smt.executeUpdate(queryDelete);
            connection.setSavepoint("mysavepoint");
            smt.executeUpdate(queryUpdate);
            smt.executeUpdate(queryUpdate2);
            smt.executeUpdate(queryInsert);
            smtAtomic.set(smt);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static void runTransactions(Connection connection) {
        ExecutorService service = Executors.newFixedThreadPool(16);
        ArrayList<Future<?>> futures = new ArrayList<>();
        for (int j = 0; j < 16; j++) {
            final int J = j;
            futures.add(service.submit(() -> runTransaction(connection, J)));
        }
        try {
            for (Future<?> fut : futures) {
                fut.get();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        service.shutdown();
    }

    static long runTimeTests(Connection connection) {
        try {
            long start = System.currentTimeMillis();

            for (int i = 1; i <= 100; i++) {
                if (i % 10 == 0) System.out.println("Progress: " + i);
                runTransactions(connection);
            }

            return System.currentTimeMillis() - start;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                connection.setAutoCommit(false);
                smtAtomic = new AtomicReference<>(connection.createStatement());

                long time = runTimeTests(connection);
                System.out.println("Execution time: " + time);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
