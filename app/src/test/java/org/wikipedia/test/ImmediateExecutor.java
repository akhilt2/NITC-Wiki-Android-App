package org.akhil.nitcwiki.test;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;

public class ImmediateExecutor implements Executor {
    @Override
    public void execute(@NonNull Runnable runnable) {
        runnable.run();
    }
}
