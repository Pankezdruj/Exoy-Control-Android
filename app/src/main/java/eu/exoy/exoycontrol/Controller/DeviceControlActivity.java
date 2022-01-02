package eu.exoy.exoycontrol.Controller;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.listeners.ColorListener;

import eu.exoy.exoycontrol.Model.Communication;
import eu.exoy.exoycontrol.Model.Device;
import eu.exoy.exoycontrol.R;

public class DeviceControlActivity extends AppCompatActivity {

    Button powerBtn;
    Button effectBtn;
    SeekBar speedSB;
    SeekBar brightnessSB;
    SeekBar BGbrightnessSB;
    Button silenceCalibrationBtn;
    Switch effectChangeSwitch;
    Button restartModeBtn;
    ColorPickerView colorPickerView;

    Dialog dialogEff;

    int IP3;
    Device device;
    Communication com;

    final String[] effects = {"Color", "Color Shift", "Rainbow", "Stroboscope", "Colorful Stroboscope", "Fire", "Volume", "Breathing", "Lines", "Rainbow reaction", "Color reaction", "Life", "Stars Reaction", "Stars"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_control_activity);

        powerBtn = findViewById(R.id.powerBtn);
        effectBtn = findViewById(R.id.effectBtn);
        speedSB = findViewById(R.id.speedSB);
        brightnessSB = findViewById(R.id.brightnessSB);
        BGbrightnessSB = findViewById(R.id.BGbrightnessSB);
        silenceCalibrationBtn = findViewById(R.id.silenceCalibrationBtn);
        effectChangeSwitch = findViewById(R.id.effectChangeS);
        restartModeBtn = findViewById(R.id.restartModeBtn);
        colorPickerView = findViewById(R.id.colorPickerView);

        IP3 = getIntent().getIntExtra("IP3",0);
        device = new Device(IP3);
        com = new Communication(device, this);

        powerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.togglePower();
            }
        });
        speedSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) com.setSpeed(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                com.getSettings();
            }
        });
        brightnessSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) com.setBrightness(i+100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                com.getSettings();
            }
        });
        BGbrightnessSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) com.setBGBrightness(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                com.getSettings();
            }
        });
        silenceCalibrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.calibrate();
            }
        });
        effectChangeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                com.setAutoEffectChange(b);
            }
        });
        colorPickerView.setColorListener(new ColorEnvelopeListener() {
            @Override
            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                float[] hsv = rgbToHsv(envelope.getArgb()[1], envelope.getArgb()[2], envelope.getArgb()[3]);
                if (fromUser) com.setColor(toThreeDigits(Math.round(map(hsv[0], 0, 360, 0, 255))), toThreeDigits(Math.round(map(hsv[1],0,100,0,255))));
            }
        });

        dialogEff = new Dialog(this);
        dialogEff.setContentView(R.layout.dialog_effects);
        ListView listView = dialogEff.findViewById(R.id.listEffects);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 0, effects) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null) {
                    convertView = getLayoutInflater()
                            .inflate(R.layout.effects_customlistadapter, null, false);
                }
                TextView Tname = convertView.findViewById(R.id.effectName);
                Tname.setText(effects[position]);

                return convertView;
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                com.setEffect(position);
                dialogEff.dismiss();
            }
        });
        dialogEff.setTitle("Effects");

        effectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEff.show();
            }
        });

        restartModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.toggleEspModes();
            }
        });
    }

    private float map(float x, int in_min, int in_max, int out_min, int out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    private static float[] rgbToHsv(float r, float g, float b) {
        r = (float) (r / 255.0);
        g = (float) (g / 255.0);
        b = (float) (b / 255.0);

        float cmax = Math.max(r, Math.max(g, b));
        float cmin = Math.min(r, Math.min(g, b));
        float diff = cmax - cmin;
        float h = -1, s = -1;
        if (cmax == cmin)
            h = 0;
        else if (cmax == r)
            h = (60 * ((g - b) / diff) + 360) % 360;
        else if (cmax == g)
            h = (60 * ((b - r) / diff) + 120) % 360;
        else if (cmax == b)
            h = (60 * ((r - g) / diff) + 240) % 360;
        if (cmax == 0)
            s = 0;
        else
            s = (diff / cmax) * 100;
        double v = cmax * 100;
        return new float[]{h, s, (float)v};
    }

    private String toThreeDigits(int num) {
        return (num < 10 ? "00" + num : (num < 100 ? "0" + num : "" + num));
    }

    public void updateInterface() {
        speedSB.setProgress(device.getSpeed());
        brightnessSB.setProgress(device.getBrightness()-100);
        BGbrightnessSB.setProgress(device.getBGBrightness());
        effectChangeSwitch.setChecked(device.isEffectChange());
    }

}
