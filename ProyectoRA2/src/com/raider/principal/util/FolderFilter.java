package com.raider.principal.util;

import java.io.File;

/**
 * Created by raider on 10/11/15.
 */

// Clase creada para hacer el filtro de carpetas

public class FolderFilter extends javax.swing.filechooser.FileFilter{
    @Override
    public boolean accept(File f) {
        return f.isDirectory();
    }

    @Override
    public String getDescription() {
        return null;
    }
}
