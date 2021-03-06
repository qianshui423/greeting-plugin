package com.jumao.greeting.transform;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

import static com.jumao.greeting.transform.AsmHelper.ASM_VERSION;

public class LogAdapter extends AdviceAdapter {

    public LogAdapter(MethodVisitor mv, int access, String name, String desc) {
        super(ASM_VERSION, mv, access, name, desc);
    }

    @Override
    protected void onMethodEnter() {
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("Hello ASM onMethodEnter!");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    @Override
    protected void onMethodExit(int opcode) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn("Hello Toast ASM!");
        mv.visitInsn(ICONST_1);
        mv.visitMethodInsn(INVOKESTATIC, "android/widget/Toast", "makeText", "(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "android/widget/Toast", "show", "()V", false);
    }
}

