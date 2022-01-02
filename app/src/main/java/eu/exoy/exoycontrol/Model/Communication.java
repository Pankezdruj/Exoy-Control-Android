package eu.exoy.exoycontrol.Model;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import eu.exoy.exoycontrol.Controller.DeviceControlActivity;
import eu.exoy.exoycontrol.Controller.MainActivity;

public class Communication {
    private String IP = "192.168.";
    private final int PORT = 8888;
    private volatile String stringMessage = "";
    private int currentDeviceIP3 = 0;
    private Device device;
    List<Integer> deviceIPs = new ArrayList();

    MainActivity main = null;
    DeviceControlActivity control = null;

    public Communication(MainActivity main) {
        IP = IP + getIPAddress(true) + ".";
        this.main = main;
    }

    public Communication(Device device, DeviceControlActivity control) {
        IP = IP + getIPAddress(true) + ".";
        currentDeviceIP3 = device.getIP3();
        sendMessage(new byte[]{'G','T',0},currentDeviceIP3,"control");
        this.device = device;
        this.control = control;
    }

    public String getIP() {
        return IP;
    }

    public void togglePower() {
        System.out.println(device.isOn());
        if (device.isOn()) sendMessage(new byte[]{'G','T',1,0,0}, currentDeviceIP3, "control");
        else sendMessage(new byte[]{'G','T',1,0,1}, currentDeviceIP3, "control");
    }

    public void confirmConnection(int IP3) {
        sendMessage(new byte[]{'G','T',0,1},IP3,"control");
    }

    public void setSpeed(int amount) {
        sendMessage(new byte[]{'G','T',2,2, (byte) amount},currentDeviceIP3,"control");
    }
    public void setBrightness(int amount) {
        sendMessage(new byte[]{'G','T',1,1,(byte) amount},currentDeviceIP3,"control");
    }
    public void setBGBrightness(int amount) {
        sendMessage(new byte[]{'G','T',2,1,(byte) amount},currentDeviceIP3,"control");
    }

    public void calibrate() {
        sendMessage(new byte[]{'G','T',3} ,currentDeviceIP3,"control");
    }

    public void setAutoEffectChange(boolean state) {
        sendMessage(new byte[]{'G','T', 1, 2, (byte) (state ? 1 : 0)} ,currentDeviceIP3,"control");
    }

    public void setColor(String hue, String saturation) {
        System.out.println(hue + " " + saturation);
        sendMessage(new byte[]{'G','T', 2, 3, (byte) Integer.parseInt(hue)}, currentDeviceIP3, "control");
        sendMessage(new byte[]{'G','T', 2, 4, (byte) Integer.parseInt(saturation)}, currentDeviceIP3, "control");
    }

    public void setEffect(int num) {
        sendMessage(new byte[]{'G','T', 2, 0, (byte) num}, currentDeviceIP3, "control-update");
    }

    public void toggleEspModes() {
        sendMessage(new byte[]{'G','T',4}, currentDeviceIP3, "control");
    }

    public void getSettings() {
        sendMessage(new byte[]{'G','T', 1}, currentDeviceIP3, "control-update");
    }

    public void search() {
        for (int i = 0; i < 255; i++) {
            byte[] msg = {'G','T',1};
            sendMessage(msg, i, "search");
            System.out.println("sent to"+i);
        }

    }

    private void sendMessage(final byte[] message, final int IP3, final String method) {

        final Communication cont = this;
        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {

            String stringData;

            @Override
            public void run() {

                DatagramSocket ds = null;
                try {
                    ds = new DatagramSocket();
                    InetAddress serverAddr = InetAddress.getByName(IP+IP3);
                    DatagramPacket dp;
                    dp = new DatagramPacket(message, message.length, serverAddr, PORT);
                    ds.send(dp);
                    byte[] lMsg = new byte[1000];
                    dp = new DatagramPacket(lMsg, lMsg.length);
                    ds.receive(dp);
                    stringData = new String(lMsg, 0, dp.getLength());

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (ds != null) {
                        ds.close();
                    }
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("RESPONSE: " + stringData);
                        if (stringData != null && stringData.trim().length() != 0 && !deviceIPs.contains(IP3)) {
                            if (method.equals("search")) {
                                deviceIPs.add(IP3);
                                main.addNewDevice(new Device(stringData, IP3));
                            } else if (method.equals("control")) {
                                device.parseMessage(stringData, IP3);
                            } else if (method.equals("control-update")) {
                                device.parseMessage(stringData, IP3);
                                control.updateInterface();
                            }
                        }
                    }
                });
            }
        });

        thread.start();
    }
    private static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        if (sAddr.indexOf(':')<0) return sAddr.split("\\.")[2];
                    }
                }
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }
}
