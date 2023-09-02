import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class Main {
    public static BlockingQueue<String> queue1 = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queue2 = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queue3 = new ArrayBlockingQueue<>(100);
    public static Thread generator;
    public static final int TEXT_AMOUNT = 10_000;
    public static final int TEXT_LENGTH = 100_000;
    public static final String letters = "abc";

    public static void main(String[] args) throws InterruptedException {
        generator = new Thread(() -> {
            for (int i = 0; i < TEXT_AMOUNT; i++) {
                String text = generateText(letters, TEXT_LENGTH);
                try {
                    queue1.put(text);
                    queue2.put(text);
                    queue3.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        generator.start();

        Thread findA = getThread(queue1, 'a');
        Thread findB = getThread(queue2, 'b');
        Thread findC = getThread(queue3, 'c');

        findA.start();
        findB.start();
        findC.start();

        findA.join();
        findB.join();
        findC.join();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static Thread getThread(BlockingQueue<String> queue, char letter) {
        return new Thread(() -> {
            int max = count(queue, letter);
            System.out.println("Максимальное кол-во символов '" + letter + "' = " + max);
        });
    }

    public static int count(BlockingQueue<String> queue, char letter) {
        int count = 0;
        int max = 0;
        String text;

        try {
            while (generator.isAlive()) {
                text = queue.take();
                for (char c : text.toCharArray()) {
                    if (c == letter) {
                        count++;
                    }
                }
                if (count > max) {
                    max = count;
                }
                count = 0;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return max;
    }
}