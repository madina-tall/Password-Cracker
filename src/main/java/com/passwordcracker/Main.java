package com.passwordcracker;

/**
 * Point d'entrée de l'application passwordCracker.
 * Usage : java com.passwordcracker.Main -m BRUTE|DICO -h <hash_md5>
 */
public class Main {

    public static void main(String[] args) {
        String method = null;
        String hash = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-m" -> {
                    if (i + 1 < args.length) {
                        method = args[++i];
                    }
                }
                case "-h" -> {
                    if (i + 1 < args.length) {
                        hash = args[++i];
                    }
                }
                default -> {
                }
            }
        }

        if (method == null || hash == null) {
            System.err.println("Usage : java com.passwordcracker.Main -m BRUTE|DICO -h <hash_md5>");
            System.exit(1);
        }

        HashCracker cracker = HashCrackerFactory.create(method);

        long start = System.nanoTime();
        String password = cracker.crack(hash);
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        if (password != null) {
            System.out.println("Password found: " + password);
        } else {
            System.out.println("Password not found");
        }

        System.out.println("Tentatives : " + cracker.getAttempts());
        System.out.println("Temps d'exécution : " + elapsedMs + " ms");
    }
}
