package ru.nucodelabs.mvvm;

public abstract class VBView<VM extends ViewModel<? extends Model>> extends VBUserControl {

    protected VM viewModel;

    public VBView(VM viewModel) {
        this.viewModel = viewModel;
    }

    public VM getViewModel() {
        return viewModel;
    }
}
