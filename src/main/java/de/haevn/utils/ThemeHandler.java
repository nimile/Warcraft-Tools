package de.haevn.utils;

import com.google.common.flogger.FluentLogger;
import de.haevn.Main;
import javafx.beans.property.SimpleObjectProperty;
import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class ThemeHandler {
    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();
    private static final ThemeHandler INSTANCE = new ThemeHandler();

    private final List<String> themes = new ArrayList<>();
    private final SimpleObjectProperty<String> currentTheme = new SimpleObjectProperty<>();

    private ThemeHandler() {
        currentTheme.addListener((observable, oldValue, newValue) -> setCurrentTheme(newValue));
        String theme = PropertyHandler.getInstance("config").get("app.theme");
        setCurrentTheme(theme.toUpperCase());

        var found = new File(FileIO.getRootPath() + "styles").listFiles((dir, name) -> name.endsWith(".css"));
        if (null != found && found.length > 0) {
            for (File file : found) {
                String name = file.getName();
                name = name.substring(0, name.lastIndexOf("."));
                themes.add(name);
            }
        }else {
            LOGGER.atSevere().log("Could not find any themes");
        }
    }

    public static ThemeHandler getInstance() {
        return INSTANCE;
    }

    public String[] getThemes() {
        return themes.toArray(new String[0]);
    }

    public SimpleObjectProperty<String> getCurrentTheme() {
        return currentTheme;
    }

    private void setCurrentTheme(String currentTheme) {
        LOGGER.atFine().log("Setting theme to: %s", currentTheme);
        if (null == currentTheme) {
            LOGGER.atWarning().log("Theme is null, setting to default");
            currentTheme = "";
        }
        this.currentTheme.set(currentTheme);
        PropertyHandler.getInstance("config").set("app.theme", currentTheme);
        reload();
    }

    @SneakyThrows
    public void reload() {
        LOGGER.atFine().log("Reloading theme");
        URL uri = FileIO.getURI(FileIO.getRootPath() + "styles/" + currentTheme.get() + ".css").toURL();

        LOGGER.atInfo().log("Loading stylesheet: %s", uri);
        if (!new File(uri.getPath()).exists()) {
            LOGGER.atSevere().log("Could not find stylesheet: %s use fallback", uri);
        }
        Main.loadStylesheet(uri);
    }
}