package com.company;

import jm.music.data.*;
import jm.util.Read;
import jm.util.Write;
import java.util.HashMap;

import static jm.constants.Pitches.*;
import static jm.music.tools.PhraseAnalysis.pitchToDegree;

public class Harmonizer {
    private Score score;
    private int[] cof;
    private  int[] majScale;
    private  int[] scaleDegree;

    public Harmonizer(Score regScore) {
        cof = new int[12];
        cof[0] = 1;
        cof[1] = 8;
        cof[2] = 3;
        cof[3] = 10;
        cof[4] = 5;
        cof[5] = 12;
        cof[6] = 7;
        cof[7] = 2;
        cof[8] = 9;
        cof[9] = 4;
        cof[10] = 11;
        cof[11] = 6;

        majScale = new int[14];
        majScale[-2 + 2] = 2;
        majScale[-1 + 2] = 2;
        majScale[0 + 2] = 2;
        majScale[1 + 2] = 1;
        majScale[2 + 2] = 2;
        majScale[3 + 2] = 2;
        majScale[4 + 2] = 1;
        majScale[5 + 2] = 2;
        majScale[6 + 2] = 2;
        majScale[7 + 2] = 2;
        majScale[8 + 2] = 1;
        majScale[9 + 2] = 2;
        majScale[10 + 2] = 2;
        majScale[11 + 2] = 1;

        scaleDegree = new int[12];
        scaleDegree[0] = 1 + 2;     // +2 to avoid array index out of bounds
        scaleDegree[2] = 2 + 2;
        scaleDegree[4] = 3 + 2;
        scaleDegree[5] = 4 + 2;
        scaleDegree[7] = 5 + 2;
        scaleDegree[9] = 6 + 2;
        scaleDegree[11] = 7 + 2;

        score = regScore;
    }

    public Score harmonize(String note) {
        int chordLength = 0;

        if (note.equalsIgnoreCase("half note")) {
            chordLength = 2;
//            System.out.println("half note");

        }


        Part[] parts = score.getPartArray();
//        System.out.println("num of Parts: " + parts.length);
        int keySig = score.getKeySignature();

        HashMap<Integer, Integer> keySigs = new HashMap<>();
        keySigs.put(0, c0);
        keySigs.put(1, g0);
        keySigs.put(2, d0);
        keySigs.put(3, a0);
        keySigs.put(4, e0);
        keySigs.put(5, b0);
        keySigs.put(6, fs0);
        keySigs.put(7, cs0);
        keySigs.put(-1, f0);
        keySigs.put(-2, bf0);
        keySigs.put(-3, ef0);
        keySigs.put(-4, af0);
        keySigs.put(-5, df0);
        keySigs.put(-6, gf0);
        keySigs.put(-7, cf0);

        Phrase[] phrases = parts[0].getPhraseArray();
//        System.out.println("num of phrases: " + phrases.length);
        int instrument = parts[0].getInstrument();
        Part chordPart = new Part();
        chordPart.setInstrument(instrument);
        CPhrase chordPhrase = new CPhrase();
        int[] notes = phrases[0].getPitchArray();
        double[] rhythm = phrases[0].getRhythmArray();
        int tonic = keySigs.get(keySig);

        // for every note
        double counter = 0;
        for (int i = 0; i < notes.length; i++) {

            System.out.println("rhythm: " + rhythm[i]);

            if (rhythm[i] > 0.9 && rhythm[i] < 1.1)
                rhythm[i] = 1.0;

            else if (rhythm[i] > 1.8 && rhythm[i] < 2.1)
                rhythm[i] = 2.0;

            System.out.println("rhythm: " + rhythm[i]);


            counter += rhythm[i];

            // adds chord on beat 1
            if (counter - rhythm[i] == 0.0) {                                                                             // not reading half notes properly
                int[] chord = addChord(notes[i], tonic);
                chordPhrase.addChord(chord, chordLength);                                                                        // remember to fix length

            }
            // adds chord on beat 3
            else if (counter >= 3.0) {
                int[] chord = addChord(notes[i], tonic);
                chordPhrase.addChord(chord, chordLength);
            }

            if (counter == 4.0)
                counter = 0.0;



        }

        chordPart.addCPhrase(chordPhrase);
        score.addPart(chordPart);

        return score;
    }

    private int distance(int root, int melodyNote) {
        return cof[pitchToDegree(melodyNote, c0)] - cof[pitchToDegree(root, c0)];
    }

    private int[] addChord(int melodyNote, int tonic) {

        Note melody = new Note(melodyNote, 2);
//        System.out.println("melody note: " + melody.getName());

        while (melodyNote > 12)
            melodyNote = melodyNote - 12;

        System.out.println("melody note: " + melodyNote);

        int melodyDegree = pitchToDegree(melodyNote, tonic);
//        System.out.println("melody degree: " + melodyDegree);
//        System.out.println();

        // positions in the triad
        int pos1 = melodyNote;
        int pos2 = melodyNote - majScale[scaleDegree[melodyDegree]] - majScale[scaleDegree[melodyDegree] - 1];
        int pos3 = melodyNote - majScale[scaleDegree[melodyDegree]] - majScale[scaleDegree[melodyDegree] - 1]
                - majScale[scaleDegree[melodyDegree] - 2] - majScale[scaleDegree[melodyDegree] - 3];

//        Note pos1Note = new Note(pos1, 2);
//        Note pos2Note = new Note(pos2, 2);
//        Note pos3Note = new Note(pos3, 2);

//        System.out.println("pos1: " + pos1Note.getName());
//        System.out.println("pos2: " + pos2Note.getName());
//        System.out.println("pos3: " + pos3Note.getName());
//        System.out.println();

        double pos1Prob = (10 - Math.abs(distance(pos1, melodyNote)))/10.0;
        double pos2Prob = (10 - Math.abs(distance(pos2, melodyNote)))/10.0;
        double pos3Prob = (10 - Math.abs(distance(pos3, melodyNote)))/10.0;

//        System.out.println("dist 1: " + Math.abs(distance(pos1, melodyNote)));
//        System.out.println("dist 2: " + Math.abs(distance(pos2, melodyNote)));
//        System.out.println("dist 3: " + Math.abs(distance(pos3, melodyNote)));
//        System.out.println();
//
//        System.out.println("pos1Prob: " + pos1Prob);
//        System.out.println("pos2Prob: " + pos2Prob);
//        System.out.println("pos3Prob: " + pos3Prob);

        double sumOfProbs = pos1Prob + pos2Prob + pos3Prob;
//        System.out.println("sumOfProbs: " + sumOfProbs);
//        System.out.println();

        pos1Prob = pos1Prob/sumOfProbs;
        pos2Prob = pos2Prob/sumOfProbs;
        pos3Prob = pos3Prob/sumOfProbs;

        double rand = Math.random();

        // sorts the probabilities and corresponding root
        if (pos1Prob > pos2Prob) {
            double temp = pos1Prob;
            pos1Prob = pos2Prob;
            pos2Prob = temp;

            int rootTemp = pos1;
            pos1 = pos2;
            pos2 = rootTemp;
        }

        if (pos2Prob > pos3Prob) {
            double temp = pos2Prob;
            pos2Prob = pos3Prob;
            pos3Prob = temp;

            int rootTemp = pos2;
            pos2 = pos3;
            pos3 = rootTemp;
        }

        if (pos1Prob > pos2Prob) {
            double temp = pos1Prob;
            pos1Prob = pos2Prob;
            pos2Prob = temp;

            int rootTemp = pos1;
            pos1 = pos2;
            pos2 = rootTemp;
        }

//        System.out.println("after the sort");
//        Note sort1 = new Note(pos1, 2);
//        System.out.println("pos1Prob: " + pos1Prob);
//        System.out.println("pos1: " + sort1.getName());
//        Note sort2 = new Note(pos2, 2);
//        System.out.println("pos2Prob: " + pos2Prob);
//        System.out.println("pos2: " + sort2.getName());
//        Note sort3 = new Note(pos3, 2);
//        System.out.println("pos3Prob: " + pos3Prob);
//        System.out.println("pos3: " + sort3.getName());
//        System.out.println();




        // creates and returns the chord
        int root;
        if (rand <= pos1Prob) {
            root = pos1;
//            System.out.println("pos1 selected: " + sort1.getName());
        }

        else if (rand <= pos1Prob + pos2Prob) {
            root = pos2;
//            System.out.println("pos2 selected: " + sort2.getName());
        }
        else {
            root = pos3;
//            System.out.println("pos3 selected: " + sort3.getName());
        }

//        System.out.println("random: " + rand);
//        Note rootNote = new Note(root, 2);
//        System.out.println("root: " + rootNote.getName());

        int rootDeg = pitchToDegree(root, tonic);

        int[] chord = new int[3];
        int octave = 48;
        chord[0] = root + octave;
        chord[1] = root + majScale[scaleDegree[rootDeg] + 1] + majScale[scaleDegree[rootDeg] + 2] + octave;
        chord[2] = root + majScale[scaleDegree[rootDeg] + 1] + majScale[scaleDegree[rootDeg] + 2]
                + majScale[scaleDegree[rootDeg] + 3] + majScale[scaleDegree[rootDeg] + 4] + octave;

//        System.out.println("The selected chord is: ");
        for (int i = 0; i < chord.length; i++) {
            Note chordNote = new Note(chord[i], 2);
//            System.out.print(chordNote.getName() + " ");
//            System.out.println(chord[i] + " ");
        }
//        System.out.println();

        return chord;

    }

    public static void main(String[] args) {
        Score score = new Score();
        Read.midi(score);
        Harmonizer harmony = new Harmonizer(score);
        Score hScore = harmony.harmonize("half note");
        Write.midi(hScore);
    }
}
