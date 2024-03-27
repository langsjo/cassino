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