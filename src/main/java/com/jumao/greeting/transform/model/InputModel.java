package com.jumao.greeting.transform.model;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.TransformInvocation;

import java.io.Serializable;

public class InputModel implements Serializable {

    private DirectoryInput directoryInput;
    private JarInput jarInput;
    private TransformInvocation transformInvocation;

    public InputModel(JarInput jarInput, TransformInvocation transformInvocation) {
        this.jarInput = jarInput;
        this.transformInvocation = transformInvocation;
    }

    public InputModel(DirectoryInput directoryInput, TransformInvocation transformInvocation) {
        this.directoryInput = directoryInput;
        this.transformInvocation = transformInvocation;
    }

    public DirectoryInput getDirectoryInput() {
        return directoryInput;
    }

    public void setDirectoryInput(DirectoryInput directoryInput) {
        this.directoryInput = directoryInput;
    }

    public JarInput getJarInput() {
        return jarInput;
    }

    public void setJarInput(JarInput jarInput) {
        this.jarInput = jarInput;
    }

    public TransformInvocation getTransformInvocation() {
        return transformInvocation;
    }

    public void setTransformInvocation(TransformInvocation transformInvocation) {
        this.transformInvocation = transformInvocation;
    }
}
