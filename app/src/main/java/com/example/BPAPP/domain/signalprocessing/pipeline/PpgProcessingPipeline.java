package com.example.BPAPP.domain.signalprocessing.pipeline;

import com.example.BPAPP.domain.signalprocessing.steps.Derivation;
import com.example.BPAPP.domain.signalprocessing.steps.MaximaCalculator;
import com.example.BPAPP.domain.signalprocessing.steps.Preprocessor;
import com.example.BPAPP.domain.signalprocessing.steps.ResultValidator;
import com.example.BPAPP.domain.signalprocessing.steps.RollingAverage;
import com.example.BPAPP.domain.signalprocessing.steps.filter.GaussianBlur;
import com.example.BPAPP.domain.signalprocessing.steps.filter.LowPassFilter;

public class PpgProcessingPipeline {

    public static Pipeline pipeline() {
        return new Pipeline(new Preprocessor())
                .pipe(new RollingAverage())
                .pipe(new LowPassFilter(30))
                .pipe(new GaussianBlur())
                .pipe(new Derivation())
                .pipe(new MaximaCalculator())
                .pipe(new ResultValidator());
    }
}
