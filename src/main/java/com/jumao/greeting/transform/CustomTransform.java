package com.jumao.greeting.transform;

import com.android.build.api.transform.*;
import com.android.build.gradle.AppExtension;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.ide.common.workers.ExecutorServiceAdapter;
import com.android.tools.r8.com.google.common.collect.ImmutableList;
import com.android.tools.r8.com.google.common.collect.Lists;
import com.jumao.greeting.transform.model.InputModel;
import com.jumao.greeting.transform.task.DirectoryInputHandleTask;
import com.jumao.greeting.transform.task.JarInputHandleTask;

import org.gradle.api.Project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

public class CustomTransform extends Transform {

    private Project mProject;
    private AppExtension mAppExtension;

    private final File mAssetsFile;
    private final File mTestFile;
    private final ImmutableList<File> mSecondaryDirectoryOutputs;
    private final ImmutableList<File> mSecondaryFileOutputs;
    private ExecutorServiceAdapter mServiceAdapter = new ExecutorServiceAdapter(Executors.newFixedThreadPool(20));

    public CustomTransform(Project project, AppExtension appExtension) {
        this.mProject = project;
        this.mAppExtension = appExtension;
        mAssetsFile = mProject.file(new File("src/main/assets"));
        mTestFile = mProject.file(new File("src/main/assets/test.txt"));
        mSecondaryDirectoryOutputs = ImmutableList.of(mAssetsFile);
        mSecondaryFileOutputs = ImmutableList.of(mTestFile);

//        Task task = project.getTasks().getByName("mergeDebugAssets");
//        Task newTask = project.getTasks().create("mergeDebugAssetsBefore", new Action<Task>() {
//            @Override
//            public void execute(Task task) {
//                System.out.println("run before mergeDebugAssets: ");
//            }
//        });
//        project.getTasks().add(newTask);
//        task.dependsOn(newTask);
    }

    @Override
    public String getName() {
        return "customTransform";
    }

    private void writeAssets() {
        try {
            FileOutputStream fos = new FileOutputStream(mTestFile);
            fos.write("Hello Test".getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        writeAssets();

        boolean isIncremental = transformInvocation.isIncremental();
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        if (!isIncremental) {
            outputProvider.deleteAll();
        }
        for (TransformInput input : inputs) {
            for (JarInput jarInput : input.getJarInputs()) {
                mServiceAdapter.submit(JarInputHandleTask.class, new InputModel(jarInput, transformInvocation));
            }
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                mServiceAdapter.submit(DirectoryInputHandleTask.class, new InputModel(directoryInput, transformInvocation));
            }
        }
        mServiceAdapter.close();
    }

    @Override
    public Collection<SecondaryFile> getSecondaryFiles() {
        final List<SecondaryFile> files = Lists.newArrayList();
        if (mTestFile != null && mTestFile.isFile()) {
            files.add(SecondaryFile.nonIncremental(mProject.files(mTestFile)));
        }
        return files;
    }

    @Override
    public Collection<File> getSecondaryDirectoryOutputs() {
        return mSecondaryDirectoryOutputs;
    }

    @Override
    public Collection<File> getSecondaryFileOutputs() {
        return mSecondaryFileOutputs;
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return true;
    }
}
