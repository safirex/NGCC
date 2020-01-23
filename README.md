# NGCC - Nicely Generated and Corrected Copies
**Projet Tutoré S3TG4**

## Sommaire

1. [Présentation du projet](https://git-iutinfo.unice.fr/rey/pt-s3t-g4#pr%C3%A9sentation-du-projet)
2. [Comment l'utiliser](https://git-iutinfo.unice.fr/rey/pt-s3t-g4#comment-lutiliser)
3. [Auteurs](https://git-iutinfo.unice.fr/rey/pt-s3t-g4#auteurs)
4. [License](https://git-iutinfo.unice.fr/rey/pt-s3t-g4#license)

## Présentation du projet

NGCC est un projet réalisé en java lors de notre 3ème semestre de notre formation (DUT informatique traditionel).
L'objectif du projet est de concevoir un logiciel en ligne de commande permettant d'aider les enseignants dans la saisie des notes et la correction des copies à l'aide de techniques de reconnaissance optique de cases noircies et de reconnaissance de caractères.


## Comment l'utiliser

Vous trouverez la documentation pour configurer votre QCM ici: [Documentation](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD)

### Liste des commandes étape 1

Création d’une copie standard (build) : Fournit une copie standard du type des copies d’examens utilisable
à l’étape 1. Le résultat sera un fichier pdf contenant cette copie. D’autres éléments du projet peuvent être
générés.

```nggc.jar -b [-u[n]] [-v[n]] [-a answer] [source]```

Lecture des notes de n copies (read) : analyse l’ensemble des copies numérisées fournies pour y
reconnaitre le numéro d’étudiant et la note. Les copies seront constituées de l’ensemble des fichiers *.pdf
du répertoire.

```nggc.jar -r [-u[n]] [-v[n]] [-d directory] [-o result] [source]```

### Liste des commandes étape 2

Génération d’un sujet et de la copie de réponse associée (generate) : génère le sujet et la copie de
réponse associés au document source.

```nggc.jar -g [-u[n]] [-v[n]] [-t topic] [-a answer] [source]```

Génération d’un sujet corrigé (produce) : produit le corrigé du sujet associé au document source.

```nggc.jar -p [-u[n]] [-v[n]] [-c sheet] [source]```

### Liste des commandes étape 3

Analyse des n copies (analyse) : analyse l’ensemble des copies numérisées fournies pour y reconnaitre le
numéro d’étudiant et l’ensemble des réponses aux questions. Les copies seront constituées de l’ensemble
des fichiers *.pdf du répertoire.

```nggc.jar -a [-u[n]] [-v[n]] [-d directory] [source]```

Evaluation des copies (evaluate) : évalue les notes de chaque copie en fonction de l’analyse effectuée
précédemment. Cette commande peut être exécutée plusieurs fois successivement pour prendre en
compte d’éventuels changement dans les formules de notation.

```nggc.jar -e [-u[n]] [-v[n]] [-o result] [source]```

### Liste des commandes étape 4

Génération de n sujets et de leur copie de réponse associée (generate) : génère le sujet et la copie de
réponse associés au document source.

```nggc.jar -g [-u[n]] [-v[n]] [-n number] [-t topic] [-a answer] [source]```

Détails des champs

- ```[source]``` : permet de spécifier le chemin vers le fichier source. Par défaut, le fichier local ./source.txt sera utilisé.
- ```[-u[n]]``` : permet de spécifier que la commande met à jour (update) le projet sans refaire le travail déjà fait. L’indice n est un entier compris entre 0 et 9. Il s’agit d’un paramètre optionnel. Il est là pour indiquer la sous étape à partir de laquelle la mise à doit avoir lieu. Toutes les valeurs ne sont pas bligatoirement définies.
- ```[-v[n]]``` : permet d’activer le mode verbeux/bavard/prolixe (verbose). Dans ce mode, l’ensemble des informations de traitement sera affiché à l’écran en plus d’être enregistré dans les fichiers de log. L’indice n est un entier compris entre 0 et 9. Il s’agit d’un paramètre optionnel. Il est là pour indiquer un niveau bavardage. Le mode v0 sera un mode silencieux, sans aucun message. Le mode v1 (mode par défaut, donc identique à l’absence du paramètre) n’indique que les erreurs, le début et la fin de traitement. Les modes v2 à v9 donnant plus de détails à mesure que l’indice augmente. Toutes les valeurs ne sont pas obligatoirement définies. Le mode v5 sera équivalent au mode v sans indice.
- ```[-a anwser]``` : permet de spécifier le nom du fichier pdf de sortie. Par défaut, le fichier answer-sheet.pdf sera utilisé.
- ```[-d directory]```: permet de spécifier le nom du répertoire contenant les copies numérisées (fichiers pdf). Par défaut, le répertoire copies sera utilisé.
- ```[-o result]``` : permet de spécifier le nom du fichier csv de sortie. Par défaut, le fichier result.csv sera utilisé.
- ```[-t topic]``` : permet de spécifier le nom du fichier pdf de sortie. Par défaut, le fichiers topic- sheet.pdf sera utilisé.
- ```[-c sheet]``` : permet de spécifier le nom du fichier pdf de sortie. Par défaut, le fichiers corrected-sheet.pdf sera utilisé.
- ```[-n number]``` : permet de spécifier le nombre (entier strictement positif) de sujets/copies que l’on souhaite générer. Par défaut la valeur 1 sera utilisée.

## Auteurs
+ [Anthony Malvesin](https://git-iutinfo.unice.fr/ma803170)
+ [Antoine Héraud](https://git-iutinfo.unice.fr/ha801565)
+ [Arnaud Lysensoone-Bijou](https://git-iutinfo.unice.fr/ba807470)
+ [Bastien Noel](https://git-iutinfo.unice.fr/nb802168)
+ [Eudes Chatin](https://git-iutinfo.unice.fr/ce806665)
+ [Florian Audouard](https://git-iutinfo.unice.fr/af809837)
+ [Geoffrey Koson-Bourreau](https://git-iutinfo.unice.fr/kg403211)
+ [Hugo Nortier](https://git-iutinfo.unice.fr/nh805942)
+ [Hugo Poissonier](https://git-iutinfo.unice.fr/ph807242)
+ [José Srifi](https://git-iutinfo.unice.fr/sj801446)
+ [Jules Vesnat](https://git-iutinfo.unice.fr/vj703676)
+ [Louis Calas](https://git-iutinfo.unice.fr/cl705239)
+ [Nawfel Hilal](https://git-iutinfo.unice.fr/hn805128)
+ [Nicolas Lacroix](https://git-iutinfo.unice.fr/ln803631)
+ [Paul Gross](https://git-iutinfo.unice.fr/gp805862)
+ [Saad Ahmed](https://git-iutinfo.unice.fr/as704245)
+ [Yessine Ben El Bey](https://git-iutinfo.unice.fr/by801687)

## License

Ce projet est disponible sous license [Apache License v2.0](http://www.apache.org/licenses/LICENSE-2.0) (voir [LICENSE.MD](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/raw/master/LICENSE))

