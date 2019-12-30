package com.jumao.greeting.test;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassPrinter extends ClassVisitor {

    public ClassPrinter(ClassWriter cw) {
        super(Opcodes.ASM7, cw);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        System.out.println(name + " extends " + superName + " {");
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        System.out.println(" " + descriptor + " " + name);
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        System.out.println(" " + name + descriptor);
        //return super.visitMethod(access, name, descriptor, signature, exceptions);
        return null;
    }

    @Override
    public void visitEnd() {
        System.out.println("}");
        FieldVisitor fv = cv.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "GREAT", "I", null, 1);
        if (fv != null) {
            fv.visitEnd();
        }
        super.visitEnd();
    }
}
