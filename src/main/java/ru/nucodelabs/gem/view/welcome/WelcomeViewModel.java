package ru.nucodelabs.gem.view.welcome;

import ru.nucodelabs.gem.core.ViewManager;
import ru.nucodelabs.mvvm.ViewModel;

public class WelcomeViewModel extends ViewModel {

    public WelcomeViewModel(ViewManager viewManager) {
        super(viewManager);
    }

    public void switchToMainView() {
        viewManager.newMainViewWithImportEXP(this);
    }
}
