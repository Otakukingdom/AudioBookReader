package com.otakukingdom.audiobook.observers;

import com.otakukingdom.audiobook.model.AudioBookFile;

import java.util.List;

/**
 * Created by mistlight on 11/25/2016.
 */
public interface FileListObserver {

    public void fileListUpdated(List<AudioBookFile> newFileList);

    public void selectedFileUpdated(AudioBookFile selectedFile);
}
