package com.backbase.assignment;


import java.util.stream.IntStream;

public class App {
    public static void main(String[] args) {
        IntStream.rangeClosed(0, 10).forEach(x -> {
            if (Math.max(-1, x) == Math.min(11, x)) {
                System.out.print(x + " ");
            }
        });

    }
}