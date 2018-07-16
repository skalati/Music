package com.company;
import jm.music.data.*;
import jm.util.Read;
import jm.util.Write;
import java.util.HashMap;
import static jm.constants.Pitches.*;
import static jm.music.tools.PhraseAnalysis.pitchToDegree;

public class Harmonizer {
    private Score score;          // score that is read in
    private int[] cof;            // distances on the circle of fifths
    private int[] majScale;       // whole - whole - half pattern
    private int[] scaleDegree;    // converts from chromatic scale degree
    private int center;

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
        majScale[4 + 2] = 1;     // 4th scale degree
        majScale[5 + 2] = 2;     // 5th scale degree
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

    // harmonizes a melody line
    public Score harmonize() {
        Part[] parts = score.getPartArray();
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
        int instrument = parts[0].getInstrument();
        Part chordPart = new Part();
        chordPart.setInstrument(instrument);
        CPhrase chordPhrase = new CPhrase();
        int[] notes = phrases[0].getPitchArray();
        double[] rhythm = phrases[0].getRhythmArray();
        int tonic = keySigs.get(keySig);

        // for every note
        double counter = 0;
        boolean hasBeat1 = false;
        boolean hasBeat3 = false;

        for (int i = 0; i < notes.length; i++) {

            if (rhythm[i] > 3.8 && rhythm[i] < 4.2)         // whole note and whole rest
                rhythm[i] = 4.0;

            else if (rhythm[i] > 2.7 && rhythm[i] < 3.1)    // dotted half note
                rhythm[i] = 3.0;

            else if (rhythm[i] > 1.8 && rhythm[i] < 2.2)    // half note and half rest
                rhythm[i] = 2.0;

            else if (rhythm[i] > 1.4 && rhythm[i] < 1.6)    // dotted quarter note
                rhythm[i] = 1.5;

            else if (rhythm[i] > 0.9 && rhythm[i] < 1.2)    // quarter note and quarter rest
                rhythm[i] = 1.0;

            else if (rhythm[i] > 0.4 && rhythm[i] < 0.6)    // eight note and eight rest
                rhythm[i] = 0.5;

            else if (rhythm[i] > 0.24 && rhythm[i] < 0.28)  // sixteenth note and sixteenth rest
                rhythm[i] = 0.25;

            counter += rhythm[i];

            // does not harmonize if note is a rest
            if (notes[i] < 0)
                continue;

            int melDeg = pitchToDegree(notes[i], tonic);                                                                  // buggy?
            System.out.println("mel deg: " + melDeg);
            if (melDeg != 0 && melDeg != 2 && melDeg != 4 && melDeg != 5 && melDeg != 7 && melDeg != 9 && melDeg != 11) {
                System.out.println("continue:");
                continue;
            }

            // adds chord on beat 1
            if (!hasBeat1) {
                int[] chord = addChord(notes[i], tonic);
                chordPhrase.addChord(chord, 2);
                hasBeat1 = true;

                // uses the third of first chord as tonal center
                if (i == 0)
                    center = chord[1];
            }

            // adds chord on beat 3
            if (counter >= 3.0 && !hasBeat3) {
                int[] chord = addChord(notes[i], tonic);
                chordPhrase.addChord(chord, 2);
                hasBeat3 = true;
            }

            // resets the measure
            if (counter == 4.0) {
                hasBeat1 = false;
                hasBeat3 = false;
                counter = 0.0;
            }
        }
        chordPart.addCPhrase(chordPhrase);
        score.addPart(chordPart);

        return score;
    }

    // return the distance between two notes on the circle of fifths
    private int distance(int root, int melodyNote) {
        return cof[pitchToDegree(melodyNote, c0)] - cof[pitchToDegree(root, c0)];
    }

    // generates 3 possible chords and adds the most probable
    private int[] addChord(int melodyNote, int tonic) {

        while (melodyNote >= 0)
            melodyNote = melodyNote - 12;

        int melodyDegree = pitchToDegree(melodyNote, tonic);

        // positions of the melody note in the triad
        int pos1 = melodyNote;
        int pos2 = melodyNote - majScale[scaleDegree[melodyDegree]] - majScale[scaleDegree[melodyDegree] - 1];
        int pos3 = melodyNote - majScale[scaleDegree[melodyDegree]] - majScale[scaleDegree[melodyDegree] - 1]
                          - majScale[scaleDegree[melodyDegree] - 2] - majScale[scaleDegree[melodyDegree] - 3];

        double pos1Prob = (10 - Math.abs(distance(pos1, melodyNote)))/10.0;
        double pos2Prob = (10 - Math.abs(distance(pos2, melodyNote)))/10.0;
        double pos3Prob = (10 - Math.abs(distance(pos3, melodyNote)))/10.0;
        double sumOfProbs = pos1Prob + pos2Prob + pos3Prob;

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

        // creates and returns the chord
        int root;
        if (rand <= pos1Prob)
            root = pos1;

        else if (rand <= pos1Prob + pos2Prob)
            root = pos2;

        else root = pos3;

        int rootDeg = pitchToDegree(root, tonic);
        int[] chord = new int[3];
        int octave = 72;
        chord[0] = root + octave;
        chord[1] = root + majScale[scaleDegree[rootDeg] + 1] + majScale[scaleDegree[rootDeg] + 2] + octave;
        chord[2] = root + majScale[scaleDegree[rootDeg] + 1] + majScale[scaleDegree[rootDeg] + 2]
                + majScale[scaleDegree[rootDeg] + 3] + majScale[scaleDegree[rootDeg] + 4] + octave;

        return centerChord(chord);
    }

    // centers the chord
    // if the interval is larger than a Major 7th, then drop down an octave
    private int[] centerChord (int[] chord) {
        for (int i = 0; i < chord.length; i++) {
            if (chord[i] - center >= 11)
                chord[i] = chord[i] - 12;
        }
        return chord;
    }

    public static void main(String[] args) {
        Score score = new Score();
        Read.midi(score);
        Harmonizer harmony = new Harmonizer(score);
        Score hScore = harmony.harmonize();
        Write.midi(hScore);
    }
}