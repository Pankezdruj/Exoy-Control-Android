package eu.exoy.exoycontrol.Model;

public class Device {
        private int ID;
        private int type; // 1,2 - cube, 3,4 - dodecahedron, 5,6 - mirror
        private String size;
        private int currentMode;
        private int brightness;
        private int BGBrightness;
        private int speed;
        private int hue;
        private int saturation;
        private boolean effectChange;
        private boolean on;
        private int IP3;
        private boolean espAPmode;

        public Device(String message, int IP3){
            parseMessage(message, IP3);
        }

        public Device(int IP3) {
            this.IP3 = IP3;
        }

    public int getID() {
        return ID;
    }

    public int getType() {
        return type;
    }

    public String getSize() {
        return size;
    }

    public int getCurrentMode() {
        return currentMode;
    }

    public int getBrightness() {
        return brightness;
    }

    public int getBGBrightness() {
        return BGBrightness;
    }

    public int getSpeed() {
        return speed;
    }

    public int getHue() {
        return hue;
    }

    public int getSaturation() {
        return saturation;
    }

    public boolean isEffectChange() {
        return effectChange;
    }

    public boolean isOn() {
        return on;
    }

    public int getIP3() {
            return IP3;
    }

    public void parseMessage(String message, int IP3) {
            String[] a = message.split(" ");
            System.out.println(a[1]);
            ID = Integer.parseInt(a[1]);
            type = Integer.parseInt(a[2]);
            size = a[3];
            currentMode = Integer.parseInt(a[4]);
            brightness = Integer.parseInt(a[5]);
            BGBrightness = Integer.parseInt(a[6]);
            speed = Integer.parseInt(a[7]);
            hue = Integer.parseInt(a[8]);
            saturation = Integer.parseInt(a[9]);
            effectChange = a[10].equals("1");
            on = a[11].equals("1");
            this.IP3 = IP3;
        }
}
