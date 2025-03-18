@echo off
echo Select a scheduling algorithm:
echo 1. RoundRobin
echo 2. ACO (Ant Colony Optimization)
echo 3. FCFS (First-Come-First-Serve)
set /p algorithm="Enter algorithm number (1/2/3): "

echo Running Java simulation...
java -cp "C:\Users\tania\Desktop\licenta\cloudsim-7.0\cloudsim-7.0\modules\cloudsim-examples\src\main\java\org\cloudbus\cloudsim\examples\energy_licenta\simulator\EnergySimulator.java" org.cloudbus.cloudsim.examples.energy_licenta.simulator.EnergySimulator
java -cp . org.cloudbus.cloudsim.examples.energy_licenta.simulator.EnergySimulator %algorithm%

echo Java simulation finished!

echo Running Python visualization...
python"C:\Users\tania\Desktop\licenta\cloudsim-7.0\ShowSimulation\.venv\Scripts\python.exe" "C:\Users\tania\Desktop\licenta\cloudsim-7.0\ShowSimulation\animation.py"

echo Python visualization finished!

pause
