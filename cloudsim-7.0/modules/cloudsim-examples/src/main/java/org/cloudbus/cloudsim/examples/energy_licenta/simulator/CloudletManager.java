package org.cloudbus.cloudsim.examples.energy_licenta.simulator;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModelFull;

import java.util.ArrayList;
import java.util.List;

public class CloudletManager {
    public static List<Cloudlet> createCloudlets(int brokerId, int numCloudlets) {
        List<Cloudlet> cloudletList = new ArrayList<>();
        for (int i = 0; i < numCloudlets; i++) {
            int length = (int) (Math.random() * 5000) + 1000;
            int pesNumber = 1;
            long fileSize = 300;
            long outputSize = 300;
            Cloudlet cloudlet = new Cloudlet(i, length, pesNumber, fileSize, outputSize, new UtilizationModelFull(), new UtilizationModelFull(), new UtilizationModelFull());
            cloudlet.setUserId(brokerId);
            cloudletList.add(cloudlet);
        }
        return cloudletList;
    }
}
