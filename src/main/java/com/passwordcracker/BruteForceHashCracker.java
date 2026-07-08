package com.passwordcracker;

/**
 * Stratégie de cassage par force brute.
 * Génère toutes les combinaisons possibles sur l'alphabet minuscule
 * anglais, pour des longueurs de 1 à 4 caractères.
 */
public class BruteForceHashCracker implements HashCracker {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final int MAX_LENGTH = 4;

    private int attempts;

    @Override
    public String crack(String hash) {
        attempts = 0;
        String targetHash = hash.toLowerCase().trim();

        for (int length = 1; length <= MAX_LENGTH; length++) {
            String found = search(targetHash, new char[length], 0);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    private String search(String targetHash, char[] current, int position) {
        if (position == current.length) {
            attempts++;
            String candidate = new String(current);
            if (MD5Util.hash(candidate).equals(targetHash)) {
                return candidate;
            }
            return null;
        }

        for (int i = 0; i < ALPHABET.length(); i++) {
            current[position] = ALPHABET.charAt(i);
            String found = search(targetHash, current, position + 1);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    @Override
    public int getAttempts() {
        return attempts;
    }
}
