package com.example.BPAPP.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.controls.Flash;
import com.example.BPAPP.R;
import com.example.BPAPP.constant.GlobalConstants;
import com.example.BPAPP.domain.image.PpgFrameProcessor;
import com.example.BPAPP.service.MeasurementPhase;
import com.example.BPAPP.service.MotionMonitoringService;

import java.lang.ref.WeakReference;
import java.io.*;
import java.util.*;


public class PpgActivity extends AppCompatActivity implements PpgFrameProcessor.ChartUpdateListener{

    private CameraView camera;
    private TextView heartRate;

    private PpgFrameProcessor ppgFrameProcessor;
//    private Vector v1 = new Vector();
//    private Vector v2 = new Vector();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppg);
        initCamera();
        initChart();
        initButton();
    }

    public void initCamera() {
        camera = findViewById(R.id.view_camera);
        camera.setVisibility(View.INVISIBLE);
        camera.setLifecycleOwner(this);
        camera.setFrameProcessingFormat(ImageFormat.YUV_420_888);
        heartRate = findViewById(R.id.text_heart_rate);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initButton() {
        findViewById(R.id.btn_start_measurement).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                startMeasurement();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                stopMeasurement();
            }
            return false;
        });
    }

    public void initChart() {
        LinearLayout chartContainer = findViewById(R.id.chartContainer);
        ppgFrameProcessor = new PpgFrameProcessor(this,new WeakReference<>(heartRate), new WeakReference<>(this));
        LineChart ppgChart = ppgFrameProcessor.getPpgChart();

        // Set layout parameters for the LineChart
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                800
        );
        ppgChart.setLayoutParams(params);

        chartContainer.addView(ppgChart);
    }

    /**
     * Starting heart rate measurement:
     * - flash is turned on
     * - processing consecutive frames starts
     * - label showing heart rate will later be updated according to new results
     * - start monitoring movement of the phone
     */
    private void startMeasurement() {
        camera.setFlash(Flash.TORCH);
        camera.addFrameProcessor(new PpgFrameProcessor(this,new WeakReference<>(heartRate),new WeakReference<>(this)));
        motionMonitoring(MeasurementPhase.START);
    }

    /**
     * Stopping heart rate measurement:
     * - flash is turned off
     * - processing frames stops
     * - label showing heart rate is cleared
     * - stop monitoring movement of the phone
     */
    private void stopMeasurement() {
        camera.setFlash(Flash.OFF);
        camera.clearFrameProcessors();
        heartRate.setText(R.string.label_empty);
        motionMonitoring(MeasurementPhase.STOP);
    }

    private void motionMonitoring(MeasurementPhase phase) {
        Intent intent = new Intent(GlobalConstants.MEASUREMENT_PHASE_CHANGE);
        intent.putExtra(GlobalConstants.MEASUREMENT_PHASE_CHANGE, phase.name());
        sendBroadcast(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera.open();
        startService(motionMonitoringService());
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.close();
        stopService(motionMonitoringService());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.destroy();
        stopService(motionMonitoringService());
    }

    /**
     * This background service monitors the motion of the phone
     * The phone needs to be still to do precise measurements
     */
    private Intent motionMonitoringService() {
        return new Intent(PpgActivity.this, MotionMonitoringService.class);
    }

    @Override
    public void onDataPointAdded(long timestamp, int redSum) {
        runOnUiThread(() -> {
//            v1.add(redSum);
//            v2.add(timestamp);



            // Update the chart with the new data point
            if (ppgFrameProcessor != null && ppgFrameProcessor.getPpgChart() != null) {
                ppgFrameProcessor.addEntryToChart( redSum);
            }

//            for(int i = 0; i < v1.size(); i++) Log.d("YourTag", String.valueOf(v1.get(i)));
        });
    }


}