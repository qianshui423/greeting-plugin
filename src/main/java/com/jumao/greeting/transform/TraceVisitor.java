package com.jumao.greeting.transform;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static com.jumao.greeting.transform.AsmHelper.ASM_VERSION;

public class TraceVisitor extends ClassVisitor {

    private String className;

    public TraceVisitor(ClassWriter cw) {
        super(ASM_VERSION, cw);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        System.out.println("-----class version: " + version + " -----class name: " + name);
        className = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
        if (!AsmHelper.isSystemSystemClass(className)) {
            if (name.equals("onCreate") && "(Landroid/os/Bundle;)V".equals(descriptor))
                mv = new LogAdapter(mv, access, name, descriptor);
            else if (name.equals("onClick"))
                mv = new OnClickListenerAdapter(mv, access, name, descriptor, className);
            else if (name.equals("onLongClick")) {
                mv = new OnLongClickListenerAdapter(mv, access, name, descriptor, className);
            }
        }
        return mv;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return super.visitAnnotation(descriptor, visible);
    }
}
