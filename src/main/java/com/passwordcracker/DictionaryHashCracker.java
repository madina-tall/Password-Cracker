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

    // Chemin par défaut vers le fichier dictionnaire, utilisé si aucun
    // chemin personnalisé n'est fourni au constructeur.
    // "static final" car cette valeur est constante et partagée par toutes les instances.
    private static final String DEFAULT_DICTIONARY = "resources/dictionary.txt";

    // Chemin du dictionnaire utilisé par CETTE instance (immuable une fois défini,
    // d'où le "final" : on ne peut pas le changer après construction).
    private final String dictionaryPath;

    // Compteur du nombre de mots testés lors du dernier appel à crack().
    // Pas "final" car sa valeur change à chaque tentative.
    private int attempts;

    // Constructeur par défaut : utilise le dictionnaire standard du projet.
    // Il délègue au second constructeur via this(...) pour éviter la duplication de code.
    public DictionaryHashCracker() {
        this(DEFAULT_DICTIONARY);
    }

    // Constructeur permettant de spécifier un dictionnaire personnalisé
    // (utile pour les tests unitaires ou un usage avec un autre fichier).
    public DictionaryHashCracker(String dictionaryPath) {
        this.dictionaryPath = dictionaryPath;
    }

    // Méthode principale imposée par l'interface HashCracker.
    // @Override confirme qu'on implémente bien une méthode du contrat de l'interface.
    @Override
    public String crack(String hash) {
        // Réinitialisation du compteur à chaque nouvel essai de cassage,
        // pour que getAttempts() reflète uniquement CET appel.
        attempts = 0;

        // Normalisation du hash cible : minuscules + suppression des espaces,
        // pour garantir une comparaison fiable avec les hash générés par MD5Util
        // (qui produisent probablement aussi des hash en minuscules).
        String targetHash = hash.toLowerCase().trim();

        // Conversion du chemin (String) en objet Path, requis par l'API java.nio.file.
        Path path = Paths.get(dictionaryPath);

        // Vérification préalable de l'existence du fichier.
        // Permet d'échouer rapidement avec un message clair plutôt que de laisser
        // une IOException moins explicite se produire plus tard.
        if (!Files.exists(path)) {
            throw new IllegalArgumentException(
                    "Fichier dictionnaire introuvable : " + dictionaryPath);
        }

        // try-with-resources : garantit que le BufferedReader sera fermé automatiquement
        // (même en cas d'exception), évitant les fuites de ressources.
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;

            // Lecture du fichier ligne par ligne jusqu'à la fin (readLine() renvoie null en EOF).
            // L'affectation dans la condition (line = reader.readLine()) permet de lire
            // ET de tester en une seule expression.
            while ((line = reader.readLine()) != null) {

                // Suppression des espaces/tabulations en début et fin de ligne,
                // pour éviter que "motdepasse " soit hashé différemment de "motdepasse".
                String word = line.trim();

                // On ignore les lignes vides (lignes blanches dans le fichier),
                // qui ne correspondent à aucun mot de passe valide à tester.
                if (word.isEmpty()) {
                    continue;
                }

                // Incrémentation du compteur : chaque mot réellement testé compte
                // comme une tentative de cassage.
                attempts++;

                // Calcul du hash MD5 du mot courant et comparaison avec le hash cible.
                // Si ça correspond, on a trouvé le mot de passe : on le retourne immédiatement
                // (ce qui arrête aussi la lecture du fichier grâce au return).
                if (MD5Util.hash(word).equals(targetHash)) {
                    return word;
                }
            }
        } catch (IOException e) {
            // Toute erreur d'E/S (fichier corrompu, problème de lecture, etc.) est
            // encapsulée dans une exception non vérifiée (unchecked), pour ne pas
            // obliger les appelants de crack() à gérer IOException explicitement.
            throw new IllegalStateException("Erreur de lecture du dictionnaire", e);
        }

        // Si on sort de la boucle sans avoir trouvé de correspondance,
        // aucun mot du dictionnaire ne correspond au hash : on retourne null.
        return null;
    }

    // Méthode d'accès (getter) imposée par l'interface HashCracker,
    // permettant de connaître le nombre de tentatives effectuées lors du dernier crack().
    @Override
    public int getAttempts() {
        return attempts;
    }
}