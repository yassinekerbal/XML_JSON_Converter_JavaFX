# XML ↔ JSON Converter (JavaFX)

Ce projet est une application JavaFX permettant de **convertir XML en JSON et JSON en XML**, avec ou sans API.  
L'application offre une interface simple pour charger, afficher et convertir des fichiers ou du texte collé directement.


## Fonctionnalités principales

- Convertir **XML → JSON** et **JSON → XML**  
- Choisir la méthode de conversion :  
  - **Avec API** : utilise `org.json.XML` pour la conversion  
  - **Sans API** : conversion manuelle avec DOM pour XML et JSON  
- Charger un fichier **XML** ou **JSON** depuis l’ordinateur  
- Copier-coller du texte directement dans l’interface  
- Affichage du résultat dans une zone de texte dédiée  


## Interface utilisateur

L'interface est réalisée avec **JavaFX** et **SceneBuilder**.  
Elle comprend :

1. Deux zones de texte : **Input** et **Output**  
2. Boutons pour charger des fichiers : `Load XML` et `Load JSON`  
3. Boutons de conversion : `Convert XML → JSON` et `Convert JSON → XML`  
4. Options de méthode : **Avec API** ou **Sans API**  

> Le design est simple et clair pour une utilisation rapide.


## Comment utiliser l’application

1. Ouvrir l’application avec JavaFX (via `Main.java`)  
2. Coller ou charger un fichier XML/JSON dans la zone **Input**  
3. Choisir la méthode de conversion : **API** ou **Manuelle**  
4. Cliquer sur le bouton correspondant pour obtenir le résultat dans **Output**


## Exemple

### Input XML

```xml
<livre>
    <titre>Programmation</titre>
    <auteur>
        <nom>Kerbal</nom>
        <prenom>yassine</prenom>
    </auteur>
</livre>
````

### Output JSON (Avec API)

```json
{
    "livre": {
        "titre": "Programmation",
        "auteur": {
            "nom": "Kerbal",
            "prenom": "yassine"
        }
    }
}
```


## Vidéo de démonstration

La vidéo présente l'utilisation de l'application en **5 minutes** :

* Présentation de l’interface
* Démonstration de la conversion XML → JSON et JSON → XML
* Choix des méthodes avec/sans API
* Résultat affiché en temps réel

 Regardez la vidéo de démonstration du projet :
 
 *v1: https://docs.google.com/videos/d/1bRD28tmlmuue3dSYPIzUPEiEgLjVC0RmboApfuiPFdM/edit?usp=sharing
 
 *v2: https://docs.google.com/videos/d/1GVZ-5ZZ52WZoDM4lB19p0XYZnnGjteMT0tMnmqjyM3I/edit?usp=sharing

## Prérequis

* Java 11 ou supérieur
* JavaFX (inclus ou séparé selon votre setup)
* Bibliothèque `org.json` pour la conversion API

---

## Auteur

* Yassine kerbal
* Projet réalisé en JavaFX avec DOM, JSON et SceneBuilder
