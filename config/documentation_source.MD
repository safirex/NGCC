# Documentation_source

## Sommaire

  * [PaperSize](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#papersize)
  * [Title](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#title)
  * [Presentation](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#presentation)
  * [DocumentModel](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#documentmodel)
  * [Date](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#date)
  * [AnswerSheetDate](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#answersheetdate)
  * [ShuffleQuestions](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#shufflequestions)
  * [ShuffleAnswers](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#shuffleanswers)
  * [Code](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#code)
  * [MarkFormat](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#markformat)
  * [NameField](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#namefield)
  * [StudentIdField](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#studentidfield)
  * [MarkField](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#markfield)
  * [SeparateAnswerSheet](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#separateanswersheet)
  * [AnswerSheetTitle](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#answersheettitle)
  * [AnswerSheetPresentation](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#answersheetpresentation)
  * [SingleSided](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#singlesided)
  * [QuestionBlocks](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#questionblocks)
  * [Columns](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#columns)
  * [ManualDuplex](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#manualduplex)
  * [AnswerSheetManualDuplex](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#answersheetmanualduplex)
  * [DefaultScoringS](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#defaultscorings)
  * [DefaultScoringM](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#defaultscoringm)
  * [NumberOfCopiesToCreate](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#numberofcopiestocreate)

  * [Format_des_questions](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#format-des-questions)
  * [Format_des_réponses](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#format-des-r%C3%A9ponses)  
  
    
Cette documentation a pour but de détailler l'utilisation du fichier de configuration du QCM.  
Vous devrez, pour le configurer, compléter les champs après les ":" sauf si une valeur par défaut vous convient.  

Voici les différents options à paramétrer:  


### PaperSize  
Permet de configurer le format de papier utilisé pour la copie.  
Vous avez le choix entre A3, A4, A5 et letter.   
Par défaut la valeur sera A4.  

### Title
Correspond au titre de l'examen.  

### Presentation
Correspond au texte introductif de l'examen notamment pour présenter les consignes.   

### DocumentModel
Indique le modèle de document utilisé pour la génération du sujet.   
Il est proposé que cet élément soit le nom du fichier du modèle.  

### Date
Indique la date de l'examen (dd/mm/yyyy), Par défaut, la date du jour de la création est utilisée.  

### AnswerSheetDate
Indique la date de l'examen sur la copie de réponses. Par défaut, la valeur de "Date" est utilisée.  

### ShuffleQuestions
Permet de configurer si les questions seront dans le même ordre ou non dans chacune des copies.  
Pour qu'elles ne soient pas mélangées il faut mettre "0" sinon, la valeur par défaut: "1" fait qu'elles seront mélangées.  

### ShuffleAnswers
Permet de configurer si les réponses aux questions seront mélangées ou non dans chacune des copies.  
Pour qu'elles ne soient pas mélangées il faut mettre "0" sinon, la valeur par défaut: "1" fait qu'elles seront mélangées.  

### Code
Permet de configurer le nombre de chiffres que comportent les numéros d'étudiants.  
Ce nombre doit être compris entre 1 et 16.  
Ainsi, si le numéro d'étudiant est: "21805942" il faudra inscrire "8" après le champ "code: ".  

### MarkFormat
Correspond au format de la note.  
Des exemples de MarkFormat seraient "20/4" pour des notes entre 0 et 20 notées à 0.25 près
et "100" pour des notes entières entre 0 et 100.  

### NameField
Cette option sert à remplacer le texte « Nom et prénom » inscrit dans le cadre où l’étudiant doit inscrire son identité.   

### StudentIdField
Cette option sert à remplacer le petit texte qui demande de coder son numéro d’étudiant  
et d’inscrire son nom sur les copies (quand l’option Code a été utilisée).  

### MarkField
Cette option sert à remplacer le petit texte qui demande de coder la note de l’étudiant   

### SeparateAnswerSheet
Cette option permet d'obtenir une feuille de réponses séparée.  
La valeur par défaut est 1, sinon attendu 0.  
Si la valeur est définie à 0, alors les questions et les réponses seront fusionnées.

### AnswerSheetTitle
Cela permet de configurer le titre à inscrire en tête de la feuille de réponse.  
Par défaut, le titre sera celui de « Title ».  

### AnswerSheetPresentation
Donne le texte de présentation de la feuille de réponse.   
Par défaut, le texte utilisé pour présenter sera celui de « Presentation ».  

### SingleSided
Permet de configurer si une page blanche sera ajoutée entre le sujet et la feuille de réponses.  
En mettant "1", il n'y aura pas de page blanche.  

### QuestionBlocks
Permet de configurer si les questions peuvent être coupées sur plusieurs pages ou plusieurs colonnes.  
Avec la valeur 1, les questions ne pourront pas être coupées tandis qu'elles peuvent l'être avec la valeur 0.  

### Columns
Correspond au nombre de colonne(s) en parallèle sur lesquelles sont écrites les questions par feuille de questionnaire.  
Si vous voulez plusieurs colonnes parallèles de questions par pages, vous le pouvez jusqu'à 4 maximum.  
Par défaut la valeur est 1.  
 
### ManualDuplex 
Permet de faire en sorte que les sujets aient un nombre pair de page.    
Utile si l'on veut imprimer en mode recto verso tous les sujets sans avoir le début d'une copie sur le verso de la copie précédente.  
Pour activer cette option la valeur est 1. Par défaut, elle est de 0.  

### AnswerSheetManualDuplex
Permet de faire en sorte que les feuilles de réponses aient un nombre pair de page.  
Utile si l'on veut imprimer en mode recto verso toutes les copies de réponse sans avoir le début d'une copie sur le verso de la copie précédente.  
Pour activer cette option la valeur est 1. Par défaut, la valeur est la même que celle de ManualDuplex.   

### DefaultScoringS
Permet de configurer le barème pour les questions à unique réponse correct.  
Il faudra compléter par un nombre après les "=". Ce nombre doit être compris entre \[-1;1], et utiliser un mutiple de 0.25 points. Pour augmenter le barème il faudra voir avec l'option 'coeff' dans les questions ([Format_des_questions](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#format-des-questions)).   
Le barème se décompose avec les paramètres suivants:  

"e=" correspond à la note en cas de plusieurs cases cochées (alors que l'on attend qu'une réponse).  
"v=" correspond à la note en cas d'aucune case cochée.  
"d=" correspond à une valeur ajoutée à toutes les bonnes notes qui ne relèvent pas des cas correspondant aux paramètres e et v.  
"p=" correspond à la note plancher c'est à dire que si la note obtenue à la question donne une valeur inférieure à la valeur plancher, cette valeur plancher prédomine.  
"b=" correspond à la note pour une bonne réponse.  
"m=" correspond à la note pour une mauvaise réponse.  
"formula="  donne le score de chaque question.  

Cette formule peut comporter les différents éléments suivants:    
'N' qui est le nombre de réponses proposées dans la question.  
'NB' qui est le nombre de bonnes réponses à la question.  
'NM' est le nombre de mauvaises réponses à la question.  
'NBC' qui est le nombre de bonnes réponses qui ont été cochées.  
'NMC' qui est le nombre de mauvaises réponses qui ont été cochées.  
Elle peut utiliser les opérateurs +,-,*,/ et les paranthèses sont prises en comptes.  

La valeur par défault de defaultScoringS est: e=0,v=0,b=1,m=-1,p=0,d=0.  

### DefaultScoringM
Permet de configurer le barème pour les questions à multiples réponses correctes.  
Il faudra compléter par un nombre après les "=". Ce nombre doit être compris entre \[-1;1], et utiliser un mutiple de 0.25 points. Pour augmenter le barème il faudra voir avec l'option 'coeff' dans les questions ([Format_des_questions](https://git-iutinfo.unice.fr/rey/pt-s3t-g4/blob/master/config/documentation_source.MD#format-des-questions)).   
Le barème se décompose avec les paramètres suivants:  

"e=" correspond à la note en cas de case "aucune de ces réponses n'est correcte" cochée en même temps qu'ue autre case.  
"v=" correspond à la note en cas d'aucune case cochée.  
"d=" correspond à une valeur ajoutée à toutes les bonnes notes qui ne relèvent pas des cas correspondant aux paramètres e et v.  
"p=" correspond à la note plancher c'est à dire que si la note obtenue à la question donne une valeur inférieure à la valeur plancher, cette valeur plancher prédomine.  
"b=" correspond à la note pour une bonne réponse.  
"m=" correspond à la note pour une mauvaise réponse.  
"mz=VALEUR1,VALEUR2" correspond à un barème où l’élève doit cocher toutes les bonnes réponses pour avoir la note VALEUR1 ou sinon il aura VALEUR2.  
VALEUR2 doit être inférieure à VALEUR1, ainsi, VALEUR2 ne peut pas être égal à "1.0" .  
"haut=VALEUR1,VALEUR2" Si l'élève a tout juste il aura la note VALEUR1 sinon ON ENLÈVERA à la note maximale (VALEUR1) autant de fois la VALEUR2 qu'il aura d'erreur.  
Attention, puisque l'on enlève, il faut que VALEUR2 soir positive.  
"formula=FORMULE" Donne directement le score de la question

Naturellement, si "mz" ou "haut" sont utilisés, "b" et "m" ne seront pas utilisés pour le barème.  

Cette formule peut comporter les différents éléments suivants:    
'N' qui est le nombre de réponses proposées dans la question.  
'NB' qui est le nombre de bonnes réponses à la question.  
'NM' est le nombre de mauvaises réponses à la question.  
'NBC' qui est le nombre de bonnes réponses qui ont été cochées.  
'NMC' qui est le nombre de mauvaises réponses qui ont été cochées.  
Elle peut utiliser les opérateurs +,-,*,/ et les paranthèses sont prises en comptes.  
 
La valeur par défaut est: e=0,v=0,p=-1,formula=(NBC/NB)-(NMC/NM)  

### NUMBEROFCOPIESTOCREATE
Permet de configurer le nombre de copies à créer, par défaut il y en a juste "1".
Un entier est attendu.  

----------------------------------------------------------------------------------------------------------

### FORMAT DES QUESTIONS  

Commencer une question par "*" signifie qu'elle n'a qu'une unique réponse juste.  
Commencer une question par "**" signifie qu'elle a plusieurs réponses justes.  

Rajouter "{lines=n}" après les étoiles signifie que la question est ouverte.  
(avec n un entier ; "n" correspond au nombre de ligne(s) où l'on pourra écrire la réponse.)  

Rajouter "{frozen}" après les étoiles signifie que la question est banalisée.  

Rajouter "{coeff=n}" après les étoiles signifie que la question a un coéfficient particulier.   

par exemple, on pourra avoir:  
*{lines=5,coeff=3}
*{lines=1,frozen}  

pour créer des groupes de questions parmi lesquelles on en choisira seulement une partie aléatoirement vous devrez ajouter:  

((n=\[un entier tel que 0 < n < nombre de questions dans le groupe]  
Les différentes questions  

))  
(notez qu'entre la dernière question et la fin du groupe caractérisée par "))" il faut un retour chariot + un saut de ligne.)  
Par défaut, ce groupe aura ses questions mélangées d'un sujet à l'autre.  
Pour que les questions soient fixes, il faut ajouter "frozen" après le nombre de questions choisies.    
 
Ainsi, un exemple de groupe serait:  
   
((n=1 frozen  
   
\*Ma question  
\+une réponse vraie  
\-une réponse fausse  
  
\*Ma question2  
\+une réponse  
\-une autre  
   
))  

### FORMAT DES RÉPONSES  

Commencer une réponse par "+" signifie que la réponse est juste.  
Commencer une réponse par "-" signifie que la réponse est fausse.  
Les réponses aux questions ouvertes sont en fait la note que peux avoir l'élève. Elle est comprise entre \[-1;1]
par défaut, la valeur est:    
\- N  
\+ P    
\+ TQP   
\+ DP   
\+ DDP   
\+ Z  
avec N= "-" , P= 1point, TQP= 0.75point, DP= 0.5point, DDP= 0.25point, Z= 0point.  

Sur la copie:  
Une seule case doit être cochée à la fois (exception faites de "N" qui peut être cochée en plus d'une autre case afin de mettre une note négative à la question ouverte).   
