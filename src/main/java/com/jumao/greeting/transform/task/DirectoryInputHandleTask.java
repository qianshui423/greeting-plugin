package com.jumao.greeting.transform.task;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.Status;
import com.android.build.api.transform.TransformInvocation;
import com.jumao.greeting.transform.AsmHelper;
import com.jumao.greeting.transform.model.InputModel;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class DirectoryInputHandleTask implements Runnable {

    private final DirectoryInput mDirectoryInput;
    private final TransformInvocation mTransformInvocation;

    public DirectoryInputHandleTask(InputModel model) {
        this.mDirectoryInput = model.getDirectoryInput();
        this.mTransformInvocation = model.getTransformInvocation();
    }

    @Override
    public void run() {
        try {
            File dest = mTransformInvocation.getOutputProvider().getContentLocation(
                    mDirectoryInput.getName(),
                    mDirectoryInput.getContentTypes(),
                    mDirectoryInput.getScopes(),
                    Format.DIRECTORY);
            FileUtils.forceMkdir(dest);
            if (mTransformInvocation.isIncremental()) {
                String srcDirPath = mDirectoryInput.getFile().getAbsolutePath();
                String destDirPath = dest.getAbsolutePath();
                Map<File, Status> fileStatusMap = mDirectoryInput.getChangedFiles();
                for (Map.Entry<File, Status> changeFile : fileStatusMap.entrySet()) {
                    Status status = changeFile.getValue();
                    File inputFile = changeFile.getKey();
                    String destFilePath = inputFile.getAbsolutePath().replace(srcDirPath, destDirPath);
                    File destFile = new File(destFilePath);
                    switch (status) {
                        case NOTCHANGED: {
                            break;
                        }
                        case REMOVED: {
                            if (destFile.exists()) {
                                FileUtils.forceDelete(destFile);
                            }
                            break;
                        }
                        case ADDED:
                        case CHANGED: {
                            AsmHelper.transform(inputFile, destFile);
                            break;
                        }
                    }
                }
            } else {
                copyDirectory(mDirectoryInput.getFile(), dest);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyDirectory(File dir, File dest) throws IOException {
        if (!dir.exists())
            throw new IllegalArgumentException("Directory：" + dir + "not exist ");
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException(dir + "is's a Directory");
        }
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                File destFile = new File(dest, file.getName());
                if (file.isDirectory()) {
                    copyDirectory(file, destFile);
                } else {
                    AsmHelper.transform(file, destFile);
                }
            }
        }
    }
}
