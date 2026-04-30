  Le Petit Bac — Jeu du Baccalauréat

Un jeu du **Petit Bac** (Baccalauréat) multijoueur et solo développé en **Java** avec **JavaFX**, incluant un système de validation de mots par **IA (Groq API)**, une architecture **client-serveur par sockets**, et une persistance des données via **Hibernate + MySQL**.

---

##  Table des matières

- [Fonctionnalités](#-fonctionnalités)
- [Captures d'écran](#-captures-décran)
- [Architecture du projet](#-architecture-du-projet)
- [Prérequis](#-prérequis)
- [Installation & Lancement](#-installation--lancement)
- [Configuration de la base de données](#-configuration-de-la-base-de-données)
- [Comment jouer](#-comment-jouer)
- [Technologies utilisées](#-technologies-utilisées)
- [Auteurs](#-auteurs)

---

## Fonctionnalités

### Mode Solo
- Choix des catégories et de la durée de la partie
- Lettre aléatoire générée à chaque manche
- Validation automatique des mots via l'API Groq (IA)
- Affichage du score final avec le nombre de mots corrects

### Mode Multijoueur
- Système **hôte/invité** : un joueur crée la partie, les autres rejoignent via un code
- Communication en temps réel via **sockets TCP**
- Lobby avec liste des joueurs connectés
- Configuration partagée (catégories, durée) par l'hôte
- Soumission simultanée des réponses avec salle d'attente
- Calcul des scores :
  - **10 points** pour un mot unique et valide
  - **5 points** pour un mot valide en commun avec un autre joueur
  - **0 point** pour un mot invalide ou vide
- Affichage des résultats et classement final

### Validation intelligente
- Vérification via **API Groq** (LLM) : le mot appartient-il à la catégorie et commence-t-il par la bonne lettre ?
- **Cache local en base de données** : les mots déjà validés sont stockés pour éviter les appels API répétés

### Effets & UI
- Interface stylisée avec CSS personnalisé (thème rose/moderne)
- Animations JavaFX : fade-in des catégories, zoom au focus, pulsation du timer
- Effets sonores : pop, tick (dernières 10 secondes), alarme de fin

---

##  Captures d'écran

### Écran d'accueil
<img width="1192" height="932" alt="image" src="https://github.com/user-attachments/assets/27e3b8af-c940-4200-939d-6d13715abec6" />


### Configuration Solo
<img width="1210" height="924" alt="image" src="https://github.com/user-attachments/assets/74f693cd-8d6d-44a1-8f96-f5ed4f595e66" />

### Écran de jeu
<img width="1166" height="856" alt="image" src="https://github.com/user-attachments/assets/12a4ad2b-9c60-495b-aa7d-fb181d71a59f" />

### Lobby Multijoueur
<img width="1107" height="373" alt="image" src="https://github.com/user-attachments/assets/9a05ca36-cad9-42d7-8258-286aaf808e28" />



### Salle d'attente
<img width="1209" height="945" alt="image" src="https://github.com/user-attachments/assets/773ab8c7-aa52-495c-bc91-ff8be783599a" />


### Résultats Multijoueur
<img width="1210" height="941" alt="image" src="https://github.com/user-attachments/assets/9aea68d6-b7c6-4952-9f27-218e061caba5" />


##  Architecture du projet

```
Baccalaureat_game/
├── pom.xml                          # Configuration Maven & dépendances
├── src/main/java/
│   ├── com/example/java_project/    # Contrôleurs JavaFX
│   │   ├── HelloApplication.java    # Point d'entrée de l'application
│   │   ├── StartController.java     # Écran d'accueil (Solo / Multi)
│   │   ├── SoloSetupController.java # Configuration partie solo
│   │   ├── GameController.java      # Écran de jeu (timer, saisie)
│   │   ├── LobbyController.java     # Lobby multijoueur
│   │   ├── WaitingController.java   # Salle d'attente (fin de manche)
│   │   ├── ResultsController.java   # Résultats multijoueur
│   │   ├── SoloResultsController.java # Résultats solo
│   │   └── database/
│   │       └── HibernateUtil.java   # Configuration Hibernate
│   │
│   ├── models/                      # Modèles de données
│   │   ├── Player.java              # Joueur (pseudo, score, hôte)
│   │   ├── Category.java            # Catégorie (Ville, Animal, etc.)
│   │   ├── Word.java                # Mot validé (cache DB)
│   │   ├── GameSession.java         # Session de jeu
│   │   ├── GameState.java           # États : LOBBY, PLAYING, FINISHED
│   │   ├── GameMode.java            # Modes : SOLO, Multiplayer
│   │   ├── Round.java               # Manche (lettre, durée)
│   │   ├── ScoreResult.java         # Résultat d'un joueur
│   │   └── WordScore.java           # Score d'un mot (unique/commun/invalide)
│   │
│   ├── services/                    # Logique métier
│   │   ├── GameService.java         # Gestion des sessions et scores
│   │   ├── ValidationService.java   # Validation des mots (cache + API)
│   │   ├── APIService.java          # Appels à l'API Groq
│   │   ├── CodeConverter.java       # Conversion IP ↔ code de partie
│   │   └── SoundService.java        # Effets sonores
│   │
│   ├── sockets/                     # Réseau multijoueur
│   │   ├── GameServer.java          # Serveur TCP (gère les connexions)
│   │   ├── GameClient.java          # Client TCP (communique avec le serveur)
│   │   ├── ClientHandler.java       # Thread par client côté serveur
│   │   └── RunServer.java           # Lancement standalone du serveur
│   │
│   ├── DAO/                         # Accès aux données
│   │   ├── CategoryDAO.java         # CRUD catégories
│   │   └── WordDAO.java             # CRUD mots validés
│   │
│   └── module-info.java             # Configuration des modules Java
│
├── src/main/resources/
│   ├── hibernate.cfg.xml            # Configuration Hibernate/MySQL
│   └── com/example/java_project/
│       ├── start_view.fxml          # Vue écran d'accueil
│       ├── solo-setup-view.fxml     # Vue configuration solo
│       ├── game-view.fxml           # Vue écran de jeu
│       ├── lobby-view.fxml          # Vue lobby multijoueur
│       ├── waiting-view.fxml        # Vue salle d'attente
│       ├── results-view.fxml        # Vue résultats multi
│       ├── solo-results-view.fxml   # Vue résultats solo
│       ├── styles.css               # Styles CSS
│       ├── images/                  # Ressources images
│       └── sounds/                  # Effets sonores (pop, tick, alarme)
```

---

##  Prérequis

| Outil       | Version requise |
|-------------|-----------------|
| **JDK**     | 21 ou supérieur |
| **Maven**   | 3.8+ (ou utiliser le wrapper `mvnw` inclus) |
| **MySQL**   | 8.0+            |

---

##  Installation & Lancement

### 1. Cloner le projet

```bash
git clone https://github.com/nisrineregragui/Baccalaureat_game.git
cd Baccalaureat_game
```

### 2. Configurer JAVA_HOME

Assurez-vous que la variable `JAVA_HOME` pointe vers votre installation JDK :

```powershell
# Windows (PowerShell)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-<votre-version>"
```

```bash
# Linux / macOS
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
```

### 3. Lancer l'application

```bash
# Avec le wrapper Maven inclus (Windows)
.\mvnw.cmd clean javafx:run

# Avec le wrapper Maven inclus (Linux/macOS)
./mvnw clean javafx:run

# Ou avec Maven installé globalement
mvn clean javafx:run
```

---

## Configuration de la base de données

L'application utilise **MySQL** via **Hibernate**. La configuration se trouve dans `src/main/resources/hibernate.cfg.xml` :

```xml
<property name="connection.url">jdbc:mysql://localhost:3306/GAME?createDatabaseIfNotExist=true</property>
<property name="connection.username">root</property>
<property name="connection.password">VotreMotDePasse</property>
```

> **Note :** La base de données `GAME` est créée automatiquement au premier lancement grâce à `createDatabaseIfNotExist=true`. Les tables sont également générées automatiquement par Hibernate (`hbm2ddl.auto=update`).

**Assurez-vous que MySQL est en cours d'exécution avant de lancer l'application.**

---

##  Comment jouer

### Mode Solo
1. Cliquez sur **"Jouer Seul"** depuis l'écran d'accueil
2. Sélectionnez les **catégories** souhaitées
3. Choisissez la **durée** de la manche
4. Une **lettre aléatoire** est générée — trouvez un mot par catégorie commençant par cette lettre
5. Soumettez vos réponses avant la fin du temps — les mots sont validés par l'IA
6. Consultez votre **score final**

### Mode Multijoueur
1. Cliquez sur **"Multijoueur"** et entrez votre **pseudo**
2. **Créer une partie** : vous devenez l'hôte et recevez un **code de partie**
3. **Rejoindre** : entrez le code fourni par l'hôte (ou `LOCALHOST` pour jouer en local)
4. L'hôte configure les catégories et la durée, puis lance la partie
5. Tous les joueurs remplissent leurs réponses simultanément
6. Après soumission, attendez les autres joueurs dans la **salle d'attente**
7. Les **résultats et le classement** s'affichent à la fin

---

## Technologies utilisées

| Technologie       | Utilisation                              |
|-------------------|------------------------------------------|
| **Java 21**       | Langage principal                        |
| **JavaFX 21**     | Interface graphique (FXML + CSS)         |
| **Maven**         | Gestion des dépendances et build         |
| **Hibernate 7**   | ORM — mapping objet-relationnel          |
| **MySQL 8**       | Base de données relationnelle            |
| **API Groq**      | Validation des mots par IA (LLM)         |
| **Sockets TCP**   | Communication multijoueur en temps réel  |
| **Jakarta Persistence** | Annotations JPA pour les entités   |


##  Licence

Ce projet a été réalisé dans le cadre d'un projet académique.
