Eestikeelse virtuaalassistendi raamistik
========================================

Kirjeldus
---------
Eestikeelse virtuaalassistendi raamistik on Androidi rakendus, mis võtab sisendiks kasutaja 
häälkäskluse ning selle põhjal tuvastab soovitud tegevuse. Käskluse võib esitada keeleliselt
vabas vormis, kuid see peab sisu poolest viitama ühele järgnevatest tegevustest:
* Meediapleieri käivitamine (näiteks "Mängi muusikat!")
* Meediapleieri peatamine (näiteks "Peata muusika mängimine!")
* Helitugevuse tõstmine (näiteks "Tõsta helitugevust!")
* Helitugevuse vähendamine (näiteks "Vähenda helitugevust!")
* Otsingu teostamine (näiteks "Mis riigis asub Pariis?")
* Meeldetuletuse määramine (näiteks "Tuleta mulle homme meelde, et ma pean hambaarsti juurde minema!")
Tegevuse tuvastamisel ja ka mittetuvastamisel annab rakendus vastavat tagasisidet nii visuaalselt 
kui ka tehiskõnena

**NB!** Tegevust seadmes ei teostata. Kõne põhjal ainult tuvastatakse soovitud tegevus.



Paigaldamine
------------

Rakendus pole saadaval APKna või Play Poes rakendusena. Seega tuleb lähtekoodist rakendus kompileerida.

Rakenduse proovimiseks on vaja täita järgmised sammud:

1. Paigaldada Android Studio. See on allalaetav [siit](https://developer.android.com/studio?gclid=Cj0KCQjwvr6EBhDOARIsAPpqUPF2ceedQLxOMrbKwvUWqbVNfcudXaGmYyGwC1v46Ens_vixnfYo5vIaAriOEALw_wcB&gclsrc=aw.ds)

1. Lae alla see repositoorium ja alammoodul `speechutils` kasutades käsku 
```
git clone --recursive git@github.com:leppsalujyrgen/eestikeelse-haalassistendi-raamistik.git
```

1. Ava projekt `eestikeelse-haalassistendi-raamistik` Android Studio IDEs ja jooksuta Gradle käsku
```
gradle assemble
```

Nüüd on kogu vajalik lähtekood arvutis olemas. Programmi käivitamiseks on vaja **füüsilist Androidi nutiseadet**, sest
emulaatorid pole võimelised mikrofoni salvestama. Rakenduse käivitamiseks arvutis leiad juhised [siit](https://developer.android.com/training/basics/firstapp/running-app).



Lisainfo ja viited 
------------------

**NB!** Rakendus on kirjutatud Kaarel Kaljuranna rakenduse [Kõnele-service](https://github.com/Kaljurand/K6nele-service) põhjal järgides litsentsi 
Apache 2.0 reegleid. Need eeskirjad rakenduvad ka selle töö modifitseerimisele ja reprodutseerimisel.
Täpsem informatsioon failis ,,LICENCE.txt".

Viited:
* [Bakalaureusetöö]() - Lõputöö, mille raames käesolev rakendus loodi. (viide lisatakse töö avalikustamisel)
* [Kõnele service](https://github.com/Kaljurand/K6nele-service) - Eestikeelse kõne transkribeerimise tarkvara Androidile.
* [Dictate.js](https://github.com/Kaljurand/dictate.js) - Eestikeelse kõne transkribeerimise tarkvara JavaScriptile. Sisaldab muuhulgas demorakendusi, mille abil saab proovida kõne transkribeerimist.
* [Kõne transkribeerimise näidisrakendus Androidis](https://github.com/leppsalujyrgen/Kone-transkribeerimine-Android-rakenduses.git) - Lihtsakoeline Androidi rakendus, mis Kõnele service teenust kasutades kõne transkribeerib.
* [Kõne transkribeerimise näidisrakendus JavaScriptile](https://github.com/leppsalujyrgen/Kone-transkribeerimine-JavaScriptis.git) -  Lihtsakoeline JavaScripti rakendus, mis Dictate.js tarkvara kasutades, kõne transkribeerib. Veel lihtsam variant autoriloodud demorakendustest. 


