package com.passwordcracker;

import java.security.MessageDigest;

/**
 * Stratégie de cassage par force brute (version simple, sans récursion).
 *
 * Principe : on essaie TOUS les mots possibles avec les lettres a-z,
 * d'abord les mots de 1 lettre, puis 2 lettres, puis 3, puis 4.
 * Pour chaque mot essayé, on calcule son hash MD5 et on le compare
 * au hash qu'on cherche. Si ça correspond, on a trouvé le mot de passe.
 */
public class BruteForceHashCracker implements HashCracker {

    // L'alphabet utilisé pour générer les mots
    private static final String LETTRES = "abcdefghijklmnopqrstuvwxyz";

    // Compteur du nombre de mots essayés (utile pour le rapport)
    private int nombreTentatives = 0;

    @Override
    public String crack(String hashRecherche) {
        nombreTentatives = 0;

        // On essaie d'abord les mots de 1 lettre
        String resultat = essayerMotsDe1Lettre(hashRecherche);
        if (resultat != null) return resultat;

        // Puis les mots de 2 lettres
        resultat = essayerMotsDe2Lettres(hashRecherche);
        if (resultat != null) return resultat;

        // Puis les mots de 3 lettres
        resultat = essayerMotsDe3Lettres(hashRecherche);
        if (resultat != null) return resultat;

        // Puis les mots de 4 lettres
        resultat = essayerMotsDe4Lettres(hashRecherche);
        if (resultat != null) return resultat;

        // Si on a tout essayé et rien trouvé
        return null;
    }

    @Override
    public int getAttempts() {
        return nombreTentatives;
    }

    // ---- Mots de 1 lettre : a, b, c, ..., z ----
    private String essayerMotsDe1Lettre(String hashRecherche) {
        for (int i = 0; i < LETTRES.length(); i++) {
            String mot = "" + LETTRES.charAt(i);
            if (verifier(mot, hashRecherche)) {
                return mot;
            }
        }
        return null;
    }

    // ---- Mots de 2 lettres : aa, ab, ac, ..., zz ----
    private String essayerMotsDe2Lettres(String hashRecherche) {
        for (int i = 0; i < LETTRES.length(); i++) {
            for (int j = 0; j < LETTRES.length(); j++) {
                String mot = "" + LETTRES.charAt(i) + LETTRES.charAt(j);
                if (verifier(mot, hashRecherche)) {
                    return mot;
                }
            }
        }
        return null;
    }

    // ---- Mots de 3 lettres : aaa, aab, ..., zzz ----
    private String essayerMotsDe3Lettres(String hashRecherche) {
        for (int i = 0; i < LETTRES.length(); i++) {
            for (int j = 0; j < LETTRES.length(); j++) {
                for (int k = 0; k < LETTRES.length(); k++) {
                    String mot = "" + LETTRES.charAt(i) + LETTRES.charAt(j) + LETTRES.charAt(k);
                    if (verifier(mot, hashRecherche)) {
                        return mot;
                    }
                }
            }
        }
        return null;
    }

    // ---- Mots de 4 lettres : aaaa, aaab, ..., zzzz ----
    private String essayerMotsDe4Lettres(String hashRecherche) {
        for (int i = 0; i < LETTRES.length(); i++) {
            for (int j = 0; j < LETTRES.length(); j++) {
                for (int k = 0; k < LETTRES.length(); k++) {
                    for (int l = 0; l < LETTRES.length(); l++) {
                        String mot = "" + LETTRES.charAt(i) + LETTRES.charAt(j) + LETTRES.charAt(k) + LETTRES.charAt(l);
                        if (verifier(mot, hashRecherche)) {
                            return mot;
                        }
                    }
                }
            }
        }
        return null;
    }

    // Calcule le hash MD5 du mot essayé et le compare au hash recherché
    private boolean verifier(String mot, String hashRecherche) {
        nombreTentatives++;
        String hashDuMot = calculerMD5(mot);
        return hashDuMot.equals(hashRecherche);
    }

    // Transforme un mot en son hash MD5 (sous forme de texte hexadécimal)
    private String calculerMD5(String mot) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] octets = md5.digest(mot.getBytes());

            // On transforme chaque octet en 2 caractères hexadécimaux (ex: 9f)
            StringBuilder texteHash = new StringBuilder();
            for (byte octet : octets) {
                texteHash.append(String.format("%02x", octet));
            }
            return texteHash.toString();

        } catch (Exception e) {
            // Ne devrait jamais arriver, MD5 existe toujours dans Java
            return "";
        }
    }
}
