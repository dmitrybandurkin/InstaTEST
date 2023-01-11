package com.example.mywork;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class OutputContent {
    private ConstraintLayout outputContentPDF;
    private TextView dateOutput;
    private TextView purposeOutput;
    private TextView numreportOutput;

    public OutputContent(ConstraintLayout outputContentPDF, TextView dateOutput, TextView purposeOutput, TextView numreportOutput) {
        this.outputContentPDF = outputContentPDF;
        this.dateOutput = dateOutput;
        this.purposeOutput = purposeOutput;
        this.numreportOutput=numreportOutput;
    }

    public ConstraintLayout getOutputContentPDF() {
        return outputContentPDF;
    }

    public TextView getDateOutput() {
        return dateOutput;
    }


    public TextView getPurposeOutput() {
        return purposeOutput;
    }

    public TextView getNumreportOutput() {
        return numreportOutput;
    }
}
