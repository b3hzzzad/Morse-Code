package th.o.th.morsecode;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private final String[] ALPHA = {"a", "b", "c", "d", "e", "f", "g", "h", "i",
            "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
            "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".",
            ",", "?", "'", "!", "/", "(", ")", "&", ":", ";", "=", "+", "-", "_",
            "\"", "$", "@", " "};
    private final String[] MORSE = {"·−/", "−···/", "−·−·/", "−··/", "·/", "··−·/", "−−·/",
            "····/", "··/", "·−−−/", "−·−/", "·−··/", "−−/", "−·/", "−−−/", "·−−·/", "−−·−/",
            "·−·/", "···/", "−/", "··−/", "···−/", "·−−/", "−··−/", "−·−−/", "−−··/", "−−−−−/",
            "·−−−−/", "··−−−/", "···−−/", "····−/", "·····/", "−····/", "−−···/", "−−−··/",
            "−−−−·/", "·−·−·−/", "−−··−−/", "··−−··/", "·−−−−·/", "−·−·−−/", "−··−·/", "−·−−·/",
            "−·−−·−/", "·−···/", "−−−···/", "−·−·−·/", "−···−/", "·−·−·/", "−····−/", "··−−·−/",
            "·−··−·/", "···−··−/", "·−−·−·/", "   "};

    private final HashMap<String, String> hashmap = new HashMap<>();

    public Button button_playSound, button_translate, button_vibration, button_flash, button_stop;
    Vibrator vibrator;
    EditText editText;
    AudioTrack tone;
    TextView textView;
    CameraManager cameraManager;
    String getCameraID;
    volatile boolean mBoolean = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //initialize camera manager
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            getCameraID = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        //create hash map
        for (int i = 0; i < ALPHA.length; i++) {
            hashmap.put(ALPHA[i], MORSE[i]);
        }

        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.editText);

        //translate button
        button_translate = findViewById(R.id.buttonTranslate);
        button_translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String morseString = null;
                StringBuilder stringBuilder_morse = new StringBuilder();

                char[] getEditText = editText.getText().toString().trim().toCharArray();

                for (int i = 0; i < getEditText.length; i++) {

                    char c = getEditText[i];
                    morseString = hashmap.get(Character.toString(c).toLowerCase());
                    stringBuilder_morse.append(morseString);

                    for (int ii = 0; ii < morseString.length(); ii++) {

                        //translated morse set to textview
                        textView.setText(stringBuilder_morse);


                    }
                }
            }
        });

        //play sound button
        button_playSound = findViewById(R.id.buttonPlaysound);
        button_playSound.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                mBoolean = true;
                new playSound().start();

            }
        });

        //vibration button
        button_vibration = findViewById(R.id.buttonVibration);
        button_vibration.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                mBoolean = true;
                new vibrationClass().start();

            }
        });

        //flash light button
        button_flash = findViewById(R.id.flashButton);
        button_flash.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                mBoolean = true;
                new flashClass().start();

            }
        });

        button_stop = findViewById(R.id.stopButton);
        button_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBoolean = false;
            }
        });

    }

    private AudioTrack generateTone(double freqHz, int durationMs) {

        int count = (int) (44100.0 * 2.0 * (durationMs / 1000.0)) & ~1;
        short[] samples = new short[count];
        for (int i = 0; i < count; i += 2) {
            short sample = (short) (Math.sin(2 * Math.PI * i / (44100.0 / freqHz)) * 0x7FFF);
            samples[i + 0] = sample;
            samples[i + 1] = sample;
        }
        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                count * (Short.SIZE / 8), AudioTrack.MODE_STATIC);
        track.write(samples, 0, count);

        return track;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void generateVibration(VibrationEffect vibrationEffect) {
        vibrator.vibrate(vibrationEffect);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void flashDash() {

        try {
            cameraManager.setTorchMode(getCameraID, true);
            Thread.sleep(600);
        } catch (CameraAccessException | InterruptedException e) {
            e.printStackTrace();
        }
        try {
            cameraManager.setTorchMode(getCameraID, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void flashDot() {

        try {
            cameraManager.setTorchMode(getCameraID, true);
            Thread.sleep(100);
        } catch (CameraAccessException | InterruptedException e) {
            e.printStackTrace();
        }
        try {
            cameraManager.setTorchMode(getCameraID, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    class playSound extends Thread {
        @Override
        public void run() {

            String morseString01 = null;
            StringBuilder stringBuilder01 = new StringBuilder();

            char[] getEditText = editText.getText().toString().trim().toCharArray();
            for (char c : getEditText) {

                morseString01 = hashmap.get(Character.toString(c).toLowerCase());
                stringBuilder01.append(morseString01);

            }
            for (int xi = 0; xi < stringBuilder01.length(); xi++) {
                if (mBoolean)
                    switch (stringBuilder01.charAt(xi)) {

                        case '−': {
                            tone = generateTone(140, 550);
                            tone.play();
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case '·': {
                            tone = generateTone(440, 250);
                            tone.play();
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case '/': {
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case ' ': {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        }

                    }
            }
            super.run();
        }
    }

    class vibrationClass extends Thread {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            String morseString01 = null;
            StringBuilder stringBuilder01 = new StringBuilder();

            char[] getEditText = editText.getText().toString().trim().toCharArray();

            for (int ii = 0; ii < getEditText.length; ii++) {

                char c = getEditText[ii];
                morseString01 = hashmap.get(Character.toString(c).toLowerCase());
                stringBuilder01.append(morseString01);

            }

            for (int xi = 0; xi < stringBuilder01.length(); xi++) {
                if (mBoolean)
                    switch (stringBuilder01.charAt(xi)) {

                        case '−': {
                            VibrationEffect vibrationEffect2 = null;
                            vibrationEffect2 = VibrationEffect.createOneShot(400, 200);

                            generateVibration(vibrationEffect2);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case '·': {
                            VibrationEffect vibrationEffect2 = null;
                            vibrationEffect2 = VibrationEffect.createOneShot(100, 50);
                            generateVibration(vibrationEffect2);
                        }
                        case '/': {
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case ' ': {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
            }
            super.run();
        }
    }

    class flashClass extends Thread {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {
            String morseString01 = null;
            StringBuilder stringBuilder01 = new StringBuilder();

            char[] getEditText = editText.getText().toString().trim().toCharArray();

            for (char c : getEditText) {

                morseString01 = hashmap.get(Character.toString(c).toLowerCase());
                stringBuilder01.append(morseString01);

            }

            for (int xi = 0; xi < stringBuilder01.length(); xi++) {
                if (mBoolean)
                    switch (stringBuilder01.charAt(xi)) {

                        case '−': {
                            flashDash();
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case '·': {
                            flashDot();
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case '/': {
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case ' ': {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }

            }
            super.run();
        }
    }
}
