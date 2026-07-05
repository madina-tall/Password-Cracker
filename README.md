# Password-Cracker
Mini-projet d'implémentation d'un password cracker MD5 (BRUTE/DICO) en Java avec le patron de conception Simple Factory.
# PasswordCracker v1

Outil en ligne de commande de cassage de mots de passe à partir de leur empreinte MD5, développé en Java selon le patron de conception **Simple Factory**.

## Sommaire

1. [Introduction](#1-introduction)
2. [Présentation du problème](#2-présentation-du-problème)
3. [Architecture](#3-architecture)
4. [Diagramme UML](#4-diagramme-uml)
5. [Usage du patron Simple Factory](#5-usage-du-patron-simple-factory)
6. [Résultats obtenus](#6-résultats-obtenus)
7. [Difficultés rencontrées](#7-difficultés-rencontrées)
8. [Conclusion](#8-conclusion)
9. [Compilation et exécution](#9-compilation-et-exécution)

---

## 1. Introduction

Dans le domaine de la cybersécurité, les mots de passe ne sont jamais stockés en clair : ils sont transformés par des fonctions de hachage cryptographiques (ici MD5). Vérifier la robustesse d'un mot de passe consiste souvent à essayer de retrouver le mot de passe original à partir de son hash — c'est l'objet de cet outil, `passwordCracker`.

Ce mini-projet, réalisé dans le cadre du cours d'Ingénierie Logicielle à l'ESP/UCAD, avait pour objectif de mettre en pratique :
- la conception orientée objet modulaire,
- le polymorphisme,
- le patron de création **Simple Factory**,
- le développement d'une application console en Java.

**Équipe projet :**

| Rôle | Membre | Responsabilité |
|------|--------|-----------------|
| P1   | Aboulaye | Interface HashCracker, diagramme UML, responsabilités des classes, sections Introduction/Présentation du problème |
| P2   | Emilie| HashCrackerFactory, Main (CLI, parsing -m/-h), sections Architecture/Usage Simple Factory |
| P3   | Aicha | Implémenter BruteForceHashCracker, intégrer le java.secruity.MD5, tester sur plusieurs hashes, rédiger les résultats |
| P4  | Madina | Stratégie dictionnaire (`DictionaryHashCracker`), fichier dictionnaire, tests, dépôt GitHub, vidéo de démo, rédaction du README final |

---

## 2. Présentation du problème

Le programme `passwordCracker` reçoit en entrée :
- une **méthode de cassage** : `BRUTE` (force brute) ou `DICO` (dictionnaire),
- un **hash MD5** à casser.

```
passwordCracker -m DICO -h e7247759c1633c0f9f1485f3690294a9
passwordCracker -m BRUTE -h e7247759c1633c0f9f1485f3690294a9
```

Résultat attendu :
```
Password found: test
```
ou
```
Password not found
```

Deux stratégies de recherche sont implémentées :

- **Cassage par dictionnaire** : le programme charge une liste de mots depuis un fichier (`resources/dictionary.txt`), calcule le hash MD5 de chaque mot et le compare au hash recherché.
- **Cassage par force brute** : le programme génère toutes les combinaisons possibles de lettres minuscules (`a` à `z`) jusqu'à 4 caractères, et teste chacune.

---

## 3. Architecture

L'application repose sur une interface commune implémentée par deux stratégies concrètes, instanciées via une fabrique unique.

### Responsabilités des classes

| Classe | Responsabilité |
|--------|-----------------|
| `HashCracker` (interface) | Définit le contrat commun : `String crack(String hash)`, qui retourne le mot trouvé ou `null`. |
| `DictionaryHashCracker` | Implémente `HashCracker`. Charge un dictionnaire, hash chaque mot en MD5 et compare au hash cible. |
| `BruteForceHashCracker` | Implémente `HashCracker`. Génère exhaustivement toutes les combinaisons de lettres (longueur ≤ 4) et compare leur hash MD5. |
| `HashCrackerFactory` | Fabrique statique centralisant la création des objets `HashCracker` selon le paramètre `method` (`"DICO"` ou `"BRUTE"`). Aucune classe concrète n'est instanciée directement ailleurs dans le code. |
| `Main` | Point d'entrée console : parse les arguments (`-m`, `-h`), délègue la création du cracker à la fabrique, affiche le résultat et les statistiques d'exécution. |

Cette architecture respecte les contraintes imposées : aucune duplication de code entre stratégies (chacune encapsule sa propre logique derrière l'interface commune), et la création d'objets est strictement centralisée dans la fabrique.

---

## 4. Diagramme UML

```
        ┌───────────────────────┐
        │     HashCracker       │  <<interface>>
        │-----------------------│
        │ + crack(hash:String)  │
        │        : String       │
        └───────────▲───────────┘
                    │
        ┌───────────┴────────────┐
        │                        │
┌───────────────────┐  ┌──────────────────────┐
│ DictionaryHashCracker│  │ BruteForceHashCracker│
├───────────────────┤  ├──────────────────────┤
│ + crack(hash)      │  │ + crack(hash)         │
└───────────────────┘  └──────────────────────┘

┌────────────────────────────┐
│    HashCrackerFactory      │
├────────────────────────────┤
│ + create(method:String)    │
│        : HashCracker        │
└────────────────────────────┘
```

> Diagramme illustratif ci-dessus. La version détaillée (image exportée depuis draw.io / PlantUML) est disponible dans [`docs/uml-diagram.png`](docs/uml-diagram.png).

---

## 5. Usage du patron Simple Factory

Le patron **Simple Factory** centralise la logique de création des objets `HashCracker`, ce qui découple le code appelant (`Main`) des classes concrètes.

```java
public class HashCrackerFactory {
    public static HashCracker create(String method) {
        switch (method) {
            case "DICO":
                return new DictionaryHashCracker();
            case "BRUTE":
                return new BruteForceHashCracker();
            default:
                throw new IllegalArgumentException("Méthode inconnue : " + method);
        }
    }
}
```

Utilisation dans `Main` :

```java
HashCracker cracker = HashCrackerFactory.create("DICO");
String password = cracker.crack(hash);
```

Grâce au polymorphisme, `Main` ne connaît jamais `DictionaryHashCracker` ni `BruteForceHashCracker` directement : il manipule uniquement l'interface `HashCracker`.

**Avantages / limites** (voir aussi Annexe) : la fabrique simplifie la création d'objets et isole le code client des implémentations concrètes, mais elle viole le principe *Open/Closed* : ajouter une nouvelle stratégie impose de modifier `HashCrackerFactory` (un `switch` supplémentaire). Cette limite sera corrigée dans le mini-projet suivant (probablement via une Factory Method ou une fabrique enregistrable).

---

## 6. Résultats obtenus

### Tests effectués

| Mot testé | Hash MD5 | Méthode | Résultat |
|-----------|----------|---------|----------|
| password  | [À COMPLÉTER] | DICO | [À COMPLÉTER] |
| admin     | [À COMPLÉTER] | DICO | [À COMPLÉTER] |
| azerty    | [À COMPLÉTER] | DICO | [À COMPLÉTER] |
| test      | [À COMPLÉTER] | BRUTE | [À COMPLÉTER] |

### Statistiques

- Temps d'exécution moyen (DICO) : [À COMPLÉTER]
- Temps d'exécution moyen (BRUTE) : [À COMPLÉTER]
- Nombre de tentatives (BRUTE) : [À COMPLÉTER]

### Vidéo de démonstration

📹 [Lien vers la vidéo de démo (max. 10 min)] — [À COMPLÉTER]

---

## 7. Difficultés rencontrées

- [À COMPLÉTER — ex. gestion des collisions de hash, performance de la force brute, parsing des arguments CLI, coordination Git entre membres, etc.]

---

## 8. Conclusion

[À COMPLÉTER — bilan global : objectifs atteints, apprentissages sur le patron Simple Factory, pistes d'amélioration pour la v2 (respect du principe Open/Closed).]

---

## 9. Compilation et exécution

### Prérequis
- JDK 17 ou supérieur

### Compilation
```bash
javac -d out src/main/java/com/Password-Cracker/*.java
```

### Exécution
```bash
java -cp out com.Password-Cracker.Main -m DICO -h e7247759c1633c0f9f1485f3690294a9
java -cp out com.Password-Cracker.Main -m BRUTE -h e7247759c1633c0f9f1485f3690294a9
```

---

## Annexe — Questions de réflexion

1. **Quels avantages apporte la fabrique simple ?** [À COMPLÉTER]
2. **Quels sont ses inconvénients ?** [À COMPLÉTER]
3. **Que faut-il modifier lorsqu'une nouvelle stratégie est ajoutée ?** [À COMPLÉTER]
4. **La fabrique respecte-t-elle le principe Open/Closed ?** [À COMPLÉTER]
