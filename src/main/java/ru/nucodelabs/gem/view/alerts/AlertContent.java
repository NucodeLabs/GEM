package ru.nucodelabs.gem.view.alerts;

import java.util.ResourceBundle;

public class AlertContent {
    private AlertContent() {
    }

    private static final ResourceBundle uiProperties = ResourceBundle.getBundle("ru/nucodelabs/gem/UI");
    public static final String INCORRECT_FILE = uiProperties.getString("fileError");
    public static final String UNABLE_TO_DRAW_CHART = uiProperties.getString("unableToDrawChart");
    public static final String NO_LIB = uiProperties.getString("noLib");
    public static final String COMPATIBILITY_MODE = uiProperties.getString("compatibilityMode");
    public static final String EXP_STT_MISMATCH = uiProperties.getString("EXPSTTMismatch");
    public static final String MINIMAL_DATA = uiProperties.getString("minimalDataWillBeDisplayed");
}
