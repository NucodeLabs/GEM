package ru.nucodelabs.mvvm;

public abstract class VBView<VM extends ViewModel> extends VBUserControl {

    protected VM viewModel;

    public VBView(VM viewModel) {
        this.viewModel = viewModel;
        Initializers.addCloseShortcutMacOS(this);
    }

    public VM getViewModel() {
        return viewModel;
    }
}
