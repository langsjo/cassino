26.2-13.3

Tein algoritmin, jolla löytyy kaikki mahdolliset siirrot. Tämän avulla tarkistetaan siirtojen laillisuus. Tein Game ja
Player luokkiin keskeiset metodit, jolla peliä voi pelata (jos game loop olisi myös tehty). Deckin muutin luokaksi (ennen objekti)
jolle voi nyt määrätä pakkojen määrän.

Kysymyksiä:

Haluan antaa Player luokalle tiedon pelistä, missä se on. Haluaisin mielellään myös antaa pelaajat Game luokan luontiparametrina. Voisi toteuttaa antamalla
Player luokalle Option[Game] muuttujan, johon myöhemmin lisättäisiin peli. Voi myös luoda ensin peli, sitten pelaajat, joille annetaan se peli luontiparametrina, ja
sitten lisätä ne pelaajat peliin. Onko jotain parempaakin tapaa tehdä tämä?

Kannattaako yhdistää endRound ja newRound?

13.3-27.3
Tein graafisen käyttöliittymän toimivaan kuntoon, ja sillä voi nyt pelata peliä. Kuitenkin puuttuu vielä botit, tiedostoon
tallennus, menu ja pisteiden näyttäminen sekä pelin voittajan määrittäminen.


siirrä eri skenet omaan objektiin

27.3-???
Lisätty main menu ja pelin asetusmenu (eli pelaajien lisääminen/poistaminen/muokkaaminen). Peli toimii menusta asti peliin hyvin,
pieniä ongelmia scene vaihdon kanssa. Vielä tarvitsee endscreenin ja tallennussysteemin.


Mitä tehdä tasapelissä?
Paras tapa antaa Scenelle parent kun käyttää extends?
Miten saada separaattorit hyvin
scene vaihto bugaa
menussa jutut ei liiku kun muuttaa kokoa (liittyköhän edelliseen?)
game scene ei päivitä ollenkaan, kunnes ensimmäinen ei botti tulee vuoroon (botit pelaa kun GameScene initialisoituu, joten
scene ei ehdi päivittyä stageen)