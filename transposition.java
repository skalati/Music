package com.company;

import jm.music.data.*;
import jm.util.Read;
import jm.util.Write;

import java.util.HashMap;

import static jm.constants.Pitches.*;

public class transposition {
    private Score majorScore;

    // constructor
    public transposition(Score score) {
        majorScore = score;
    }

    // transposes to minor version of given key
    public Score transpose() {

        Score minorScore = new Score();
        minorScore.setNumerator(majorScore.getNumerator());
        minorScore.setDenominator(majorScore.getDenominator());
        minorScore.setTempo(majorScore.getTempo());
        int keySig = majorScore.getKeySignature();
        Part[] parts = majorScore.getPartArray();

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

        // for every part in the score
        for (int l = 0; l < parts.length; l++) {

            Phrase[] phrases = parts[l].getPhraseArray();
            int instrument = parts[l].getInstrument();
            Part minorPart = new Part();
            minorPart.setInstrument(instrument);
            CPhrase chords = new CPhrase();

            // for every phrase
            for (int i = 0; i < phrases.length; i++) {
                int[] notes = phrases[i].getPitchArray();
                double[] rhythm = phrases[i].getRhythmArray();
                int key = keySigs.get(keySig);
                int third = key + 4;
                int sixth = key + 9;
                int seventh = key + 11;

                // for every note
                for (int j = 0; j < notes.length; j++) {

                    // for every octave
                    for (int k = 0; k < 10; k++) {
                        if (notes[j] == (third + 12 * k))
                            notes[j] = (third + 12 * k) - 1;

                        if (notes[j] == (sixth + 12 * k))
                            notes[j] = (sixth + 12 * k) - 1;

                        if (notes[j] == (seventh + 12 * k))
                            notes[j] = (seventh + 12 * k) - 1;
                    }
                }
                Phrase phrase = new Phrase(phrases[i].getStartTime());
                phrase.addNoteList(notes, rhythm);
                chords.addPhrase(phrase);

            }
            minorPart.addCPhrase(chords);
            minorScore.addPart(minorPart);
        }
        return minorScore;
    }

    public static void main(String[] args) {
        Score scoreToTranspose = new Score();
        Read.midi(scoreToTranspose);
        transposition minorScore = new transposition(scoreToTranspose);
        Score newScore = minorScore.transpose();
        Write.midi(newScore);
    }
}
