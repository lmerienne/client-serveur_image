# client-serveur

## Besoin 21 :
***

### Systèmes d'exploitation sur lesquels le serveur a été testé :
* MacOS 11
* Ubuntu 20
* Debian Buster

### Navigateurs web sur lesquels le client a été testé :
* Chrome
* Firefox
* Safari
***
## Installation :
***

### Attention à utiliser la bonne version de Node.js
Si il y a des problèmes à la compilation du projet utiliser la commande :
```
export PATH=/opt/users/Node.js/bin:$PATH
``` 

### Lancement du projet : 
```
$ mvn clean install
$ mvn --projects backend spring-boot:run
```

/!\ La première commande doit être relancée pour chaque modifications sur le Frontend. 

***
## Quelques soucis :
***
Lors de la compilation du proet (première commande), des erreurs ont lieu. Elles sont dues aux tests du noyaux
qui ne passent pas. Malgré ces erreurs, la compilation se fait bien.

Notre implémentation des filtres n'est pas parfaite (pas de correction depuis le rendu du devoir), il se peut qu'il
y ai quelques problèmes lors de l'application de certains.

***
## Application des filtres (URL) :
***
 Exemple d'appel pour le filtre de changement  de luminosité:
 ```
http://localhost:8080/images/0?algorithm=changeLum&p1=1
 ```
 Pour les appels des filtres avec plusieurs paramètres utiliser p1=X et p2=Y (si besoin de deux paramètres).
 ***