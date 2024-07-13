package org.altart.telegrambridge.config;

import org.altart.telegrambridge.StandardMockTest;
import org.altart.telegrambridge.TelegramBridge;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TranslationsTest extends StandardMockTest {
    @Mock
    Plugin mockPlugin;

    @Test
    @DisplayName("Get translation")
    void getTranslation() {
        File resourseFolder = new File("src/test/resources");
        when(mockPlugin.getDataFolder()).thenReturn(resourseFolder);

        TelegramBridge.plugin = mockPlugin;

        Translations translations = new Translations("en");
        List<String> languageCodes = translations.getLoadedLanguages();
        List<String> expectedLanguageCodes = Arrays.asList("en", "es", "jp", "pt", "ru", "uk", "tt");
        assertEquals(expectedLanguageCodes.size(), languageCodes.size());
        for (String lang : expectedLanguageCodes) {
            assertTrue(languageCodes.contains(lang));
        }
        Translations.Translation translation = translations.get(null);
        assertNotNull(translation);
        verify(mockPlugin, times(1)).getDataFolder();
        Field[] fields = translation.getClass().getDeclaredFields();
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new File(resourseFolder, "lang/en.yml"));
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.getName().equals("__$hits$__")) {
                    continue;
                }
                assertEquals(yamlConfiguration.get(field.getName()), field.get(translation));
            } catch (IllegalAccessException e) {
                System.out.println(e.getMessage());
                fail();
            }
        }
    }

    @Test
    @DisplayName("Set default language")
    void setDefaultLang() {
        File resourseFolder = new File("src/test/resources");
        when(mockPlugin.getDataFolder()).thenReturn(resourseFolder);

        TelegramBridge.plugin = mockPlugin;

        Translations translations = new Translations(null);
        try {
            translations.setDefaultLang("pt");
        } catch (Exception e) {
            fail();
        }

        Field[] fields = translations.get().getClass().getDeclaredFields();
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new File(resourseFolder, "lang/pt.yml"));
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.getName().equals("__$hits$__")) {
                    continue;
                }
                assertEquals(yamlConfiguration.get(field.getName()), field.get(translations.get()));
            } catch (IllegalAccessException e) {
                System.out.println(e.getMessage());
                fail();
            }
        }
    }

    @Test
    @DisplayName("Set default language with invalid language and non-existent file")
    void translationExceptions() {
        File resourseFolder = new File("src/test/resources");
        when(mockPlugin.getDataFolder()).thenReturn(resourseFolder);

        TelegramBridge.plugin = mockPlugin;

        Translations translations = new Translations(null);
        assertThrows(Exception.class, () -> translations.setDefaultLang("fr"));

        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(false);
        new Translations.Translation(mockFile);
        verify(mockFile, times(1)).exists();
    }

    @Test
    @DisplayName("Empty translations directory")
    void emptyTranslationsDirectory() {
        File resourseFolder = new File("src/test/resources/empty");
        when(mockPlugin.getDataFolder()).thenReturn(resourseFolder);

        TelegramBridge.plugin = mockPlugin;

        new Translations(null);

        verify(mockPlugin, times(1)).getDataFolder();
        List<String> languageCodes = new Translations(null).getLoadedLanguages();
        for (String lang : languageCodes) {
            verify(mockPlugin, times(1)).saveResource("lang/" + lang + ".yml", false);
        }

        File langDir = new File(resourseFolder, "lang");
        assertTrue(langDir.delete());
        assertTrue(resourseFolder.delete());
    }

    @Test
    @DisplayName("File passed as directory")
    void nullTranslationsDirectory() {
        File fileAsDir = new File("src/test/resources/lang/lang");
        try {
            assertTrue(fileAsDir.createNewFile());
        } catch (IOException e) {
            fail();
        }

        File translationsFolder = new File("src/test/resources/lang");
        when(mockPlugin.getDataFolder()).thenReturn(translationsFolder);

        TelegramBridge.plugin = mockPlugin;

        new Translations(null);

        verify(mockPlugin, times(1)).getDataFolder();
        verify(mockPlugin, never()).saveResource("lang/tt.yml", false);

        assertTrue(fileAsDir.delete());
    }
}
