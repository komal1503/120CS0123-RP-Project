package com.example.bp_app;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
//import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
//import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

//import android.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BluetoothTest";
    private static final int MESSAGE_READ = 1;

    private BluetoothDevice targetDevice;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private byte[] buffer;
    private boolean connected = false;

    private TextView textView;
    private LineChart heartRateChart;
    private LineDataSet dataSet;
    private LineData lineData;
    private int dataCount = 0;
    // The median filter and low-pass filter variables.



    private final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID for SPP

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LowPassFilter lowPassFilter = new LowPassFilter(0.5f,1024);

//        Button ppgButton = findViewById(R.id.ppgButton);
//        ppgButton.setOnClickListener(view -> {
//            Intent intent = new Intent(MainActivity.this, PpgActivity.class);
//            startActivity(intent);
//        });
        // Initialize the median and low-pass filters.


        textView = findViewById(R.id.dataTextView);

        // Initialize the LineChart and LineDataSet for heart rate data
        heartRateChart = findViewById(R.id.heartRateChart);
        YAxis yAxis = heartRateChart.getAxisLeft();
        yAxis.setAxisMinimum(2200f);
        yAxis.setAxisMaximum(4500f);
        dataSet = new LineDataSet(new ArrayList<>(), "Heart Rate");
        lineData = new LineData(dataSet);
        heartRateChart.setData(lineData);
        dataSet.setDrawCircles(false);



        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled, request to enable it
            // You can use startActivityForResult() to request the user to enable Bluetooth
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
                }
                if (device.getName().equals("YourESP32Name")) { // Replace with your ESP32 device name
                    targetDevice = device;
                    break;
                }
            }
        }

        if (targetDevice == null) {
            // Target device not found
            return;
        }

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            }
            bluetoothSocket = targetDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            connected = true;

            inputStream = bluetoothSocket.getInputStream();
            buffer = new byte[1024];

            final Handler handler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(@NonNull Message msg) {
                    if (msg.what == MESSAGE_READ) {
                        String data = (String) msg.obj;

                        String[] numbers = data.split("\n");

                        for (String number : numbers) {
                            // Remove non-numeric characters (e.g., newline)
                            number = number.replaceAll("[^0-9]", "");

                            if (!number.isEmpty()) {
                                try {
                                    int heartRate = Integer.parseInt(number);
                                    textView.setText(number);
//                                    Log.d(TAG, "Raw value: " + heartRate);
//
//                                    // Apply median filter
//                                    heartRate = (int) medianFilter.filter(heartRate);
//                                    Log.d(TAG, "After median filter: " + heartRate);
                                    heartRate = (int) lowPassFilter.filter(heartRate);
                                    addEntry(heartRate);
                                } catch (NumberFormatException e) {
                                    // Handle invalid data or parsing errors
                                    Log.e(TAG, "Error parsing heart rate data", e);
                                }
                            }
                        }
                        Log.d(TAG, "Received data: " + data);
                        Log.d(TAG, "Processed heart rate data: " + Arrays.toString(numbers));
                    }
                    return true;
                }
            });

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (connected) {
                        try {
                            int bytes = inputStream.read(buffer);
                            String data = new String(buffer, 0, bytes);
                            Message message = handler.obtainMessage(MESSAGE_READ, data);
                            message.sendToTarget();
                        } catch (IOException e) {
                            Log.e(TAG, "Error reading input stream", e);
                            connected = false;
                        }
                    }
                }
            });
            thread.start();

        } catch (IOException e) {
            Log.e(TAG, "Error creating socket", e);
            connected = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connected) {
            try {
                inputStream.close();
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing socket", e);
            }
        }
    }


    private void addEntry(float value) {
        // Add new entry to the chart
        if (dataSet.getEntryCount() >= 1000) {
            // Remove oldest entry if there are already 1000 entries
            dataSet.removeFirst();

            // Shift x-values of remaining entries
            for (Entry entry : dataSet.getValues()) {
                entry.setX(entry.getX() - 1);
            }
        }


        dataSet.addEntry(new Entry(dataCount, value));

        // Notify the chart data has changed
        lineData.notifyDataChanged();
        heartRateChart.notifyDataSetChanged();
        heartRateChart.setVisibleXRangeMaximum(1000);
        heartRateChart.moveViewToX(dataCount);


        dataCount++;
    }


    public class LowPassFilter {
        private float alpha;
        private float lastValue;

        private DWTFilter dwtFilter;

        public LowPassFilter(float alpha,int bufferSize) {
            this.alpha = alpha;
            this.lastValue = 0.0f;
            this.dwtFilter = new DWTFilter(2,bufferSize);
        }

        public float filter(float value) {
            // Apply DWT filter
            float[] dwtResult = dwtFilter.filter(new float[]{value});
            // Apply low-pass filter
            float lowPassValue = dwtResult[0];
            float result = alpha * lowPassValue + (1 - alpha) * lastValue;
            lastValue = result;
            return result;
        }
    }

    public class DWTFilter {
        private int levels;
        private float[] inputBuffer;
        private float[] outputBuffer;

        public DWTFilter(int levels, int bufferSize) {
            this.levels = levels;
            this.inputBuffer = new float[bufferSize];
            this.outputBuffer = new float[bufferSize];
        }

        public float[] filter(float[] input) {
            // Assuming input length is a power of 2 for simplicity
            int length = input.length;

            // Initialize input buffer
            System.arraycopy(input, 0, inputBuffer, 0, length);

            // Perform DWT
            for (int level = 0; level < levels; level++) {
                length /= 2;
                for (int i = 0; i < length; i++) {
                    float sum = inputBuffer[2 * i] + inputBuffer[2 * i + 1];
                    float diff = inputBuffer[2 * i] - inputBuffer[2 * i + 1];

                    outputBuffer[i] = sum;
                    outputBuffer[length + i] = diff;
                }

                // Swap input and output buffers
                float[] temp = inputBuffer;
                inputBuffer = outputBuffer;
                outputBuffer = temp;
            }

            // Copy the final result back to the input buffer
            System.arraycopy(inputBuffer, 0, outputBuffer, 0, inputBuffer.length);

            return outputBuffer;
        }
    }








//    private void updateHeartRateChart(int heartRate) {
//        if (lineData != null) {
//            dataCount++;
//
//            // Only add data if dataCount is less than or equal to 5,000
////            if (dataCount <= 5000) {
//                dataSet.addEntry(new Entry(dataCount, heartRate));
//                lineData.notifyDataChanged();
//                heartRateChart.notifyDataSetChanged();
//                heartRateChart.setVisibleXRangeMaximum(1000); // Limit the number of visible data points
//                heartRateChart.moveViewToX(1000);
//
//                addEntry(heartRate);
//
//
// //           }
//
//        }
//    }

}