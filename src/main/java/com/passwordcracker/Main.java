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
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-m":
                    if (i + 1 < args.length) {
                        method = args[++i];
                    }
                    break;
                case "-h":
                    if (i + 1 < args.length) {
                        hash = args[++i];
                    }
                    break;
                default:
                    // argument inconnu, on l'ignore silencieusement
                    break;
            }
        }

        if (method == null || hash == null) {
            System.out.println("Usage : java com.passwordcracker.Main -m <BRUTE|DICO> -h <hashMD5>");
            System.exit(1);
            return;
        }

        try {
            // --- Création de la stratégie via la fabrique simple ---
            HashCracker cracker = HashCrackerFactory.create(method);

            // --- Exécution + mesure du temps ---
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

        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
            System.exit(1);
        }
    }
}
