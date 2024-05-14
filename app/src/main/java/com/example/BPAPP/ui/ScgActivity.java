package com.example.BPAPP.ui;

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

import com.example.BPAPP.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

//import android.R;
//
//public class ScgActivity extends AppCompatActivity {
//
//    private static final String TAG = "BluetoothTest";
//    private static final int MESSAGE_READ = 1;
//
//    private BluetoothDevice targetDevice;
//    private BluetoothSocket bluetoothSocket;
//    private InputStream inputStream;
//    private byte[] buffer;
//    private boolean connected = false;
//
//    private TextView textView;
//    private LineChart heartRateChart;
//
//
//    private LineDataSet dataSet;
//    private LineData lineData;
//
//    private LineChart secondChart;
//    private LineDataSet secondDataSet;
//    private LineData secondLineData;
//    private int secondDataCount = 0;
//    private int dataCount = 0;
//    // The median filter and low-pass filter variables.
//
//    private List<Float> originalSignal = new ArrayList<>();
//    private List<Float> convolutedSignal = new ArrayList<>();
//
//    // Define the filter coefficients
//    List<Float> filter = Arrays.asList(0.1f, 0.2f, 0.3f, 0.2f, 0.1f);
//
//
//    private final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID for SPP
//
//    @RequiresApi(api = Build.VERSION_CODES.S)
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_scg);
//
////        LowPassFilter lowPassFilter = new LowPassFilter(0.5f,1024);
//
////        Button ppgButton = findViewById(R.id.ppgButton);
////        ppgButton.setOnClickListener(view -> {
////            Intent intent = new Intent(MainActivity.this, PpgActivity.class);
////            startActivity(intent);
////        });
//        // Initialize the median and low-pass filters.
//
//
//        textView = findViewById(R.id.dataTextView);
//
//        // Initialize the LineChart and LineDataSet for heart rate data
//        heartRateChart = findViewById(R.id.heartRateChart);
//        if (heartRateChart != null) {
//            YAxis yAxis = heartRateChart.getAxisLeft();
//            yAxis.setAxisMinimum(2200f);
//            yAxis.setAxisMaximum(4500f);
//            dataSet = new LineDataSet(new ArrayList<>(), "Heart Rate");
//            lineData = new LineData(dataSet);
//            heartRateChart.setData(lineData);
//            dataSet.setDrawCircles(false);
//            dataSet.setDrawValues(false);
//        }
//
//        // Initialize the second LineChart
//        secondChart = findViewById(R.id.secondChart);
//        YAxis secondYAxis = secondChart.getAxisLeft();
//        secondYAxis.setAxisMinimum(1f);  // Set appropriate minimum and maximum values
//        secondYAxis.setAxisMaximum(50000f);
//        secondDataSet = new LineDataSet(new ArrayList<>(), "Convoluted Graph");
//        secondLineData = new LineData(secondDataSet);
//        secondChart.setData(secondLineData);
//        secondDataSet.setDrawCircles(false);
//
//
//
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (bluetoothAdapter == null) {
//            // Device doesn't support Bluetooth
//            return;
//        }
//
//        if (!bluetoothAdapter.isEnabled()) {
//            // Bluetooth is not enabled, request to enable it
//            // You can use startActivityForResult() to request the user to enable Bluetooth
//            return;
//        }
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
//        }
//        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
//        if (pairedDevices.size() > 0) {
//            for (BluetoothDevice device : pairedDevices) {
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
//                }
//                if (device.getName().equals("YourESP32Name")) { // Replace with your ESP32 device name
//                    targetDevice = device;
//                    break;
//                }
//            }
//        }
//
//        if (targetDevice == null) {
//            // Target device not found
//            return;
//        }
//
//        try {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
//            }
//            bluetoothSocket = targetDevice.createRfcommSocketToServiceRecord(uuid);
//            bluetoothSocket.connect();
//            connected = true;
//
//            inputStream = bluetoothSocket.getInputStream();
//            buffer = new byte[1024];
//
//            final Handler handler = new Handler(new Handler.Callback() {
//                @Override
//                public boolean handleMessage(@NonNull Message msg) {
//                    if (msg.what == MESSAGE_READ) {
//                        String data = (String) msg.obj;
//
//                        String[] numbers = data.split("\n");
//
//                        for (String number : numbers) {
//                            // Remove non-numeric characters (e.g., newline)
//                            number = number.replaceAll("[^0-9]", "");
//
//                            if (!number.isEmpty()) {
//                                try {
//                                    int heartRate = Integer.parseInt(number);
//                                    textView.setText(number);
////                                    Log.d(TAG, "Raw value: " + heartRate);
////
////                                    // Apply median filter
////                                    heartRate = (int) medianFilter.filter(heartRate);
////                                    Log.d(TAG, "After median filter: " + heartRate);
////                                    heartRate = (int) lowPassFilter.filter(heartRate);
//                                    addEntry(heartRate);
//                                } catch (NumberFormatException e) {
//                                    // Handle invalid data or parsing errors
//                                    Log.e(TAG, "Error parsing heart rate data", e);
//                                }
//                            }
//                        }
//                        Log.d(TAG, "Received data: " + data);
//                        Log.d(TAG, "Processed heart rate data: " + Arrays.toString(numbers));
//                    }
//                    return true;
//                }
//            });
//
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (connected) {
//                        try {
//                            int bytes = inputStream.read(buffer);
//                            String data = new String(buffer, 0, bytes);
//                            Message message = handler.obtainMessage(MESSAGE_READ, data);
//                            message.sendToTarget();
//
//                            // Update UI on the main thread
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    // Add any UI update logic here
//                                    // For example, update charts, text views, etc.
//                                }
//                            });
//                        } catch (IOException e) {
//                            Log.e(TAG, "Error reading input stream", e);
//                            connected = false;
//                        }
//                    }
//                }
//            });
//            thread.start();
//
//        } catch (IOException e) {
//            Log.e(TAG, "Error creating socket", e);
//            connected = false;
//        }
//
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (connected) {
//            try {
//                inputStream.close();
//                bluetoothSocket.close();
//            } catch (IOException e) {
//                Log.e(TAG, "Error closing socket", e);
//            }
//        }
//    }
//
//
//    private void addEntry(float value) {
////         Add new entry to the chart
//        if (dataSet.getEntryCount() >= 1000) {
//            // Remove oldest entry if there are already 1000 entries
//            dataSet.removeFirst();
//
//            // Shift x-values of remaining entries
//            for (Entry entry : dataSet.getValues()) {
//                entry.setX(entry.getX() - 1);
//            }
//        }
//
//
//        dataSet.addEntry(new Entry(dataCount, value));
//
//        // Notify the chart data has changed
//        lineData.notifyDataChanged();
//        heartRateChart.notifyDataSetChanged();
//        heartRateChart.setVisibleXRangeMaximum(1000);
//        heartRateChart.moveViewToX(dataCount);
//
//        // Convolute the data and add it to the second chart
//        List<Float> convolutedData = convolute(originalSignal, filter);
//        for (int i = 0; i < convolutedData.size(); i++) {
//            secondDataSet.addEntry(new Entry(secondDataCount++, convolutedData.get(i)));
//        }
//        secondLineData.notifyDataChanged();
//        secondChart.notifyDataSetChanged();
//        secondChart.setVisibleXRangeMaximum(1000);
//        secondChart.moveViewToX(secondDataCount);
//
//        dataCount++;
//
//
//        originalSignal.clear();
//        }
//
//
//    private List<Float> convolute(List<Float> myData, List<Float> filter) {
//        List<Float> convolutedData = new ArrayList<>();
//        List<Float> newData = new ArrayList<>();
//        int size = filter.size();
//
//        // Padding zeros to the beginning and end of the original signal
//        for (int i = 0; i < size - 1; i++) {
//            newData.add(0.0f);
//        }
//        newData.addAll(myData);
//        for (int i = 0; i < size - 1; i++) {
//            newData.add(0.0f);
//        }
//
//        // Convolution process
//        for (int i = 0; i <= newData.size() - filter.size(); i++) {
//            float val = 0.0f;
//            for (int j = 0; j < filter.size(); j++) {
//                val += newData.get(j + i) * filter.get(j);
//            }
//            convolutedData.add(val);
//        }
//
//        return convolutedData;
//    }
//
//
//
//
//
//
//
//
//
////    public class LowPassFilter {
////        private float alpha;
////        private float lastValue;
////
////        private DWTFilter dwtFilter;
////
////        public LowPassFilter(float alpha,int bufferSize) {
////            this.alpha = alpha;
////            this.lastValue = 0.0f;
////            this.dwtFilter = new DWTFilter(2,bufferSize);
////        }
////
////        public float filter(float value) {
////            // Apply DWT filter
////            float[] dwtResult = dwtFilter.filter(new float[]{value});
////            // Apply low-pass filter
////            float lowPassValue = dwtResult[0];
////            float result = alpha * lowPassValue + (1 - alpha) * lastValue;
////            lastValue = result;
////            return result;
////        }
////    }
//
////    public class DWTFilter {
////        private int levels;
////        private float[] inputBuffer;
////        private float[] outputBuffer;
////
////        public DWTFilter(int levels, int bufferSize) {
////            this.levels = levels;
////            this.inputBuffer = new float[bufferSize];
////            this.outputBuffer = new float[bufferSize];
////        }
////
////        public float[] filter(float[] input) {
////            // Assuming input length is a power of 2 for simplicity
////            int length = input.length;
////
////            // Initialize input buffer
////            System.arraycopy(input, 0, inputBuffer, 0, length);
////
////            // Perform DWT
////            for (int level = 0; level < levels; level++) {
////                length /= 2;
////                for (int i = 0; i < length; i++) {
////                    float sum = inputBuffer[2 * i] + inputBuffer[2 * i + 1];
////                    float diff = inputBuffer[2 * i] - inputBuffer[2 * i + 1];
////
////                    outputBuffer[i] = sum;
////                    outputBuffer[length + i] = diff;
////                }
////
////                // Swap input and output buffers
////                float[] temp = inputBuffer;
////                inputBuffer = outputBuffer;
////                outputBuffer = temp;
////            }
////
////            // Copy the final result back to the input buffer
////            System.arraycopy(inputBuffer, 0, outputBuffer, 0, inputBuffer.length);
////
////            return outputBuffer;
////        }
////    }
//
//}



public class ScgActivity extends AppCompatActivity {

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

    private LineChart secondChart;
    private LineDataSet secondDataSet;
    private LineData secondLineData;
    private int secondDataCount = 0;
    private int dataCount = 0;
    // The median filter and low-pass filter variables.

    private List<Float> originalSignal = new ArrayList<>();
    private List<Float> convolutedSignal = new ArrayList<>();

    // Define the filter coefficients
//    List<Float> filter = Arrays.asList(0.1f, 0.2f, 0.3f, 0.2f, 0.1f);
    List<Double> filter = Arrays.asList(0.00054221,0.000083571,0.000028217,-0.00007191,-0.00021191,-0.00037979,-0.00055672,-0.00071794,-0.00083521,-0.00087966,-0.0008257,-0.0006546,-0.00035819,0.000058762,0.00057702,0.0011635,0.0017731,0.0023531,0.0028482,0.0032066,0.0033862,0.0033601,0.0031205,0.0026807,0.002075,0.0013559,0.00058832,-0.00015783,-0.00081529,-0.0013265,-0.0016499,-0.0017706,-0.001693,-0.0014485,-0.0010872,-0.00067225,-0.00027245,0.000047803,0.00023634,0.00026072,0.00011221,-0.00019268,-0.00061493,-0.001098,-0.0015765,-0.0019844,-0.0022656,-0.0023814,-0.0023168,-0.0020825,-0.0017141,-0.0012669,-0.00080819,-0.00040789,-0.00012812,-0.00001371,-0.000085431,-0.00033644,-0.00073311,-0.0012188,-0.0017217,-0.0021664,-0.002483,-0.0026195,-0.0025488,-0.0022733,-0.0018261,-0.0012657,-0.00066878,-0.00011854,0.00030703,0.00054741,0.00056862,0.00036944,-0.000018042,-0.0005319,-0.0010893,-0.0015979,-0.0019697,-0.0021342,-0.0020499,-0.0017106,-0.0011479,-0.00042709,0.00036112,0.001114,0.0017314,0.0021314,0.0022623,0.0021119,0.0017103,0.0011259,0.00045735,-0.00018136,-0.00067724,-0.0009352,-0.00089331,-0.00053264,0.00011809,0.00098585,0.0019624,0.00292,0.0037293,0.0042793,0.0044942,0.0043459,0.0038595,0.0031113,0.0022187,0.001324,0.00057327,0.000094929,-0.000020467,0.00026519,0.00092979,0.0018927,0.0030246,0.004166,0.0051493,0.0058243,0.0060811,0.0058681,0.0052012,0.0041641,0.0028974,0.0015798,0.00040219,-0.0004606,-0.00087678,-0.00077862,-0.00017526,0.00084641,0.0021317,0.0034793,0.0046697,0.0054975,0.0058032,0.0054983,0.0045814,0.003142,0.0013498,-0.0005674,-0.0023558,-0.0037728,-0.0046238,-0.0047932,-0.0042654,-0.0031302,-0.0015744,0.0001431,0.0017266,0.0028871,0.0033854,0.0030709,0.0019076,-0.000015048,-0.0024904,-0.0052197,-0.0078528,-0.010038,-0.011476,-0.011964,-0.011434,-0.0099688,-0.0077959,-0.0052622,-0.0027879,-0.00080752,0.0002938,0.00024127,-0.0010827,-0.0036114,-0.0070927,-0.011115,-0.015158,-0.018663,-0.021112,-0.022108,-0.021442,-0.019135,-0.015455,-0.010895,-0.0061188,-0.0018802,0.0010792,0.0021373,0.00089025,-0.0027689,-0.0086072,-0.016048,-0.024215,-0.032013,-0.03825,-0.04177,-0.041601,-0.037083,-0.027972,-0.014492,0.0026519,0.022323,0.043053,0.06318,0.081018,0.095023,0.10396,0.10703,0.10396,0.095023,0.081018,0.06318,0.043053,0.022323,0.0026519,-0.014492,-0.027972,-0.037083,-0.041601,-0.04177,-0.03825,-0.032013,-0.024215,-0.016048,-0.0086072,-0.0027689,0.00089025,0.0021373,0.0010792,-0.0018802,-0.0061188,-0.010895,-0.015455,-0.019135,-0.021442,-0.022108,-0.021112,-0.018663,-0.015158,-0.011115,-0.0070927,-0.0036114,-0.0010827,0.00024127,0.0002938,-0.00080752,-0.0027879,-0.0052622,-0.0077959,-0.0099688,-0.011434,-0.011964,-0.011476,-0.010038,-0.0078528,-0.0052197,-0.0024904,-0.000015048,0.0019076,0.0030709,0.0033854,0.0028871,0.0017266,0.0001431,-0.0015744,-0.0031302,-0.0042654,-0.0047932,-0.0046238,-0.0037728,-0.0023558,-0.0005674,0.0013498,0.003142,0.0045814,0.0054983,0.0058032,0.0054975,0.0046697,0.0034793,0.0021317,0.00084641,-0.00017526,-0.00077862,-0.00087678,-0.0004606,0.00040219,0.0015798,0.0028974,0.0041641,0.0052012,0.0058681,0.0060811,0.0058243,0.0051493,0.004166,0.0030246,0.0018927,0.00092979,0.00026519,-0.000020467,0.000094929,0.00057327,0.001324,0.0022187,0.0031113,0.0038595,0.0043459,0.0044942,0.0042793,0.0037293,0.00292,0.0019624,0.00098585,0.00011809,-0.00053264,-0.00089331,-0.0009352,-0.00067724,-0.00018136,0.00045735,0.0011259,0.0017103,0.0021119,0.0022623,0.0021314,0.0017314,0.001114,0.00036112,-0.00042709,-0.0011479,-0.0017106,-0.0020499,-0.0021342,-0.0019697,-0.0015979,-0.0010893,-0.0005319,-0.000018042,0.00036944,0.00056862,0.00054741,0.00030703,-0.00011854,-0.00066878,-0.0012657,-0.0018261,-0.0022733,-0.0025488,-0.0026195,-0.002483,-0.0021664,-0.0017217,-0.0012188,-0.00073311,-0.00033644,-0.000085431,-0.00001371,-0.00012812,-0.00040789,-0.00080819,-0.0012669,-0.0017141,-0.0020825,-0.0023168,-0.0023814,-0.0022656,-0.0019844,-0.0015765,-0.001098,-0.00061493,-0.00019268,0.00011221,0.00026072,0.00023634,0.000047803,-0.00027245,-0.00067225,-0.0010872,-0.0014485,-0.001693,-0.0017706,-0.0016499,-0.0013265,-0.00081529,-0.00015783,0.00058832,0.0013559,0.002075,0.0026807,0.0031205,0.0033601,0.0033862,0.0032066,0.0028482,0.0023531,0.0017731,0.0011635,0.00057702,0.000058762,-0.00035819,-0.0006546,-0.0008257,-0.00087966,-0.00083521,-0.00071794,-0.00055672,-0.00037979,-0.00021191,-0.00007191,0.000028217,0.000083571,0.00054221
    );


    private final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID for SPP

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scg);
        Log.d("scg activity", "yooo");

        initBluetooth();

    }

    public void initBluetooth() {
        textView = findViewById(R.id.dataTextView);

        // Initialize the LineChart and LineDataSet for heart rate data
        heartRateChart = findViewById(R.id.heartRateChart);
        if (heartRateChart != null) {
            YAxis yAxis = heartRateChart.getAxisLeft();
            yAxis.setAxisMinimum(1000f);
            yAxis.setAxisMaximum(3000f);
            dataSet = new LineDataSet(new ArrayList<>(), "Heart Rate");
            lineData = new LineData(dataSet);
            heartRateChart.setData(lineData);
            dataSet.setDrawCircles(false);
            dataSet.setDrawValues(false);
        }

        // Initialize the second LineChart
        secondChart = findViewById(R.id.secondChart);
        YAxis secondYAxis = secondChart.getAxisLeft();
        secondYAxis.setAxisMinimum(0f);  // Set appropriate minimum and maximum values
        secondYAxis.setAxisMaximum(2f);
        secondDataSet = new LineDataSet(new ArrayList<>(), "Convoluted Graph");
        secondLineData = new LineData(secondDataSet);
        secondChart.setData(secondLineData);
        secondDataSet.setDrawCircles(false);



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
//                                    heartRate = (int) lowPassFilter.filter(heartRate);
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

                            // Update UI on the main thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Add any UI update logic here
                                    // For example, update charts, text views, etc.
                                }
                            });
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
//         Add new entry to the chart
//        if (dataSet.getEntryCount() >= 1000) {
//            // Remove oldest entry if there are already 1000 entries
//            dataSet.removeFirst();
//
//            // Shift x-values of remaining entries
//            for (Entry entry : dataSet.getValues()) {
//                entry.setX(entry.getX() - 1);
//            }
//        }


        dataSet.addEntry(new Entry(dataCount, value));

        // Notify the chart data has changed
        lineData.notifyDataChanged();
        heartRateChart.notifyDataSetChanged();
        heartRateChart.setVisibleXRangeMaximum(1000);
        heartRateChart.moveViewToX(dataCount);

        // Convolute the data and add it to the second chart
        List<Float> convolutedData = convolute(originalSignal, filter);
        Log.d(TAG, "convoluted Data: " + convolutedData);
        for (int i = 0; i < convolutedData.size(); i++) {
            secondDataSet.addEntry(new Entry(secondDataCount++, convolutedData.get(i)));
        }
        secondLineData.notifyDataChanged();
        secondChart.notifyDataSetChanged();
        secondChart.setVisibleXRangeMaximum(1000);
        secondChart.moveViewToX(secondDataCount);

        dataCount++;


        originalSignal.clear();
    }


    private List<Float> convolute(List<Float> myData, List<Double> filter) {
        List<Float> convolutedData = new ArrayList<>();
        List<Float> newData = new ArrayList<>();
        int size = filter.size();

        // Padding zeros to the beginning and end of the original signal
        for (int i = 0; i < size - 1; i++) {
            newData.add(0.0f);
        }
        newData.addAll(myData);
        for (int i = 0; i < size - 1; i++) {
            newData.add(0.0f);
        }

        // Convolution process
        for (int i = 0; i <= newData.size() - filter.size(); i++) {
            float val = 0.0f;
            for (int j = 0; j < filter.size(); j++) {
                val += newData.get(j + i) * filter.get(j);
            }
            convolutedData.add(val);
        }

        return convolutedData;
    }









//    public class LowPassFilter {
//        private float alpha;
//        private float lastValue;
//
//        private DWTFilter dwtFilter;
//
//        public LowPassFilter(float alpha,int bufferSize) {
//            this.alpha = alpha;
//            this.lastValue = 0.0f;
//            this.dwtFilter = new DWTFilter(2,bufferSize);
//        }
//
//        public float filter(float value) {
//            // Apply DWT filter
//            float[] dwtResult = dwtFilter.filter(new float[]{value});
//            // Apply low-pass filter
//            float lowPassValue = dwtResult[0];
//            float result = alpha * lowPassValue + (1 - alpha) * lastValue;
//            lastValue = result;
//            return result;
//        }
//    }

//    public class DWTFilter {
//        private int levels;
//        private float[] inputBuffer;
//        private float[] outputBuffer;
//
//        public DWTFilter(int levels, int bufferSize) {
//            this.levels = levels;
//            this.inputBuffer = new float[bufferSize];
//            this.outputBuffer = new float[bufferSize];
//        }
//
//        public float[] filter(float[] input) {
//            // Assuming input length is a power of 2 for simplicity
//            int length = input.length;
//
//            // Initialize input buffer
//            System.arraycopy(input, 0, inputBuffer, 0, length);
//
//            // Perform DWT
//            for (int level = 0; level < levels; level++) {
//                length /= 2;
//                for (int i = 0; i < length; i++) {
//                    float sum = inputBuffer[2 * i] + inputBuffer[2 * i + 1];
//                    float diff = inputBuffer[2 * i] - inputBuffer[2 * i + 1];
//
//                    outputBuffer[i] = sum;
//                    outputBuffer[length + i] = diff;
//                }
//
//                // Swap input and output buffers
//                float[] temp = inputBuffer;
//                inputBuffer = outputBuffer;
//                outputBuffer = temp;
//            }
//
//            // Copy the final result back to the input buffer
//            System.arraycopy(inputBuffer, 0, outputBuffer, 0, inputBuffer.length);
//
//            return outputBuffer;
//        }
//    }

}