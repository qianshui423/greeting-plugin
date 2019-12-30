package com.jumao.greeting.test;

import org.objectweb.asm.*;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
    public static final String path = "/Users/liuxuehao/Documents/dasouche/ThirdLibrary/greeting-plugin/build/classes/java/main/com/jumao/greeting/test/Collector.class";
    public static final String path2 = "/Users/liuxuehao/Documents/dasouche/ThirdLibrary/greeting-plugin/build/classes/java/main/com/jumao/greeting/test/Collector2.class";
    public static final String pathTrace = "/Users/liuxuehao/Documents/dasouche/ThirdLibrary/greeting-plugin/build/classes/java/main/com/jumao/greeting/test/CollectorTrace.txt";

    private static MyClassloader myClassloader = new MyClassloader();

    public static void main(String[] args) throws IOException {
//        try {
//            myClassloader.loadClass("Hello");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }

        ClassWriter cw = new ClassWriter(0);
        PrintWriter printWriter = new PrintWriter(new FileOutputStream(pathTrace));
        TraceClassVisitor cv = new TraceClassVisitor(cw, printWriter);
        cv.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC,
                "com/jumao/greeting/test/Collector", null, Type.getType(Object.class).getInternalName(), new String[]{"com/jumao/greeting/test/Measurable"});
        cv.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "LESS", Type.INT_TYPE.getDescriptor(), null, -1).visitEnd();
        MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "print", "(Ljava/lang/String;)I", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(12, l0);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitInsn(Opcodes.IRETURN);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", "Lcom/jumao/greeting/test/Collector;", null, l0, l1, 0);
        mv.visitLocalVariable("input", "Ljava/lang/String;", null, l0, l1, 1);
        mv.visitMaxs(1, 2);
        mv.visitEnd();
        cv.visitEnd();
        byte[] b = cw.toByteArray();
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(b);

        FileInputStream fis = new FileInputStream(path);
        ClassReader cr = new ClassReader(fis);
        ClassWriter cw2 = new ClassWriter(cr, 0);
        ClassPrinter cp = new ClassPrinter(cw2);
        cr.accept(cp, 0);
        byte[] b2 = cw2.toByteArray();
        FileOutputStream fos2 = new FileOutputStream(path2);
        fos2.write(b2);
        fos2.flush();

        AgentLoader.load();

        new Thread(PrintLog::print).start();
    }
}
