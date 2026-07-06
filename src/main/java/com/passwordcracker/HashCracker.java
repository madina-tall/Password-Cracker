/**
 * Interface commune à toutes les stratégies de cassage de mot de passe.
 * Chaque implémentation concrète propose une méthode différente pour
 * retrouver un mot de passe à partir de son empreinte (hash) MD5.
 */
public interface HashCracker {

    /**
     * Tente de retrouver le mot de passe correspondant au hash donné.
     *
     * @param hash le hash MD5 recherché
     * @return le mot de passe trouvé, ou {@code null} si aucune
     *         correspondance n'a été trouvée
     */
    String crack(String hash);
}
