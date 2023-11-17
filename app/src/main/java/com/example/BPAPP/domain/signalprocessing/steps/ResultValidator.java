package com.example.BPAPP.domain.signalprocessing.steps;

import android.util.Log;

import com.example.BPAPP.domain.signalprocessing.exception.FrameProcessingException;

public class ResultValidator implements Step {

    private static final String TAG = ResultValidator.class.getName();

    @Override
    public int[] invoke(int[] signal) {
        Log.i(TAG, "Running result validation - number of maxima: " + signal.length);
        validate(signal);
        return signal;
    }

    private void validate(int[] signal) {
        if (signal.length < 2) {
            throw new FrameProcessingException("Not enough maxima to calculate with");
        }
    }
}