package com.jumao.greeting.transform;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

public class AsmHelper {

    public static final int ASM_VERSION = Opcodes.ASM7;
    private static final String SYSTEM_CLASS_NAME_PREFIX[] = new String[]{"android/support", "androidx"}; // 系统类
    private static final String CLASS_BYTE_CODE_HEADER_HEX_STRING = "CAFEBABE"; // 字节码.class文件头信息
    private static final String ZIP_HEADER_HEX_STRING = "504B0304"; // Zip文件头信息

    private static boolean isJarFile(File input) {
        try {
            return isJarFile(new FileInputStream(input));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isJarFile(InputStream is) {
        return ZIP_HEADER_HEX_STRING.equals(readHeaderU4HexString(is));
    }

    private static boolean isClassFile(File input) {
        try {
            return isClassFile(new FileInputStream(input));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isClassFile(InputStream is) {
        return CLASS_BYTE_CODE_HEADER_HEX_STRING.equals(readHeaderU4HexString(is));
    }

    private static String readHeaderU4HexString(InputStream is) {
        byte[] headerU4 = new byte[4];
        try {
            is.read(headerU4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < headerU4.length; i++) {
            String hexString = Integer.toHexString(headerU4[i] & 0xFF).toUpperCase();
            if (hexString.length() < 2) {
                builder.append(0);
            }
            builder.append(hexString);
        }
        return builder.toString();
    }

    public static boolean isSystemSystemClass(String className) {
        for (int i = 0; i < SYSTEM_CLASS_NAME_PREFIX.length; i++) {
            if (className == null || className.equals("")) return false;
            if (className.startsWith(SYSTEM_CLASS_NAME_PREFIX[i])) {
                return true;
            }
        }
        return false;
    }

    private static void directCopyToTarget(File input, File output) {
        try {
            if (!output.exists()) {
                FileUtils.touch(output);
            }
            FileUtils.copyFile(input, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void transform(File input, File output) {
        System.out.println(input.getAbsolutePath());
        if (isJarFile(input)) {
            transformJar(input, output);
        } else if (isClassFile(input)) {
            transformClass(input, output);
        } else {
            directCopyToTarget(input, output);
        }
    }

    private static void transformJar(File input, File output) {
        try {
            JarFile jarFile = new JarFile(input);
            Enumeration<JarEntry> enumeration = jarFile.entries();
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(output));
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = enumeration.nextElement();
                String entryName = jarEntry.getName();
                ZipEntry zipEntry = new ZipEntry(entryName);
                InputStream inputStream = jarFile.getInputStream(jarEntry);
                jarOutputStream.putNextEntry(zipEntry);
                if (isClassFile(input)) {
                    jarOutputStream.write(transformClass(inputStream));
                } else {
                    jarOutputStream.write(IOUtils.toByteArray(inputStream));
                }
                inputStream.close();
                jarOutputStream.closeEntry();
            }
            jarOutputStream.close();
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void transformClass(File input, File output) {
        FileOutputStream fos = null;
        try {
            if (!output.exists()) {
                FileUtils.touch(output);
            }
            byte[] byteArray = transformClass(new FileInputStream(input));
            fos = new FileOutputStream(output);
            fos.write(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static byte[] transformClass(InputStream is) throws IOException {
        ClassReader cr = new ClassReader(is);
        ClassWriter cw = new ClassWriter(cr, 0);
        TraceVisitor tv = new TraceVisitor(cw);
        //TraceClassVisitor tcv = new TraceClassVisitor(tv, new ASMifier(), new PrintWriter(new FileOutputStream(output + ".ASM")));
        ClassNode cn = new ClassNode();
        cr.accept(tv, ClassReader.EXPAND_FRAMES);
        cr.accept(cn, ClassReader.EXPAND_FRAMES);
        return cw.toByteArray();
    }
}
