# Mini-Projet 1 – PasswordCracker v1

Application Java de cassage de mots de passe MD5 implémentant le patron de conception **Simple Factory**, dans le cadre d'un cours sur les design patterns.

---

## 1. Introduction

Dans le domaine de la cybersécurité, la vérification de la robustesse des mots de passe est une étape
essentielle de tout audit de sécurité. Les mots de passe ne sont généralement jamais stockés en clair
dans une base de données : ils sont transformés à l'aide de fonctions de hachage cryptographiques,
comme MD5, afin de limiter les risques en cas de fuite de données.

Ce mini-projet propose une première version d'un outil en ligne de commande, `passwordCracker`,
capable de retrouver un mot de passe à partir de son empreinte MD5. Deux méthodes de cassage sont
implémentées : une recherche par dictionnaire et une recherche par force brute.

Au-delà de l'aspect fonctionnel, l'objectif pédagogique de ce projet est de mettre en pratique une
architecture orientée objet modulaire, en s'appuyant sur le polymorphisme et sur le patron de
conception créationnel **Simple Factory**, qui centralise la création des objets et découple le
programme principal des classes concrètes.

**Équipe projet :**

| Rôle | Membre | Responsabilité |
|------|--------|-----------------|
| P1 | Abdoulaye | Interface HashCracker, diagramme UML, responsabilités des classes |
| P2 | Emilie | HashCrackerFactory, Main (CLI), sections Architecture/Usage Simple Factory |
| P3 | Aïcha | BruteForceHashCracker, intégration MD5, tests, résultats |
| P4 | Madina | DictionaryHashCracker, fichier dictionnaire, tests, dépôt GitHub, vidéo, README final |

---

## 2. Présentation du problème

### Contexte

Lorsqu'un utilisateur choisit un mot de passe, le système calcule son hash MD5 et stocke uniquement cette empreinte. Pour vérifier un mot de passe saisi, le système recalcule le hash et le compare à celui stocké.

### Problème à résoudre

Étant donné un hash MD5, retrouver le mot de passe correspondant en utilisant l'une des stratégies suivantes :

| Méthode | Description |
|---------|-------------|
| `DICO`  | Parcours d'un fichier dictionnaire contenant des mots courants |
| `BRUTE` | Génération exhaustive de toutes les combinaisons (a-z, longueur 1 à 4) |

### Entrées et sorties

**Commande (exemple du sujet) :**
```bash
java com.passwordcracker.Main -m DICO -h e7247759c1633c0f9f1485f3690294a9
```

**Commande de test fonctionnelle (MD5 réel de "test") :**
```bash
java com.passwordcracker.Main -m DICO -h 098f6bcd4621d373cade4e832627b4f6
```

**Sortie attendue :**
```
Password found: test
Nombre de tentatives : 6
Temps d'exécution : 12 ms
```

En cas d'échec :
```
Password not found
Nombre de tentatives : 20
Temps d'exécution : 8 ms
```

> **Note :** le hash `e7247759c1633c0f9f1485f3690294a9` figurant dans le sujet ne correspond pas au MD5 standard de `test` (`098f6bcd4621d373cade4e832627b4f6`). L'implémentation utilise l'algorithme MD5 Java standard.

---

## 3. Architecture

Le projet suit une architecture en couches basée sur deux patrons :

### Patron Strategy (stratégies concrètes)

Chaque algorithme de cassage implémente l'interface `HashCracker` :

```
HashCracker (interface)
    ├── DictionaryHashCracker   → lecture du fichier dictionnaire
    └── BruteForceHashCracker   → génération exhaustive a-z (max 4 car.)
```

### Patron Simple Factory (création centralisée)

La classe `HashCrackerFactory` est le seul point de création des stratégies. Le programme principal (`Main`) ne connaît que l'interface `HashCracker` et ne doit **jamais** instancier directement les classes concrètes.

### Choix de conception : la méthode `getAttempts()`

Le sujet n'imposait pas explicitement de méthode pour compter les tentatives. Nous avons choisi d'ajouter `getAttempts()` à l'interface `HashCracker`, implémentée par les deux stratégies concrètes, car elle permet de **comparer objectivement les performances** de `DICO` et `BRUTE` (nombre d'essais nécessaires avant de trouver le mot de passe, ou avant d'épuiser toutes les possibilités). Ce choix illustre aussi concrètement l'intérêt du polymorphisme : `Main` appelle `cracker.getAttempts()` sans savoir quelle implémentation est utilisée.

### Structure du projet

```
Password-Cracker/
├── resources/
│   └── dictionary.txt          # Fichier de mots pour la stratégie DICO
├── src/main/java/com/passwordcracker/
│   ├── HashCracker.java          # Interface commune
│   ├── DictionaryHashCracker.java
│   ├── BruteForceHashCracker.java
│   ├── HashCrackerFactory.java   # Fabrique simple
│   ├── MD5Util.java              # Utilitaire MD5 partagé
│   └── Main.java                 # Application console
└── README.md
```

### Responsabilités des classes

| Classe | Responsabilité |
|--------|----------------|
| `HashCracker` | Définit le contrat `crack(hash)` et `getAttempts()` |
| `DictionaryHashCracker` | Charge `resources/dictionary.txt`, hash chaque mot, compare au hash cible |
| `BruteForceHashCracker` | Génère toutes les combinaisons a-z (1 à 4 caractères) et teste chaque hash |
| `HashCrackerFactory` | Crée l'instance appropriée selon la méthode (`BRUTE` ou `DICO`) |
| `MD5Util` | Centralise le calcul MD5 (évite la duplication de code) |
| `Main` | Parse les arguments CLI, délègue à la fabrique, affiche les résultats |

---

## 4. Diagramme UML

```
        ┌───────────────────────┐
        │     HashCracker       │  <<interface>>
        │-----------------------│
        │ + crack(hash:String)  │
        │        : String       │
        │ + getAttempts()       │
        │        : int          │
        └───────────▲───────────┘
                    │
        ┌───────────┴────────────┐
        │                        │
┌───────────────────┐  ┌──────────────────────┐
│ DictionaryHashCracker│  │ BruteForceHashCracker│
├───────────────────┤  ├──────────────────────┤
│ + crack(hash)      │  │ + crack(hash)         │
│ + getAttempts()     │  │ + getAttempts()        │
└───────────────────┘  └──────────────────────┘

┌────────────────────────────┐
│    HashCrackerFactory      │
├────────────────────────────┤
│ + create(method:String)    │
│        : HashCracker        │
└────────────────────────────┘
```

> Diagramme illustratif ci-dessus. La version détaillée (image exportée depuis draw.io / PlantUML) est disponible dans [`docs/uml_diagram.svg`](docs/uml_diagram.svg).

---

## 5. Usage du patron Simple Factory

### Principe

La **fabrique simple** (`HashCrackerFactory`) encapsule toute la logique de création des objets. Le client (ici `Main`) demande une stratégie par son nom sans connaître la classe concrète :

```java
HashCracker cracker = HashCrackerFactory.create("DICO");
String password = cracker.crack("e7247759c1633c0f9f1485f3690294a9");
```

### Implémentation

```java
public class HashCrackerFactory {

    private HashCrackerFactory() {
        // constructeur privé : classe utilitaire, pas d'instanciation
    }

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
```

Utilisation dans `Main` :

```java
HashCracker cracker = HashCrackerFactory.create("DICO");
String password = cracker.crack(hash);
System.out.println("Nombre de tentatives : " + cracker.getAttempts());
```

Grâce au polymorphisme, `Main` ne connaît jamais `DictionaryHashCracker` ni `BruteForceHashCracker` directement : il manipule uniquement l'interface `HashCracker`.

### Fonctionnement de `DictionaryHashCracker`

1. Ouvre le fichier `resources/dictionary.txt`
2. Pour chaque ligne (mot) :
   - Calcule le hash MD5 du mot via `MD5Util`
   - Compare au hash cible
   - Retourne le mot si correspondance trouvée
3. Retourne `null` si aucun mot ne correspond

### Avantages / limites

La fabrique simplifie la création d'objets et isole le code client des implémentations concrètes, mais elle viole le principe *Open/Closed* : ajouter une nouvelle stratégie impose de modifier `HashCrackerFactory` (un `switch` supplémentaire). Cette limite sera corrigée dans le mini-projet suivant (probablement via une Factory Method ou une fabrique enregistrable). Voir la section 9 pour le détail de cette analyse.

---

## 6. Résultats obtenus

### Compilation

```bash
cd Password-Cracker
javac -d out src/main/java/com/passwordcracker/*.java
```

### Exécution

```bash
# Cassage par dictionnaire — hash MD5 de "test"
java -cp out com.passwordcracker.Main -m DICO -h 098f6bcd4621d373cade4e832627b4f6

# Cassage par force brute — hash MD5 de "abc"
java -cp out com.passwordcracker.Main -m BRUTE -h 900150983cd24fb0d6963f7d28e17f72

# Mot de passe absent du dictionnaire
java -cp out com.passwordcracker.Main -m DICO -h 00000000000000000000000000000000
```

### Résultats attendus

| Commande | Résultat |
|----------|----------|
| `-m DICO -h 098f6bcd4621d373cade4e832627b4f6` | `Password found: test` (6 tentatives) |
| `-m DICO -h 5f4dcc3b5aa765d61d8327deb882cf99` | `Password found: password` |
| `-m BRUTE -h 900150983cd24fb0d6963f7d28e17f72` | `Password found: abc` |
| `-m DICO -h 00000000000000000000000000000000` | `Password not found` |

### Démonstration vidéo

> Lien vers la vidéo de démonstration (max 10 min) : *[à compléter par l'étudiant]*

---

## 7. Difficultés rencontrées

- **Chemin du dictionnaire** : le fichier `resources/dictionary.txt` doit être accessible depuis le répertoire de travail courant lors de l'exécution. Il faut lancer le programme depuis la racine du projet.
- **Normalisation du hash** : les comparaisons sont effectuées en minuscules (`hash.toLowerCase()`) pour éviter les faux négatifs.
- **Performance de la force brute** : avec 26¹ + 26² + 26³ + 26⁴ = 475 254 combinaisons, le mode BRUTE reste rapide pour des mots courts mais deviendrait impraticable avec des longueurs supérieures.
- **Violation du Open/Closed** : l'ajout d'une nouvelle stratégie nécessite de modifier `HashCrackerFactory`, ce qui sera corrigé dans le mini-projet suivant.
- **Gestion des branches Git** : le travail en équipe (une branche par membre) a nécessité une vigilance particulière pour éviter les commits directs sur `main` et pour intégrer proprement les modifications croisées entre fichiers de différents membres.

---

## 8. Conclusion

Ce mini-projet démontre l'intérêt du patron **Simple Factory** pour découpler le code client des classes concrètes. L'application `passwordCracker` offre deux stratégies interchangeables via une interface commune, respectant les contraintes d'architecture imposées.

La stratégie **DICO** est efficace pour des mots de passe courants présents dans le dictionnaire, tandis que **BRUTE** garantit une couverture exhaustive mais limitée (4 caractères max). La limitation principale identifiée — la modification de la fabrique à chaque nouvelle stratégie — sera abordée dans le prochain mini-projet.

---

## 9. Questions de réflexion

### 1. Quels avantages apporte la fabrique simple ?

- **Centralisation de la création** : toute la logique d'instanciation est regroupée dans `HashCrackerFactory`. Si la création d'un objet change (nouveau constructeur, paramètres), un seul fichier est modifié.
- **Découplage** : le code client (`Main`) manipule uniquement l'interface `HashCracker` sans connaître les classes concrètes (`DictionaryHashCracker`, `BruteForceHashCracker`).
- **Lisibilité** : `HashCrackerFactory.create("DICO")` est plus expressif et maintenable qu'un `new DictionaryHashCracker()` dispersé dans le code.
- **Respect des contraintes** : la fabrique garantit que les classes concrètes ne sont jamais instanciées directement dans le programme principal.

### 2. Quels sont ses inconvénients ?

- **Violation du principe Open/Closed** : chaque nouvelle stratégie oblige à modifier le code source de `HashCrackerFactory` (ajout d'un `case` dans le `switch`).
- **Responsabilité unique** : la fabrique finit par connaître toutes les classes concrètes, ce qui augmente son couplage avec le reste du système.
- **Pas d'extensibilité dynamique** : on ne peut pas ajouter une nouvelle stratégie à l'exécution sans recompiler.
- **Testabilité** : la fabrique statique est plus difficile à mocker ou substituer dans des tests unitaires comparée à une injection de dépendances.

### 3. Que faut-il modifier lorsqu'une nouvelle stratégie est ajoutée ?

Pour ajouter par exemple une stratégie `RAINBOW` (table arc-en-ciel), il faut :

1. **Créer** une nouvelle classe `RainbowHashCracker` implémentant `HashCracker`
2. **Modifier** `HashCrackerFactory` pour ajouter le cas `"RAINBOW"` dans la méthode `create()`
3. **Mettre à jour** la documentation et les tests de validation

Le code client (`Main`) n'a **pas** besoin d'être modifié grâce au polymorphisme, mais la fabrique elle-même doit être modifiée — c'est la limitation centrale de ce patron.

### 4. La fabrique respecte-t-elle le principe Open/Closed ?

**Non**, la fabrique simple ne respecte pas pleinement le principe **Open/Closed** (ouvert à l'extension, fermé à la modification).

- **Ouvert à l'extension** : on peut créer de nouvelles classes implémentant `HashCracker` sans toucher aux stratégies existantes.
- **Fermé à la modification** : **non respecté** — chaque nouvelle stratégie impose de modifier `HashCrackerFactory`.

Comme indiqué dans le sujet, cette limitation sera corrigée dans le mini-projet suivant, probablement via un patron **Factory Method** ou un **registre dynamique** (Map de fournisseurs), permettant d'enregistrer de nouvelles stratégies sans modifier la fabrique existante.

### Bilan global

Ce premier mini-projet nous a permis de mettre en pratique concrètement le patron **Simple Factory** et de comprendre ses avantages (découplage, centralisation) mais aussi ses limites (violation du principe Open/Closed). Le travail en équipe sur GitHub, avec une branche dédiée par membre, nous a aussi confrontés aux réalités du développement collaboratif : gestion des conflits, discipline de branchement, revue croisée du code. Pour la v2, nous envisageons de remplacer la fabrique simple par une **Factory Method** ou un **registre de stratégies**, afin de pouvoir ajouter de nouvelles méthodes de cassage sans modifier le code existant.
