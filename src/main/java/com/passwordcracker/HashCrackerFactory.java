package com.passwordcracker;

/**
 * Fabrique simple (Simple Factory) responsable de la création
 * des instances de HashCracker.
 *
 * Le programme principal (Main) ne doit JAMAIS instancier directement
 * DictionaryHashCracker ou BruteForceHashCracker : il passe uniquement
 * par cette fabrique, ce qui centralise la logique de création et
 * découple Main des classes concrètes.
 */
public class HashCrackerFactory {

    // Constructeur privé : cette classe n'a pas besoin d'être instanciée,
    // elle n'expose que des méthodes statiques.
    private HashCrackerFactory() {
    }

    /**
     * Crée l'instance de HashCracker correspondant à la méthode demandée.
     *
     * @param method "BRUTE" pour une attaque par force brute,
     *               "DICO" pour une attaque par dictionnaire
     * @return une implémentation de HashCracker
     * @throws IllegalArgumentException si la méthode est null ou inconnue
     */
    public static HashCracker create(String method) {
        if (method == null) {
            throw new IllegalArgumentException("La méthode ne peut pas être null");
        }

        switch (method.toUpperCase()) {
            case "BRUTE":
                return new BruteForceHashCracker();
            case "DICO":
                return new DictionaryHashCracker();
            default:
                throw new IllegalArgumentException(
                        "Méthode inconnue : " + method + " (attendu : BRUTE ou DICO)");
        }
    }
}
