package com.passwordcracker;

/**
 * Application console PasswordCracker.
 *
 * Usage :
 *   java com.passwordcracker.Main -m BRUTE -h e7247759c1633c0f9f1485f3690294a9
 *   java com.passwordcracker.Main -m DICO  -h e7247759c1633c0f9f1485f3690294a9
 *
 * Main ne connaît que l'interface HashCracker : la création de l'objet
 * concret est entièrement déléguée à HashCrackerFactory (patron Simple Factory).
 */
public class Main {

    public static void main(String[] args) {
        String method = null;
        String hash = null;

        // --- Parsing simple des arguments de la ligne de commande ---
        // On parcourt args[] et dès qu'on trouve "-m" ou "-h", on récupère
        // la valeur juste après (args[++i] incrémente i pour ne pas la relire)
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-m":
                    if (i + 1 < args.length) {
                        method = args[++i]; // ex: "BRUTE" ou "DICO"
                    }
                    break;
                case "-h":
                    if (i + 1 < args.length) {
                        hash = args[++i]; // le hash MD5 cible
                    }
                    break;
                default:
                    // argument inconnu, on l'ignore silencieusement
                    break;
            }
        }

        // Si un des deux arguments obligatoires manque, on affiche l'usage
        // et on quitte avec un code d'erreur (1 = échec)
        if (method == null || hash == null) {
            System.out.println("Usage : java com.passwordcracker.Main -m <BRUTE|DICO> -h <hashMD5>");
            System.exit(1);
            return;
        }

        try {
            // --- Création de la stratégie via la fabrique simple ---
            // Main ne sait pas si c'est un BruteForceHashCracker ou un
            // DictionaryHashCracker : il manipule juste l'interface HashCracker
            HashCracker cracker = HashCrackerFactory.create(method);

            // --- Exécution + mesure du temps ---
            // On chronomètre l'appel à crack() pour comparer les performances
            // entre les deux stratégies (utile pour la démo/rapport)
            long start = System.currentTimeMillis();
            String result = cracker.crack(hash);
            long elapsed = System.currentTimeMillis() - start;

            // --- Affichage du résultat ---
            if (result != null) {
                System.out.println("Password found: " + result);
            } else {
                System.out.println("Password not found");
            }

            System.out.println("Temps d'exécution : " + elapsed + " ms");

            // Nombre de mots/combinaisons testés avant de trouver (ou d'abandonner).
            // Fonctionne pour les deux stratégies grâce au polymorphisme :
            // chacune implémente getAttempts() à sa manière
            System.out.println("Nombre de tentatives : " + cracker.getAttempts());

        } catch (IllegalArgumentException e) {
            // Attrape les erreurs venant de la factory (méthode inconnue)
            // ou du cracker lui-même (dictionnaire introuvable, etc.)
            System.out.println("Erreur : " + e.getMessage());
            System.exit(1);
        }
    }
}