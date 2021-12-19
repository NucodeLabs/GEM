package ru.nucodelabs.gem.view.main;

import ru.nucodelabs.gem.core.ViewManager;
import ru.nucodelabs.mvvm.Model;
import ru.nucodelabs.mvvm.ViewModel;

public class WelcomeViewModel extends ViewModel<Model> {

    public WelcomeViewModel(ViewManager viewManager) {
        super(null, viewManager);
    }

    private WelcomeViewModel(Model model, ViewManager viewManager) {
        super(model, viewManager);
    }

    public void switchToMainView() {
        viewManager.openMainViewWithImport();
    }
}
