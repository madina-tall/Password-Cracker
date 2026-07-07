package com.passwordcracker;

/**
 * Fabrique simple centralisant la création des stratégies de cassage.
 * Le programme principal ne doit jamais instancier directement les classes concrètes.
 */
public class HashCrackerFactory {

    private HashCrackerFactory() {
    }

    public static HashCracker create(String method) {
        if (method == null || method.isBlank()) {
            throw new IllegalArgumentException("La méthode de cassage est obligatoire (BRUTE ou DICO).");
        }

        return switch (method.toUpperCase()) {
            case "DICO" -> new DictionaryHashCracker();
            case "BRUTE" -> new BruteForceHashCracker();
            default -> throw new IllegalArgumentException(
                    "Méthode inconnue : " + method + ". Valeurs acceptées : BRUTE, DICO.");
        };
    }
}
