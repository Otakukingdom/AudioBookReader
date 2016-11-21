package com.otakukingdom.audiobook.services;

/**
 * Created by mistlight on 11/20/2016.
 */
public class LibraryService {

    public LibraryService(AudioBookScanService audioBookScanService) {
        this.audioBookScanService = audioBookScanService;
    }

    private AudioBookScanService audioBookScanService;
}
