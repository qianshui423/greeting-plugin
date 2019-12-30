package com.jumao.greeting;

import com.android.build.gradle.AppExtension;
import com.jumao.greeting.transform.CustomTransform;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class BytecodePlugin implements Plugin<Project> {

    @Override
    public void apply(@NotNull Project project) {
        AppExtension appExtension = (AppExtension) project.getProperties().get("android");
        appExtension.registerTransform(new CustomTransform(project, appExtension), Collections.EMPTY_LIST);

        project.getConfigurations().all(new Action<Configuration>() {
            @Override
            public void execute(Configuration files) {
                String name = files.getName();
                System.out.println("this configuration is: " + name);
                if (!"implementation".equals(name) && !"compile".equals(name)) {
                    return;
                }
                files.getDependencies().add(project.getDependencies().create("com.tencent:mmkv:1.0.23"));
            }
        });
    }
}
