package com.example.mywork;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class InputContent extends AppCompatActivity {
    //content_input
    private ConstraintLayout inputContentPDF;
    private EditText dateInput;
    private EditText purposeInput;
    private Button generatePDFbutton;
    private EditText numreportInput;
    private ImageView image1;
    private ImageView image2;

    public InputContent(ConstraintLayout inputContentPDF, EditText dateInput, EditText purposeInput, Button generatePDFbutton, EditText numreportInput, ImageView image1, ImageView image2) {
        this.inputContentPDF = inputContentPDF;
        this.dateInput = dateInput;
        this.purposeInput = purposeInput;
        this.generatePDFbutton = generatePDFbutton;
        this.numreportInput = numreportInput;
        this.image1 = image1;
        this.image2 = image2;
    }

    public ConstraintLayout getInputContentPDF() {
        return inputContentPDF;
    }

    public EditText getDateInput() {
        return dateInput;
    }

    public ImageView getImage1() {
        return image1;
    }

    public ImageView getImage2() {
        return image2;
    }

    public EditText getPurposeInput() {
        return purposeInput;
    }

    public Button getGeneratePDFbutton() {
        return generatePDFbutton;
    }

    public EditText getNumreportInput() {
        return numreportInput;
    }
}
