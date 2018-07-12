package com.company;

import jm.constants.Pitches;
import jm.music.data.*;
import jm.util.Read;
import jm.util.Write;

import static jm.constants.Chords.MAJOR;
import static jm.constants.Pitches.*;
import static jm.music.tools.PhraseAnalysis.MAJOR_SCALE;
import static jm.music.tools.PhraseAnalysis.pitchToDegree;
import static jm.music.tools.ga.NormalDistributionFE.tonic;

public class NoteSize {

    public static void main(String[] args) {

//        int[] cof = new int[12];
//        cof[0] = 1;
//        cof[1] = 8;
//        cof[2] = 3;
//        cof[3] = 10;
//        cof[4] = 5;
//        cof[5] = 12;
//        cof[6] = 7;
//        cof[7] = 2;
//        cof[8] = 9;
//        cof[9] = 4;
//        cof[10] = 11;
//        cof[11] = 6;
//
//        int[] majScale = new int[14];
//        majScale[-2 + 2] = 2;
//        majScale[-1 + 2] = 2;
//        majScale[0 + 2] = 2;
//        majScale[1 + 2] = 1;
//        majScale[2 + 2] = 2;
//        majScale[3 + 2] = 2;
//        majScale[4 + 2] = 1;
//        majScale[5 + 2] = 2;
//        majScale[6 + 2] = 2;
//        majScale[7 + 2] = 2;
//        majScale[8 + 2] = 1;
//        majScale[9 + 2] = 2;
//        majScale[10 + 2] = 2;
//        majScale[11 + 2] = 1;
//
//        int[] scaleDegree = new int[12];
//        scaleDegree[0] = 1 + 2;     // +2 to avoid array index out of bounds
//        scaleDegree[2] = 2 + 2;
//        scaleDegree[4] = 3 + 2;
//        scaleDegree[5] = 4 + 2;
//        scaleDegree[7] = 5 + 2;
//        scaleDegree[9] = 6 + 2;
//        scaleDegree[11] = 7 + 2;
//
//        int melodyNote = e1;
//        int tonic = c1;
//
//        int melodyDegree = pitchToDegree(melodyNote, tonic);
//
//        System.out.println("melody deg: " + melodyDegree);
//
//        int pos1 = melodyNote;
//        int pos2 = melodyNote + majScale[scaleDegree[melodyDegree] + 1] + majScale[scaleDegree[melodyDegree] + 2];
//        int pos3 = melodyNote + majScale[scaleDegree[melodyDegree] + 1] + majScale[scaleDegree[melodyDegree] + 2]
//                      + majScale[scaleDegree[melodyDegree] + 3] + majScale[scaleDegree[melodyDegree] + 4];
//
//        Note note1 = new Note(pos1, 2);
//        System.out.println(note1.getName());
//
//        Note note2 = new Note(pos2, 2);
//        System.out.println(note2.getName());
//
//        Note note3 = new Note(pos3, 2);
//        System.out.println(note3.getName());

        Score score = new Score();
        Read.midi(score);
        System.out.println("time sig: " + score.getNumerator());
        Part[] parts = score.getPartArray();
        System.out.println("number of parts: " + parts.length);

        // for every part
        for (int i = 0; i < parts.length; i++) {
            Phrase[] phrases = parts[i].getPhraseArray();

            // for every phrase
            for (int j = 0; j < phrases.length; j++) {
                int[] notes = (phrases[j]).getPitchArray();
                double[] rhythm = (phrases[j]).getRhythmArray();

                double counter = 0;
                // for every note
                for (int k = 0; k < notes.length; k++) {

                    if (rhythm[k] > 0.9 && rhythm[k] < 1.1)
                        rhythm[k] = 1;

                    else if (rhythm[k] > 1.8 && rhythm[k] < 2.1)
                        rhythm[k] = 2;

                    System.out.println(rhythm[k]);

                    counter += rhythm[k];

                    // adds a chord if on beat 1
                    if (counter - rhythm[k] == 0.0)
                        System.out.println("add chord");

                    else counter = 0.0;

                    if (rhythm[k] == 2.0)
                        counter = 0.0;






                }
            }
        }
    }
}

