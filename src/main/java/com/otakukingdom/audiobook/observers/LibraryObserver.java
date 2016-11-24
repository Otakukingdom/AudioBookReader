package com.otakukingdom.audiobook.observers;

import com.otakukingdom.audiobook.model.AudioBook;

/**
 * Created by mistlight on 11/20/2016.
 */
public interface LibraryObserver {
    void selectionUpdated(AudioBook newSelection);
}
