package eu.exoy.exoycontrol.Model;

import android.graphics.Bitmap;

public class DeviceMenuListItem {
    public Bitmap image;
    public String name;
    public String code;
    public int IP3;

    public DeviceMenuListItem(Bitmap image, String name, String code, int IP3) {
        this.image = image;
        this.name = name;
        this.code = code;
        this.IP3 = IP3;
    }
}
