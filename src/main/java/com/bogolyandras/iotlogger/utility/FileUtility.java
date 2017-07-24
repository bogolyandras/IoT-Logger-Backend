package com.bogolyandras.iotlogger.utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class FileUtility {

    public static String getResourceAsString(String path) {
        InputStream stream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(path);
        return new BufferedReader(new InputStreamReader(stream))
                .lines().collect(Collectors.joining("\n"));
    }

}
