package com.jumao.greeting.transform.task;

import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.Status;
import com.android.build.api.transform.TransformInvocation;
import com.jumao.greeting.transform.AsmHelper;
import com.jumao.greeting.transform.model.InputModel;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

public class JarInputHandleTask implements Runnable {

    private final JarInput mJarInput;
    private final TransformInvocation mTransformInvocation;

    public JarInputHandleTask(@Nonnull InputModel model) {
        mJarInput = model.getJarInput();
        mTransformInvocation = model.getTransformInvocation();
    }

    @Override
    public void run() {
        Status status = mJarInput.getStatus();
        File dest = mTransformInvocation.getOutputProvider().getContentLocation(
                mJarInput.getName(),
                mJarInput.getContentTypes(),
                mJarInput.getScopes(),
                Format.JAR);
        if (mTransformInvocation.isIncremental()) {
            switch (status) {
                case NOTCHANGED: {
                    break;
                }
                case ADDED:
                case CHANGED: {
                    AsmHelper.transform(mJarInput.getFile(), dest);
                    break;
                }
                case REMOVED: {
                    if (dest.exists()) {
                        try {
                            FileUtils.forceDelete(dest);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
            }
        } else {
            AsmHelper.transform(mJarInput.getFile(), dest);
        }
    }
}
