package com.example.mywork;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class OutputContent {
    private ConstraintLayout outputContentPDF;
    private TextView dateOutput;
    private TextView purposeOutput;
    private TextView numreportOutput;
    private ImageView imageOutput1;
    private ImageView imageOutput2;

    public OutputContent(ConstraintLayout outputContentPDF, TextView dateOutput, TextView purposeOutput, TextView numreportOutput, ImageView image1, ImageView image2) {
        this.outputContentPDF = outputContentPDF;
        this.dateOutput = dateOutput;
        this.purposeOutput = purposeOutput;
        this.numreportOutput=numreportOutput;
        this.imageOutput1=image1;
        this.imageOutput2=image2;
    }

    public ImageView getImageOutput1() {
        return imageOutput1;
    }

    public ImageView getImageOutput2() {
        return imageOutput2;
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
