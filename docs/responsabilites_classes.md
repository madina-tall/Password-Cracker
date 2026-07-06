## Description des responsabilités des classes

### `HashCracker` (interface)
Définit le contrat commun à toutes les stratégies de cassage de mot de passe. Elle expose une unique
méthode, `crack(String hash)`, qui retourne le mot de passe trouvé ou `null` si aucune correspondance
n'est trouvée. C'est cette interface qui permet le polymorphisme : le programme principal manipule un
objet de type `HashCracker` sans avoir besoin de savoir s'il s'agit, concrètement, d'une recherche par
dictionnaire ou par force brute.

### `DictionaryHashCracker`
Implémente `HashCracker` en s'appuyant sur un dictionnaire de mots (fichier texte contenant une liste
de mots courants). Pour chaque mot du dictionnaire, elle calcule son hash MD5 et le compare au hash
recherché ; dès qu'une correspondance est trouvée, elle retourne le mot correspondant. Sa
responsabilité unique est de parcourir un ensemble fini et connu de candidats.

### `BruteForceHashCracker`
Implémente `HashCracker` en générant systématiquement toutes les combinaisons possibles de caractères
à partir de l'alphabet `a` à `z`, jusqu'à une longueur maximale de 4 caractères. Chaque combinaison est
hachée puis comparée au hash cible. Sa responsabilité unique est d'explorer l'espace des combinaisons
de façon exhaustive, sans dépendre d'une source de données externe.

### `HashCrackerFactory`
Centralise la création des objets `HashCracker`. Sa méthode statique `create(String method)` reçoit une
chaîne identifiant la méthode souhaitée (`"DICO"` ou `"BRUTE"`) et retourne l'instance concrète
correspondante (`DictionaryHashCracker` ou `BruteForceHashCracker`). Cette centralisation permet au
programme principal de ne jamais instancier directement une classe concrète, respectant ainsi la
contrainte imposée par l'énoncé et le principe du patron **Simple Factory**.

| Classe                  | Rôle                                              | Dépend de       |
|-------------------------|----------------------------------------------------|-----------------|
| `HashCracker`            | Contrat commun (interface)                        | —               |
| `DictionaryHashCracker`  | Stratégie de cassage par dictionnaire             | `HashCracker`   |
| `BruteForceHashCracker`  | Stratégie de cassage par force brute              | `HashCracker`   |
| `HashCrackerFactory`     | Création centralisée des stratégies               | Les 3 ci-dessus |
