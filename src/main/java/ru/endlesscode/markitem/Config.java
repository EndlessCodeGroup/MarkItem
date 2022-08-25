package ru.endlesscode.markitem;

import org.bukkit.configuration.Configuration;
import org.jetbrains.annotations.NotNull;
import ru.endlesscode.markitem.util.Strings;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Config {

    private boolean enabled = true;

    private String markTexture = "";
    private String markName = "";
    private List<String> markText = Collections.emptyList();
    private List<String> markLore = Collections.emptyList();

    private String recipeTitle = "";
    private List<String> recipeDescription = Collections.emptyList();

    private List<Pattern> allowed = Collections.emptyList();
    private List<Pattern> denied = Collections.emptyList();

    private final Configuration configuration;

    Config(Configuration configuration) {
        this.configuration = configuration;
        load();
    }

    private void load() {
        enabled = configuration.getBoolean("enabled", true);

        markTexture = configuration.getString("mark.texture", "");
        markName = getColorizedString("mark.name");
        markText = getColorizedStringList("mark.text");
        markLore = getColorizedStringList("mark.lore");

        recipeTitle = getColorizedString("recipe.title");
        recipeDescription = getColorizedStringList("recipe.description");

        allowed = getSimplePatternList("allowed");
        denied = getSimplePatternList("denied");
    }

    private String getColorizedString(String path) {
        return Strings.colorize(configuration.getString(path, ""));
    }

    private List<String> getColorizedStringList(String path) {
        return configuration.getStringList(path)
                .stream()
                .map(Strings::colorize)
                .collect(Collectors.toList());
    }

    private @NotNull List<Pattern> getSimplePatternList(String path) {
        return configuration.getStringList(path)
                .stream()
                .map(Strings::parseSimplePattern)
                .collect(Collectors.toList());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getMarkTexture() {
        return markTexture;
    }

    public String getMarkName() {
        return markName;
    }

    public List<String> getMarkText() {
        return markText;
    }

    public List<String> getMarkLore() {
        return markLore;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public List<String> getRecipeDescription() {
        return recipeDescription;
    }

    public List<Pattern> getAllowed() {
        return allowed;
    }

    public List<Pattern> getDenied() {
        return denied;
    }
}
