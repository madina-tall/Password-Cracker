package com.passwordcracker;

/**
 * Interface commune à toutes les stratégies de cassage de hash MD5.
 */
public interface HashCracker {

    /**
     * Tente de retrouver le mot de passe correspondant au hash MD5 fourni.
     *
     * @param hash le hash MD5 cible (32 caractères hexadécimaux)
     * @return le mot de passe trouvé, ou {@code null} si aucune correspondance
     */
    String crack(String hash);

    /**
     * @return le nombre de tentatives effectuées lors du dernier appel à {@link #crack(String)}
     */
    int getAttempts();
}
