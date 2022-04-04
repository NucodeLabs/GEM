package ru.nucodelabs.gem.app.model;

import ru.nucodelabs.data.ves.Section;

import java.util.concurrent.Flow;

public abstract class AbstractSectionObserver implements Flow.Subscriber<Section> {

    protected Flow.Subscription subscription;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    abstract public void onNext(Section item);

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println("AbstractSectionObserver.onComplete");
    }
}
