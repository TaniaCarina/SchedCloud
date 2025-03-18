package org.cloudbus.cloudsim.examples.energy_licenta;

import java.io.File;
import java.io.IOException;

public class RunPythonAfterJava {
    public static void main(String[] args) {
        System.out.println("Running Java Simulation...");

        // Aici rulezi simularea
        org.cloudbus.cloudsim.examples.energy_licenta.simulator.EnergySimulatorDynamic.main(new String[]{});

        System.out.println("Java simulation finished. Running Python...");

        try {
            // Calea absolută catre Python
            String pythonPath = "C:\\Users\\tania\\AppData\\Local\\Programs\\Python\\Python311\\python.exe";  // Modifică dacă ai altă versiune

            // Calea absolută catre scriptul Python
            String scriptPath = "C:\\cloudsim-7.0\\ShowSimulation\\animation.py";

            ProcessBuilder pb = new ProcessBuilder(pythonPath, scriptPath);
            pb.directory(new File("C:\\Users\\tania\\Desktop\\licenta\\cloudsim-7.0\\ShowSimulation\\animation.py")); // Setează directorul de lucru
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Python visualization finished!");
    }
}
