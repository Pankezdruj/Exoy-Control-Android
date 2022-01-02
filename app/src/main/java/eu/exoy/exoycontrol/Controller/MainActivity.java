package eu.exoy.exoycontrol.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import eu.exoy.exoycontrol.Model.Communication;
import eu.exoy.exoycontrol.Model.Device;
import eu.exoy.exoycontrol.Model.DeviceMenuListItem;
import eu.exoy.exoycontrol.R;

public class MainActivity extends AppCompatActivity {

    Communication com = new Communication(this);
    List<DeviceMenuListItem> devices = new ArrayList<>();
    ArrayAdapter<DeviceMenuListItem> arrayAdapter;
    List<Device> devicesData = new ArrayList<>();

    ListView deviceLV;
    Bitmap iconHypercube;
    Bitmap iconDodecahedron;
    Bitmap iconMirror;
    Button refreshBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        deviceLV = findViewById(R.id.devicesLV);
        refreshBtn = findViewById(R.id.refreshBtn);

        com.search();

        iconHypercube = BitmapFactory.decodeResource(getResources(), R.drawable.hypercube);
        iconDodecahedron = BitmapFactory.decodeResource(getResources(), R.drawable.dodecahedron);
        iconMirror = BitmapFactory.decodeResource(getResources(), R.drawable.mirror);

        arrayAdapter =
                new ArrayAdapter<DeviceMenuListItem>(this, 0, devices) {
                    @SuppressLint("InflateParams")
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if(convertView == null) {
                            convertView = getLayoutInflater()
                                    .inflate(R.layout.customlistadapter, null, false);
                        }

                        DeviceMenuListItem currentMenuItem = devices.get(position);

                        ImageView img = (ImageView)convertView.findViewById(R.id.deviceIV);

                        TextView Tname = (TextView)convertView.findViewById(R.id.deviceNameTV);
                        TextView Tid = (TextView)convertView.findViewById(R.id.deviceCodeTV);
                        Button Tbtn = convertView.findViewById(R.id.deviceConnectBtn);

                        Tbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                com.confirmConnection(devices.get(position).IP3);
                                Intent intent = new Intent(MainActivity.this, DeviceControlActivity.class);
                                intent.putExtra("IP3", devices.get(position).IP3);
                                startActivity(intent);
                            }
                        });

                        img.setImageBitmap(currentMenuItem.image);

                        Tname.setText(currentMenuItem.name);
                        Tid.setText(currentMenuItem.code);

                        return convertView;
                    }
                };

        deviceLV.setAdapter(arrayAdapter);

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.search();
            }
        });
    }

    public void addNewDevice(Device device) {
        arrayAdapter.notifyDataSetChanged();
        devicesData.add(device);
        Bitmap img = null;
        String name = "";
        String code = "";
        switch (device.getType()) {
            case 0:
                img = iconHypercube;
                name = "Exoy Test";
                code = "Test";
                break;
            case 1:
                img = iconHypercube;
                name = "Exoy Hypercube";
                code = "Hypercube";
                break;
            case 2:
                img = iconHypercube;
                name = "Exoy Ultra Dense Hypercube";
                code = "Hypercube";
                break;
            case 3:
                img = iconDodecahedron;
                name = "Exoy Dodecahedron";
                code = "Dodecahedron";
                break;
            case 4:
                img = iconDodecahedron;
                name = "Exoy Ultra Dense Dodecahedron";
                code = "Dodecahedron";
                break;
            case 5:
                img = iconMirror;
                name = "Exoy Mirror";
                code = "Mirror";
                break;
            case 6:
                img = iconMirror;
                name = "Exoy Ultra Dense Mirror";
                code = "Mirror";
                break;
            case 7:
                img = iconHypercube;
                name = "Exoy Icosahedron";
                code = "Hypercube";
                break;
            case 8:
                img = iconHypercube;
                name = "Exoy Ultra Dense Icosahedron";
                code = "Hypercube";
                break;
            case 9:
                img = iconHypercube;
                name = "Exoy Tetrahedron";
                code = "Hypercube";
                break;
            case 10:
                img = iconHypercube;
                name = "Exoy Ultra Dense Tetrahedron";
                code = "Hypercube";
                break;
            case 11:
                img = iconHypercube;
                name = "Exoy Hexagon";
                code = "Hypercube";
                break;
            case 12:
                img = iconHypercube;
                name = "Exoy Ultra Dense Hexagon";
                code = "Hypercube";
                break;
            case 13:
                img = iconHypercube;
                name = "Exoy Sound Visualiser";
                code = "Hypercube";
                break;
            case 14:
                img = iconHypercube;
                name = "Exoy Ultra Dense Sound Visualiser";
                code = "Hypercube";
                break;
        }
        name = name + " " + device.getSize() + " inch";
        code = code + " #" + device.getID();
        devices.add(new DeviceMenuListItem(img, name, code, device.getIP3()));
    }


}