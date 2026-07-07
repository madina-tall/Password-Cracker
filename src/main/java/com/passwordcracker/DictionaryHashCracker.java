package com.passwordcracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Stratégie de cassage par dictionnaire.
 * Parcourt un fichier contenant une liste de mots et compare leur hash MD5
 * au hash cible jusqu'à trouver une correspondance.
 */
public class DictionaryHashCracker implements HashCracker {

    private static final String DEFAULT_DICTIONARY = "resources/dictionnaire.txt";

    private final String dictionaryPath;
    private int attempts;

    public DictionaryHashCracker() {
        this(DEFAULT_DICTIONARY);
    }

    public DictionaryHashCracker(String dictionaryPath) {
        this.dictionaryPath = dictionaryPath;
    }

    @Override
    public String crack(String hash) {
        attempts = 0;
        String targetHash = hash.toLowerCase().trim();
        Path path = Paths.get(dictionaryPath);

        if (!Files.exists(path)) {
            throw new IllegalArgumentException(
                    "Fichier dictionnaire introuvable : " + dictionaryPath);
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim();
                if (word.isEmpty()) {
                    continue;
                }

                attempts++;
                if (MD5Util.hash(word).equals(targetHash)) {
                    return word;
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Erreur de lecture du dictionnaire", e);
        }

        return null;
    }

    @Override
    public int getAttempts() {
        return attempts;
    }
}
