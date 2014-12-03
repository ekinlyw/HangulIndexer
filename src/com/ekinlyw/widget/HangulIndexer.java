
package com.ekinlyw.widget;

import android.database.Cursor;
import android.util.Log;
import android.widget.AlphabetIndexer;

import java.text.Collator;

public class HangulIndexer extends AlphabetIndexer {

    private static final String TAG = "HangulIndexer";

    private static final char[] HANGUL_CHOSEONG_LETTERS = new char[] {
            '#', '\u1100', '\u1101', '\u1102', '\u1103', '\u1104', '\u1105', '\u1106', '\u1107',
            '\u1108', '\u1109', '\u110A', '\u110B', '\u110C', '\u110D', '\u110E', '\u110F',
            '\u1110', '\u1111', '\u1112'
    };

    // 'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ',
    // 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' (U+1100 - U+1112)
    private static final int CHOSEONG_COUNT = 19;

    // 'ᅡ', 'ᅢ', 'ᅣ', 'ᅤ', 'ᅥ', 'ᅦ', 'ᅧ', 'ᅨ', 'ᅩ', 'ᅪ', 'ᅫ', 'ᅬ', 'ᅭ', 'ᅮ',
    // 'ᅯ', 'ᅰ', 'ᅱ', 'ᅲ', 'ᅳ', 'ᅴ', 'ᅵ' (U+1161 - U+1175)
    private static final int JUNGSEONG_COUNT = 21;

    // 'ᆨ', 'ᆩ', 'ᆪ', 'ᆫ', 'ᆬ', 'ᆭ', 'ᆮ', 'ᆯ', 'ᆰ', 'ᆱ', 'ᆲ', 'ᆳ', 'ᆴ', 'ᆵ',
    // 'ᆶ', 'ᆷ', 'ᆸ', 'ᆹ', 'ᆺ, 'ᆻ', 'ᆼ', 'ᆽ', 'ᆾ', 'ᆿ', 'ᇀ', 'ᇁ', 'ᇂ'
    // (U+11A8 - U+11C2)
    private static final int JONGSEONG_COUNT = 28;

    private String[] mHangulArray;

    private Collator mCollator;

    public HangulIndexer(Cursor cursor, int sortedColumnIndex) {
        super(cursor, sortedColumnIndex, String.valueOf(HANGUL_CHOSEONG_LETTERS));

        mCollator = Collator.getInstance();
        mHangulArray = new String[HANGUL_CHOSEONG_LETTERS.length];

        for (int i = 0; i < HANGUL_CHOSEONG_LETTERS.length; i++) {
            mHangulArray[i] = Character.toString(HANGUL_CHOSEONG_LETTERS[i]);
        }
    }

    @Override
    protected int compare(String word, String letter) {
        Log.v(TAG, "compare(" + word + "," + letter + ")");

        word = word.replaceAll("[\\[\\]\\(\\)\"'.,?!]", "").trim();
        String firstLetter;

        if (word.length() == 0) {
            firstLetter = "#";
        } else {
            int codePoint = word.codePointAt(0);

            if (isAlphabet(codePoint) || Character.isDigit(codePoint)) {
                // TODO: Add alphabet in sections then handle the code point.
                // In current, only handles if the code point is Hangul syllable
                firstLetter = "#";
            } else if (isHangul(codePoint)) {
                // a code point of Hangul syllable is consisted of as follows;

                // U+AC00 + ((CHOSEONG * 21 ) + JUNGSEONG) * 28 + JONGSEONG
                // U+AC00 is the start of Hangul syllable
                // 21 is the number of Choseong
                // 28 is the number of Jongseong
                int choseongIndex = (codePoint - ('\uAC00')) / (JUNGSEONG_COUNT * JONGSEONG_COUNT);

                // Got Choseong from the code point.
                firstLetter = Character.toString(HANGUL_CHOSEONG_LETTERS[choseongIndex + 1]);

                Log.v(TAG, word + " : It's a Hangul syllable, Choseong=" + firstLetter);
            } else {
                // TODO: Should handle other cases
                // For this case, the position for section, or section for
                // position might be wrong.
                firstLetter = word.substring(0, 1);
            }
        }

        return mCollator.compare(firstLetter, letter);
    }

    @Override
    public Object[] getSections() {
        return mHangulArray;
    }

    /**
     * Indicates whether the code point is alphabetic
     * 
     * @param codePoint the code point to check.
     * @return true if codePoint is alphabetic; false otherwise.
     */
    private boolean isAlphabet(int codePoint) {
        if (codePoint >= '\u0061' && codePoint <= '\u007A') { // Lower case
            return true;
        }

        if (codePoint >= '\u0041' && codePoint <= '\u005A') { // Upper case
            return true;
        }

        return false;
    }

    /**
     * Indicates whether the code point is a Hangul syllable
     * 
     * @param codePoint the code point to check.
     * @return true if codePoint is a Hangul syllable; false otherwise.
     */
    private boolean isHangul(int codePoint) {
        // Range of Hangul syllables is '\uAC00' to '\uD7A3'
        // '\uAC00' is '가', '\uD7A3' is '힣'
        if (codePoint >= '\uAC00' && codePoint <= '\uD7A3') {
            return true;
        }
        return false;
    }
}
