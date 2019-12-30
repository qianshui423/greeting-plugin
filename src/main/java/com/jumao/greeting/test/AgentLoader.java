package com.jumao.greeting.test;

import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;
import java.lang.management.ManagementFactory;

public class AgentLoader {

    public static void load() {
        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        int p = nameOfRunningVM.indexOf('@');
        String pid = nameOfRunningVM.substring(0, p);

        String jarFilePath = "/Users/liuxuehao/Documents/dasouche/ThirdLibrary/greeting-plugin/src/main/java/target/classes/aopAgent.jar";
        VirtualMachine vm = null;
        try {
            vm = VirtualMachine.attach(pid);
            vm.loadAgent(jarFilePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (vm != null) vm.detach();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
