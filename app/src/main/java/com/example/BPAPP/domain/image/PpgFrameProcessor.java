package com.example.BPAPP.domain.image;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.otaliastudios.cameraview.size.Size;
import com.example.BPAPP.domain.adapter.HeartRate;
import com.example.BPAPP.domain.adapter.HeartRateAdapter;
import com.example.BPAPP.domain.signalprocessing.pipeline.Pipeline;
import com.example.BPAPP.domain.signalprocessing.pipeline.PpgProcessingPipeline;

import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static com.example.BPAPP.constant.GlobalConstants.BATCH_SIZE;
import static com.example.BPAPP.domain.image.PixelProcessor.yuvToRedSum;
import static com.example.BPAPP.root.PpgApplication.executor;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class PpgFrameProcessor implements FrameProcessor {

    private Context context;
    private LineChart ppgChart;
    /**
     * The number of frames collected for a single batch
     */
    private int frameCounter;
    private int counter = 0;

    /**
     * Y axis: the amount of color Red
     */
    private int[] signal;

    /**
     * X axis: current time in milliseconds when a frame is captured
     */
    private long[] time;

    /**
     * Reference to the UI element displaying heart rate
     */
    private WeakReference<TextView> viewWeakReference;
    private WeakReference<ChartUpdateListener> chartUpdateListener;

    public PpgFrameProcessor(Context context, WeakReference<TextView> viewWeakReference, WeakReference<ChartUpdateListener> chartUpdateListener) {
        this.context = context;
        this.viewWeakReference = viewWeakReference;
        this.chartUpdateListener = chartUpdateListener;
        this.ppgChart = new LineChart(context);
        resetParameters();
    }


    @Override
    @WorkerThread
    public void process(@NonNull Frame frame) {
        Size size = frame.getSize();
        int redSum = yuvToRedSum(frame.getData(), size.getWidth(), size.getHeight());
        signal[frameCounter] = redSum;
        time[frameCounter] = frame.getTime();

        if (chartUpdateListener != null && chartUpdateListener.get() != null) {
            chartUpdateListener.get().onDataPointAdded(frame.getTime(), redSum);
        }

        if (++frameCounter == BATCH_SIZE) {
            calculateHeartRate();
            resetParameters();
        }
    }

    public interface ChartUpdateListener {
        void onDataPointAdded(long timestamp, int redSum);
    }

    private void calculateHeartRate() {
        long startTime = time[0];
        int[] y = IntStream.of(signal).toArray();
        long[] x = LongStream.of(time).map(t -> t - startTime).toArray();

        CompletableFuture.supplyAsync(() -> processSignal(y), executor())
                .thenApply(signal -> toHeartRate(signal, x))
                .thenAccept(this::updateUI);
    }

    private int[] processSignal(int[] unprocessedSignal) {
        Pipeline pipeline = PpgProcessingPipeline.pipeline();
        return pipeline.execute(unprocessedSignal);
    }

    private String toHeartRate(int[] processedSignal, long[] timestamps) {
        HeartRate adapter = new HeartRateAdapter(processedSignal, timestamps);
        return adapter.convertToHeartRate();
    }

    private void updateUI(String heartRate) {
        TextView textView = viewWeakReference.get();
        textView.post(() -> textView.setText(heartRate));
    }

    private void resetParameters() {
        frameCounter = 0;
        signal = new int[BATCH_SIZE];
        time = new long[BATCH_SIZE];
    }

    public LineChart getPpgChart() {
        return ppgChart;
    }

    public void addEntryToChart(int redSum) {
        counter++;
        LineData data = ppgChart.getData();
        if (data == null) {
            data = new LineData();
            Log.d("ChartUpdate", "Created new LineData");
            ppgChart.setData(data);
            // Customize the appearance of the chart data
            LineDataSet dataSet = new LineDataSet(null, "PPG Signal");
            dataSet.setDrawCircles(false);
            dataSet.setColor(Color.RED);
            data.addDataSet(dataSet);
            data.setDrawValues(false);
            Log.d("ChartUpdate", "Added LineDataSet");
        }

        Entry newEntry = new Entry(counter, redSum);
        data.addEntry(newEntry, 0);
        Log.d("ChartUpdate", "Added Entry: " + newEntry);
        data.setDrawValues(
                false
        );
        // Notify the chart that the data has changed
        ppgChart.notifyDataSetChanged();

        // Move the chart to the latest entry
        ppgChart.setVisibleXRangeMaximum(10000);  // Adjust as needed
        ppgChart.moveViewToX(counter);
        ppgChart.setData(data);
        Log.d("ChartUpdate", "Notified and moved to X: " + counter);
    }

}
