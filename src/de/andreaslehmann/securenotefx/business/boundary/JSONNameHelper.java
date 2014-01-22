package de.andreaslehmann.securenotefx.business.boundary;

import java.io.File;
import java.io.FilenameFilter;
import java.util.UUID;

/**
 *
 * @author Andreas
 */
class JSONNameHelper implements FilenameFilter {

    protected static final String FILE_SUFFIX = ".json";

    @Override
    public boolean accept(File file, String string) {
        return string.endsWith(FILE_SUFFIX);
    }

    protected static String buildFilename(String basePath, UUID uuid) {
        StringBuilder b = new StringBuilder(basePath);
        b.append('/');
        b.append(uuid);
        b.append(JSONNameHelper.FILE_SUFFIX);
        return b.toString();
    }
}