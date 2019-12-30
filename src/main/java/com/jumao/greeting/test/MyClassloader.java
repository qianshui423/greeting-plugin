package com.jumao.greeting.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MyClassloader extends ClassLoader {

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            String cname = "/Users/liuxuehao/Documents/dasouche/ThirdLibrary/greeting-plugin/pathClasses/" + name.replace('.', '/') + ".class";
            byte[] classBytes = Files.readAllBytes(Paths.get(cname));
            Class<?> cl = defineClass(name, classBytes, 0, classBytes.length);
            if (cl == null) {
                throw new ClassNotFoundException(name);
            }
            return cl;
        } catch (IOException e) {
            System.out.print(e.getMessage());
            throw new ClassNotFoundException(name);
        }
    }
}
