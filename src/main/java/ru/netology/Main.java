package ru.netology;

import java.util.*;


public class Main {
    public final static Map<Integer, Integer> sizeToFreq        = new HashMap<>();
    final static        String                LETTERS           = "RLRFR";
    final static        int                   ROUTE_LENGTH      = 100;
    final static        int                   AMOUNT_OF_THREADS = 1000;

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threadList = new ArrayList<>();
        Thread printer = new Thread(() -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                    printLeader();
                }
            }
        });
        printer.start();

        for (int i = 0; i < AMOUNT_OF_THREADS; i++) {
            threadList.add(getThread());
        }
        for (Thread thread : threadList) {
            thread.start();
            thread.join();
        }
        printer.interrupt();

    }

    public static Thread getThread() {
        return new Thread(() -> {
            String route = generateRoute(LETTERS, ROUTE_LENGTH);
            int frequency = (int) route.chars().filter(ch -> ch == 'R').count();
            synchronized (sizeToFreq) {
                if (sizeToFreq.containsKey(frequency)) {
                    sizeToFreq.put(frequency, sizeToFreq.get(frequency) + 1);
                } else {
                    sizeToFreq.put(frequency, 1);
                }
                sizeToFreq.notify();
            }
        });
    }

    public static void printLeader() {
        Map.Entry<Integer, Integer> max = sizeToFreq
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get();
        System.out.println("Текущий лидер " + max.getKey()
                + " (встретилось " + max.getValue() + " раз)");
    }
}