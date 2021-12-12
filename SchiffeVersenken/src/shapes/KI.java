/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shapes;

import java.util.Random;

/**
 *
 * @author esmay
 */
public class KI {
    private int spielfeldgroesse;  
    private int[] anzahlSchiffeTyp;
    private boolean fertig = false;
    private Schiff[] schiffArray;
    private int anzSchiffe = 0;
    private Grid gridSpielfeld;
    private int[][] getroffen;

    public KI(int spielfeldgroesse, int[] anzahlSchiffeTyp) {
        this.spielfeldgroesse = spielfeldgroesse;
        this.anzahlSchiffeTyp = anzahlSchiffeTyp;
        for (int i = 0; i < anzahlSchiffeTyp.length; i++) {
            this.anzSchiffe += anzahlSchiffeTyp[i];
        }
        gridSpielfeld = new Grid(spielfeldgroesse);
        gridSpielfeld.macheGrid(); 
        getroffen = new int[spielfeldgroesse][spielfeldgroesse];
    }

    public Grid getGridSpielfeld() {
        return gridSpielfeld;
    }

    public void erzeugeEigeneSchiffe() {
        int wiederholungen = 0;
        boolean allegesetzt = false;
        int anzahlgesetzt = 0;
        Random zufall = new Random();
        int zufallx;
        int zufally;
        int zufallsrichtung; // horizontal--- -> 0, vertikal||| -> 1
        
        schiffArray = new Schiff[anzSchiffe];
        int ctn = 0;
        for (int i = 0; i < anzahlSchiffeTyp[0]; i++) {
            schiffArray[ctn++] = new Schiff(2 * gridSpielfeld.getKachelgroeße(), gridSpielfeld.getKachelgroeße());
        }
        for (int i = 0; i < anzahlSchiffeTyp[1]; i++) {
            schiffArray[ctn++] = new Schiff(3 * gridSpielfeld.getKachelgroeße(), gridSpielfeld.getKachelgroeße());
        }
        for (int i = 0; i < anzahlSchiffeTyp[2]; i++) {
            schiffArray[ctn++] = new Schiff(4 * gridSpielfeld.getKachelgroeße(), gridSpielfeld.getKachelgroeße());
        }
        for (int i = 0; i < anzahlSchiffeTyp[3]; i++) {
            schiffArray[ctn++] = new Schiff(5 * gridSpielfeld.getKachelgroeße(), gridSpielfeld.getKachelgroeße());
        }
        
        while(!allegesetzt){
            for(int i = schiffArray.length-1; i >= 0; i--){
                if(wiederholungen > 30 && schiffArray[i+1].getLaenge() != 2){
                    //System.out.println("Break");
                    break;
                }
                else if(wiederholungen > 60 && schiffArray[i+1].getLaenge() == 2){
                    //System.out.println("Break");
                    break;
                }
                wiederholungen = 0;
                while(anzahlgesetzt < schiffArray.length-i){ // anzahlgesetzt = 0 
                    do{
                        zufallx = zufall.nextInt(spielfeldgroesse); 
                        zufally = zufall.nextInt(spielfeldgroesse);
                        schiffArray[i].setStart(zufallx, zufally);
                        zufallsrichtung = zufall.nextInt(2);
                        if(zufallsrichtung == 0){
                            schiffArray[i].setRichtung(Richtung.HORIZONTAL);
                        }
                        else if(zufallsrichtung == 1){
                            schiffArray[i].setRichtung(Richtung.VERTIKAL);
                        }
                    }
                    while(!setIdNeu(schiffArray[i], i)); // !gridSpielfeld.getGrid()[zufally][zufallx].getId().equals(1));

                    //schiffArray[i].print();
                    //System.out.println("Schiff Nr " + i + " x: " + zufallx + " y: " + zufally + " richtung: " + zufallsrichtung + " leange: " + schiffArray[i].getLaenge());
                    
                    if(zufallsrichtung == 0){ // Horizontal ---
                        if(ueberpruefePlatzHorizontal(schiffArray[i])){
                            anzahlgesetzt++;
                            //System.out.println("Gesetzt");
                            //gridSpielfeld.print(); // DEBUG
                        }
                        else{
                            //System.out.println("Nicht gesetzt");
                            clearId(schiffArray[i]);
                        }
                    }
                    else if(zufallsrichtung == 1){ // Vertikal |||
                        if(ueberpruefePlatzVertikal(schiffArray[i])){
                            anzahlgesetzt++;
                            //System.out.println("Gesetzt");
                            //gridSpielfeld.print(); // DEBUG
                        }
                        else{
                            //System.out.println("Nicht gesetzt");
                            clearId(schiffArray[i]);
                        }
                    }
                    wiederholungen++;
                    if(wiederholungen > 30 && schiffArray[i].getLaenge() != 2){
                        //System.out.println("Break");
                        break;
                    }
                    else if(wiederholungen > 60 && schiffArray[i].getLaenge() == 2){
                        //System.out.println("Break");
                        break;
                    }
                }
                //System.out.println("Anzahl gebraucht " + wiederholungen);
            }
            if(anzahlgesetzt == schiffArray.length){
                allegesetzt = true;
                //System.out.println("Alle gesetzet");
                //gridSpielfeld.print(); // DEBUG
                fertig = true;
            }
            else{
                //System.out.println("Zurücksetzen");
                wiederholungen = 0;
                clearAll();
                anzahlgesetzt = 0;
                //gridSpielfeld.print(); // DEBUG
            }
        }
    }
    
    public int[] shot(){
        int[] schuss = {0,0}; // [Zeile, Spalte]
        Random zufall = new Random();
        int zufally = zufall.nextInt(spielfeldgroesse); 
        int zufallx = zufall.nextInt(spielfeldgroesse);
        
        while(getroffen[zufally][zufallx] != 0){ // 0 Noch nix , 1 Wasser, 2 Treffer
            zufally = zufall.nextInt(spielfeldgroesse); 
            zufallx = zufall.nextInt(spielfeldgroesse); 
        }
        
        return schuss;
    }
    
    public Schiff[] getSchiffArray() {
        return schiffArray;
    }

    public boolean isFertig() {
        return fertig;
    }
    
    public void clearId(Schiff s) {
        if (s.getRichtung() == Richtung.HORIZONTAL) {
            for (int i = s.getStartX(); i < s.getStartX() + s.getLaenge(); i++) {
                gridSpielfeld.getGrid()[i][s.getStartY()].setId("0");
                //System.out.println("Setze 0: " + i + " / " + s.getStartY());
            }
        } else {
            for (int i = s.getStartY(); i < s.getStartY() + s.getLaenge(); i++) {
                gridSpielfeld.getGrid()[s.getStartX()][i].setId("0");
                //System.out.println("Setze 0: " + s.getStartX() + " / " + i);
            }
        }
    }
    
    public void clearAll(){
        for(int i = 0; i < spielfeldgroesse; i++){
            for(int j = 0; j < spielfeldgroesse; j++){
                gridSpielfeld.getGrid()[i][j].setId("0");
            }
        }
    }
    
    public boolean setIdNeu(Schiff s , int index) {
        int counter = 0;
        if (s.getRichtung() == Richtung.HORIZONTAL) {
            for (int i = s.getStartX(); i < s.getStartX() + s.getLaenge(); i++) {
                if(i < gridSpielfeld.getKachelAnzahl()){
                    if(!gridSpielfeld.getGrid()[i][s.getStartY()].getId().equals("0")){
                        return false;
                    }
                }
                else{
                    return false;
                }
            }
            for (int i = s.getStartX(); i < s.getStartX() + s.getLaenge(); i++) {
                gridSpielfeld.getGrid()[i][s.getStartY()].setId(""+((index+1)*10+ counter));
                counter++;
                //System.out.println("Setze 1: " + i + " / " + s.getStartY());
            }
        } else {
            for (int i = s.getStartY(); i < s.getStartY() + s.getLaenge(); i++) {    
                if(i < gridSpielfeld.getKachelAnzahl()){
                    if(!gridSpielfeld.getGrid()[s.getStartX()][i].getId().equals("0")){
                        return false;
                    }
                }
                else{
                    return false;
                }
                
            }
            for (int i = s.getStartY(); i < s.getStartY() + s.getLaenge(); i++) {
                gridSpielfeld.getGrid()[s.getStartX()][i].setId(""+((index+1)*10+ counter));
                counter++;
                //System.out.println("Setze 1: " + s.getStartX() + " / " + i);
            }
        }
        return true;
        //gridSpielfeld.print(); // DEBUG
    }
    
     private boolean ueberpruefePlatzHorizontal(Schiff s) {
        int x = s.getStartX();
        int y = s.getStartY();
        boolean status = true;
        //1.Über/Unter dem Schiff, wenn dort kein Rand ist:
        if (y - 1 >= 0 && y + 1 <= gridSpielfeld.getKachelAnzahl() - 1 && x + s.getLaenge() <= gridSpielfeld.getKachelAnzahl()) { //Überprüfe auf Rand
            for (int i = x; i < x + s.getLaenge(); i++) {
                if (!gridSpielfeld.getGrid()[i][y - 1].getId().equals("0")) { //Suche über dem Schiff
                    status = false; //Markierung gefunden
                    break; //Breche ab
                }
                if (!gridSpielfeld.getGrid()[i][y + 1].getId().equals("0")) {//Suche unter dem Schiff
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
            }
        }
        //2.Links/Rechts neben dem Schiff
        if (x - 1 >= 0 && x + 1 <= gridSpielfeld.getKachelAnzahl() - s.getLaenge() && y - 1 >= 0 && y + 1 <= gridSpielfeld.getKachelAnzahl() - 1) { //Randüberprüfung
            for (int i = y - 1; i < y - 1 + 3; i++) {
                if (!gridSpielfeld.getGrid()[x - 1][i].getId().equals("0")) { //Links vom Schiff
                    status = false; //Markierung gefunden
                    break; //Breche ab
                }
                if (!gridSpielfeld.getGrid()[x + s.getLaenge()][i].getId().equals("0")) {//Rechts vom Schiff
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
            }
        }
        //3. Randfälle
        if (y - 1 <= 0 && x - 1 >= 0 && x + s.getLaenge() <= gridSpielfeld.getKachelAnzahl() - 1) { //Am Oberen Rand Zeichnen
            for (int i = x; i < x + s.getLaenge(); i++) {
                if (!gridSpielfeld.getGrid()[i][y + 1].getId().equals("0")) {//Suche unter dem Schiff
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
            }
            for (int i = y; i < y + 2; i++) {
                if (!gridSpielfeld.getGrid()[x - 1][i].getId().equals("0")) { //Links vom Schiff
                    status = false; //Markierung gefunden
                    break; //Breche ab
                }
                if (!gridSpielfeld.getGrid()[x + s.getLaenge()][i].getId().equals("0")) {//Rechts vom Schiff
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
            }
        }
        if (x - 1 <= 0 && y - 1 >= 0 && y + 1 <= gridSpielfeld.getKachelAnzahl() - 1) { //Am linken Rand zeichen
            for (int i = y - 1; i < y - 1 + 3; i++) {
                if (!gridSpielfeld.getGrid()[x + s.getLaenge()][i].getId().equals("0")) {//Rechts vom Schiff
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
            }
        }
        if (x + 1 == gridSpielfeld.getKachelAnzahl() - s.getLaenge() + 1 && y - 1 >= 0 && y + 1 <= gridSpielfeld.getKachelAnzahl() - 1) { //Wenn rechts raus zeiche zusätlich noch links 
            for (int i = y - 1; i < y - 1 + 3; i++) {
                if (!gridSpielfeld.getGrid()[x - 1][i].getId().equals("0")) { //Links vom Schiff
                    status = false; //Markierung gefunden
                    break; //Breche ab
                }
            }
        }
        if (y + 1 >= gridSpielfeld.getKachelAnzahl() - 1 && x - 1 >= 0 && x + s.getLaenge() <= gridSpielfeld.getKachelAnzahl() - 1) { //Am unteren Rand 
            for (int i = x; i < x + s.getLaenge(); i++) {
                if (!gridSpielfeld.getGrid()[i][y - 1].getId().equals("0")) { //Suche über dem Schiff
                    status = false; //Markierung gefunden
                    break; //Breche ab
                }
            }
            for (int i = y - 1; i < y - 1 + 2; i++) {
                if (!gridSpielfeld.getGrid()[x - 1][i].getId().equals("0")) { //Links vom Schiff
                    status = false; //Markierung gefunden
                    break; //Breche ab
                }
                if (!gridSpielfeld.getGrid()[x + s.getLaenge()][i].getId().equals("0")) {//Rechts vom Schiff
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
            }
        }
        //Linke obere Ecke
        if (x - 1 <= 0 && y - 1 <= 0) {
            for (int i = y; i < y + 2; i++) {
                if (!gridSpielfeld.getGrid()[x + s.getLaenge()][i].getId().equals("0")) {//Rechts vom Schiff
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
            }
            for (int i = x; i < x + s.getLaenge(); i++) {
                if (!gridSpielfeld.getGrid()[i][y + 1].getId().equals("0")) {//Suche unter dem Schiff
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
            }
        }
        //Rechte obere Ecke
        if (x + 1 == gridSpielfeld.getKachelAnzahl() - s.getLaenge() + 1 && y - 1 <= 0) {
            for (int i = y; i < y + 2; i++) {
                if (!gridSpielfeld.getGrid()[x - 1][i].getId().equals("0")) { //Links vom Schiff
                    status = false; //Markierung gefunden
                    break; //Breche ab
                }
            }
            for (int i = x; i < x + s.getLaenge(); i++) {
                if (!gridSpielfeld.getGrid()[i][y + 1].getId().equals("0")) {//Suche unter dem Schiff
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
            }
        }
        //Rechte untere Ecke
        if (x + 1 == gridSpielfeld.getKachelAnzahl() - s.getLaenge() + 1 && y + 1 >= gridSpielfeld.getKachelAnzahl() - 1) {
            for (int i = y; i > y - 2; i--) {
                if (!gridSpielfeld.getGrid()[x - 1][i].getId().equals("0")) { //Links vom Schiff
                    status = false; //Markierung gefunden
                    break; //Breche ab
                }
            }
            for (int i = x; i < x + s.getLaenge(); i++) {
                if (!gridSpielfeld.getGrid()[i][y - 1].getId().equals("0")) {//Suche unter dem Schiff
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
            }
        }
        //Linke untere Ecke
        if (x - 1 <= 0 && y + 1 >= gridSpielfeld.getKachelAnzahl() - 1) {
            for (int i = y; i > y - 2; i--) {
                if (!gridSpielfeld.getGrid()[x + s.getLaenge()][i].getId().equals("0")) {//Rechts vom Schiff
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
            }
            for (int i = x; i < x + s.getLaenge(); i++) {
                if (!gridSpielfeld.getGrid()[i][y - 1].getId().equals("0")) {//Suche unter dem Schiff
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
            }
        }
        return status;
    }
    
     private boolean ueberpruefePlatzVertikal(Schiff s) {
        int x = s.getStartX();
        int y = s.getStartY();
        boolean status = true;
        //1.Über/Unter dem Schiff, wenn dort kein Rand ist:
        if (y - 1 >= 0 && y + s.getLaenge() <= gridSpielfeld.getKachelAnzahl() - 1 && x + 1 <= gridSpielfeld.getKachelAnzahl() - 1 && x - 1 >= 0) { //Überprüfe auf Rand
            for (int i = x - 1; i < x + 2; i++) {
                if (!gridSpielfeld.getGrid()[i][y - 1].getId().equals("0")) { //Suche über dem Schiff
                    status = false; //Markierung gefunden
                    break; //Breche ab
                }
                if (!gridSpielfeld.getGrid()[i][y + s.getLaenge()].getId().equals("0")) {//Suche unter dem Schiff
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
            }
        }
        //2.Links/Rechts neben dem Schiff
        if (x - 1 >= 0 && x + 1 <= gridSpielfeld.getKachelAnzahl() - 1 && y - 1 >= 0 && y + s.getLaenge() <= gridSpielfeld.getKachelAnzahl() - 1) { //Randüberprüfung
            for (int i = y; i < y + s.getLaenge(); i++) {
                if (!gridSpielfeld.getGrid()[x - 1][i].getId().equals("0")) { //Links vom Schiff
                    status = false; //Markierung gefunden
                    break; //Breche ab
                }
                if (!gridSpielfeld.getGrid()[x + 1][i].getId().equals("0")) {//Rechts vom Schiff
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
            }
        }

        //Sonderfälle:       
        //1. Links
        if (x - 1 <= 0) { //Wenn links raus
            if (y + s.getLaenge() >= gridSpielfeld.getKachelAnzahl()) { //und Unten raus
                System.out.println("Bin Hier");
                for (int i = x; i < x + 2; i++) {
                    if (!gridSpielfeld.getGrid()[i][y - 1].getId().equals("0")) {
                        status = false; //Markierung gefunden 
                        break; //Brche suche ab
                    }
                }
            }
            for (int i = y; i < y + s.getLaenge(); i++) {
                if (!gridSpielfeld.getGrid()[x + 1][i].getId().equals("0")) {//Rechts vom Schiff
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
            }
            if (y - 1 <= 0) { //Und wenn oben raus
                for (int i = x; i < x + 2; i++) {
                    if (!gridSpielfeld.getGrid()[i][y + s.getLaenge()].getId().equals("0")) {//Rechts vom Schiff
                        status = false; //Markierung gefunden 
                        break; //Brche suche ab
                    }
                }
            }

            if (y + s.getLaenge() <= gridSpielfeld.getKachelAnzahl() - 1 && y - 1 >= 0) {
                for (int i = x; i < x + 2; i++) {
                    if (!gridSpielfeld.getGrid()[i][y - 1].getId().equals("0")) {//Rechts vom Schiff
                        status = false; //Markierung gefunden 
                        break; //Brche suche ab
                    }
                    if (!gridSpielfeld.getGrid()[i][y + s.getLaenge()].getId().equals("0")) {//Rechts vom Schiff
                        status = false; //Markierung gefunden 
                        break; //Brche suche ab
                    }
                }
            }
        }
        //2. Rechts raus
        if (x + 1 >= gridSpielfeld.getKachelAnzahl() - 1 && y + s.getLaenge() <= gridSpielfeld.getKachelAnzahl()) {
            for (int i = y; i < y + s.getLaenge(); i++) {
                if (!gridSpielfeld.getGrid()[x - 1][i].getId().equals("0")) {//Rechts vom Schiff
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }

            }
            if (y - 1 <= 0) { //Und wenn oben raus
                for (int i = x; i > x - 2; i--) {
                    if (!gridSpielfeld.getGrid()[i][y + s.getLaenge()].getId().equals("0")) {//Rechts vom Schiff
                        status = false; //Markierung gefunden 
                        break; //Brche suche ab
                    }
                }
            }
            if (y + s.getLaenge() >= gridSpielfeld.getKachelAnzahl() - 1) { //Und wenn unten raus
                for (int i = x; i > x - 2; i--) {
                    if (!gridSpielfeld.getGrid()[i][y - 1].getId().equals("0")) {//Rechts vom Schiff
                        status = false; //Markierung gefunden 
                        break; //Brche suche ab
                    }
                }
            }
            if (y + s.getLaenge() <= gridSpielfeld.getKachelAnzahl() - 1 && y - 1 >= 0) { //Rechts raus ohne ohen unten 
                for (int i = x; i > x - 2; i--) {
                    if (!gridSpielfeld.getGrid()[i][y - 1].getId().equals("0")) {
                        status = false; //Markierung gefunden 
                        break; //Brche suche ab
                    }
                    if (!gridSpielfeld.getGrid()[i][y + s.getLaenge()].getId().equals("0")) {
                        status = false; //Markierung gefunden 
                        break; //Brche suche ab
                    }
                }
            }
        }
        //3. Oben Raus
        if (y - 1 <= 0 && x < gridSpielfeld.getKachelAnzahl() - 1 && x - 1 >= 0) {
            for (int i = x - 1; i < x + 2; i++) {
                if (!gridSpielfeld.getGrid()[i][y + s.getLaenge()].getId().equals("0")) {
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
            }
            for (int i = y; i < y + s.getLaenge(); i++) {
                if (!gridSpielfeld.getGrid()[x + 1][i].getId().equals("0")) {//Rechts vom Schiff
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
            }
            for (int i = y; i < y + s.getLaenge(); i++) {
                if (!gridSpielfeld.getGrid()[x - 1][i].getId().equals("0")) {//Rechts vom Schiff
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
            }
        }
        //Unten raus
        if (y >= gridSpielfeld.getKachelAnzahl() - s.getLaenge() && x - 1 >= 0 && x + 1 <= gridSpielfeld.getKachelAnzahl() - 1) {
            for (int i = x - 1; i < x + 2; i++) { //Oben über dem Schiff
                if (!gridSpielfeld.getGrid()[i][y - 1].getId().equals("0")) {
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
            }
            //Neben dem Schiff
            for (int i = y; i < y + s.getLaenge(); i++) {
                if (!gridSpielfeld.getGrid()[x - 1][i].getId().equals("0")) {
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
                if (!gridSpielfeld.getGrid()[x + 1][i].getId().equals("0")) {
                    status = false; //Markierung gefunden 
                    break; //Brche suche ab
                }
            }
        }
        return status;
    }
}
