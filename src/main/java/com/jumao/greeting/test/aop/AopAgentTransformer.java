package com.jumao.greeting.test.aop;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class AopAgentTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader classLoader, String s, Class<?> aClass, ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {
        ClassNode cn = new ClassNode(Opcodes.ASM7);
        ClassReader cr = new ClassReader(bytes);
        cr.accept(cn, 0);
        // convert
        logMethod(cn);
        ClassWriter cw = new ClassWriter(cr, 0);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private void logMethod(ClassNode classNode) {
        for (MethodNode methodNode : classNode.methods) {
            if ("com/jumao/greeting/test/PrintLog".equals(classNode.name) && "print".equals(methodNode.name))
                System.out.println("class name: " + classNode.name + ". method name: " + methodNode.name);
        }
    }
}
