package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];

        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        long startTs = System.currentTimeMillis(); // start time

//        Создали лист из будущих решений
        List<Future<String>> futureList = new ArrayList<>();

//      создали пул из 25 потоков
        final ExecutorService threadPool = Executors.newFixedThreadPool(25);

//        для каждой строки массива texts создали задачу Callable
        for (int i = 0; i < texts.length; i++) {
            int finalI = i;
            Callable<String> myCallable = () -> {
                int maxSize = 0;
                for (int i1 = 0; i1 < texts[finalI].length(); i1++) {
                    for (int j = 0; j < texts[finalI].length(); j++) {
                        if (i1 >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = finalI; k < j; k++) {
                            if (texts[finalI].charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i1) {
                            maxSize = j - i1;
                        }
                    }
                }
                return (texts[finalI].substring(0, 100) + " -> " + maxSize);
            };
            futureList.add(threadPool.submit(myCallable));
        }

        int maxValue = 0;
        for (int i = 0; i < futureList.size(); i++) {
            String result = futureList.get(i).get();
            String[] str = result.split(" -> ");
            int value = Integer.parseInt(str[1]);
            if (maxValue < value) {
                maxValue = value;
                System.out.println("На итерации " + i + " наибольшее значение повторений буквы b составило " + value);
            }
        }
        threadPool.shutdown();

        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

}
