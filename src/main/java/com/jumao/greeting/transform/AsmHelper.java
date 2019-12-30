package com.jumao.greeting.transform;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class AsmHelper {

    public static final int ASM_VERSION = Opcodes.ASM7;

    public static void transformClass(File input, File output) {
        try {
            System.out.println(input.getAbsolutePath());
            if (input.getAbsolutePath().endsWith(".jar")) {
                FileUtils.copyFile(input, output);
                return;
            }

            FileInputStream fis = new FileInputStream(input.getAbsoluteFile());
            ClassReader cr = new ClassReader(fis);
            ClassWriter cw = new ClassWriter(cr, 0);
            TraceVisitor tv = new TraceVisitor(cw);
            //TraceClassVisitor tcv = new TraceClassVisitor(tv, new ASMifier(), new PrintWriter(new FileOutputStream(output + ".ASM")));
            ClassNode cn = new ClassNode();
            cr.accept(tv, ClassReader.EXPAND_FRAMES);
            cr.accept(cn, ClassReader.EXPAND_FRAMES);
            FileOutputStream fos = new FileOutputStream(output);
            fos.write(cw.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
