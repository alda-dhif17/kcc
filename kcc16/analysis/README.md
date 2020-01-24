# KCC 16

## Aufgabe

_Kommissionieren_ ... Produkte in verschiedenen Mengen zu einem Paket zusammenzufassen.

Zu Beginn alle `locations` leer.

Befüllen der Lagerorte und Antworten der Kundenaufträge --> macht API.

### Gegebene Daten

1. alle Produkte ... `productCollection`
2. alle Lagerorte ... `locationCollection`
3. alle Kundenaufträge ... `pickOrderCollection`

**Einstiegspunkt**: `Solution`

### Zu erstellende Daten

Pro Zyklus --> Welches Produkt? Wie viel? Auf welchen Lagerort? => `ReplenishmentOrder`

## Lösungen

### Bob-Lösung

1. Solange unbearbeitete `pickOrder`s vorhanden sind:
    1. Nimm nächste `pickOrder`
    2. Für alle `productLine`s wiederhole:
        1. Lager die, für die `productLine` benötigte Anzahl des gefragten Produkts ein
            1. Sollte die benötigte Anzahl die maximal Lagerzahl an einem Lagerort überschreiten, teile sie auf mehrere auf!
2. Fertig!

### Verbesserung - Ansatz

Einlagern des, in den meisten Bestellungen benötigten Produkts. Jedoch müssen die Lagerorte so befüllt werden, dass mindestens noch eine Bestellung abgearbeitet werden kann (Platz für andere Produkte).