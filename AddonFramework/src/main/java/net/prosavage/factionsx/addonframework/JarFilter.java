package net.prosavage.factionsx.addonframework;

import java.io.File;
import java.io.FilenameFilter;

public class JarFilter implements FilenameFilter {

    public boolean accept(File dir, String name) {
        String lower = name.toLowerCase();
        return lower.endsWith(".jar") || lower.endsWith(".zip");
    }
}
