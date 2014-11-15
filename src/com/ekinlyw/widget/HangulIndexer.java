
package com.ekinlyw.widget;

import android.database.Cursor;
import android.widget.AlphabetIndexer;

public class HangulIndexer extends AlphabetIndexer {

    private static final String TAG = "HangulIndexer";

    public HangulIndexer(Cursor cursor, int sortedColumnIndex, CharSequence alphabet) {
        super(cursor, sortedColumnIndex, alphabet);
    }
}
