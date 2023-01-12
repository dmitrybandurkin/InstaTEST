package com.example.mywork;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;



public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

        InputContent inputContent;
        OutputContent outputContent;
        MenuItem createPDFButton;
        int day,month,year;

        private static final String TAG = "MyApp";

        ActivityResultLauncher<Intent> startCamera;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputContent = new InputContent(
            findViewById(R.id.content_input_pdf_layout),
            findViewById(R.id.date_input),
            findViewById(R.id.purpose_input),
            findViewById(R.id.generate_pdf_content_button),
            findViewById(R.id.numreport_input),
            findViewById(R.id.image_input),
            findViewById(R.id.image_input_2)
        );

        outputContent = new OutputContent(
            findViewById(R.id.content_output_pdf_layout),
            findViewById(R.id.date_output),
            findViewById(R.id.purpose_output),
            findViewById(R.id.numreport_output)
        );

        outputContent.getOutputContentPDF().setVisibility(View.GONE);

        day=month=year=0;

//        startCamera = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if(result.getResultCode() == RESULT_OK){
//                            Log.e(TAG,"Activity Ok");
//                        }
//                    }
//                }
//        );

        startCamera = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                new ActivityResultCallback<Bitmap>() {
                    @Override
                    public void onActivityResult(Bitmap result) {
                            Log.e(TAG,"Activity Ok");
                    }
                }
        );

        inputContent.getDateInput().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicketDialog();
            }
        });

        inputContent.getPurposeInput().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPurposeDialog();
            }
        });

        inputContent.getGeneratePDFbutton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneratePDFView();
            }
        });



        inputContent.getImage1().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startCamera.launch(cameraIntent);
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pdfgenerator_menu, menu);
        createPDFButton=menu.findItem(R.id.create_pdf_button);
        createPDFButton.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.create_pdf_button:
                CreatePDFDocument();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void GeneratePDFView(){
        inputContent.getInputContentPDF().setVisibility(View.GONE);
        outputContent.getOutputContentPDF().setVisibility(View.VISIBLE);
        createPDFButton.setVisible(true);
        outputContent.getDateOutput().setText(inputContent.getDateInput().getText());
        outputContent.getPurposeOutput().setText(inputContent.getPurposeInput().getText());
        outputContent.getNumreportOutput().setText(inputContent.getNumreportInput().getText());
    }

    private void CreatePDFDocument(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float height = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;

        int convertHeight = (int) height, convertWidth = (int) width;

        // создаем документ
        PdfDocument document = new PdfDocument();
        // определяем размер страницы
        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 1).create();
        // получаем страницу, на котором будем генерировать контент
        PdfDocument.Page page = document.startPage(pageInfo);

        // получаем холст (Canvas) страницы
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        // получаем контент, который нужно добавить в PDF, и загружаем его в Bitmap
        Bitmap bitmap = loadBitmapFromView(outputContent.getOutputContentPDF(), outputContent.getOutputContentPDF().getWidth(), outputContent.getOutputContentPDF().getHeight());
        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, true);

        // рисуем содержимое и закрываем страницу
        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        File dir = new File(Environment.getExternalStorageDirectory() + "/Documents/PDF");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // сохраняем записанный контент

        String targetPdf = dir.getAbsolutePath() + "/"+outputContent.getNumreportOutput().getText()+".pdf";
        File filePath = new File(targetPdf);
        if (filePath.exists()){
            int i=1;
            while (filePath.exists()){
                targetPdf = dir.getAbsolutePath() + "/"+outputContent.getNumreportOutput().getText()+"_"+String.valueOf(i)+".pdf";
                filePath = new File(targetPdf);
                i++;
            }
        }

        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(getApplicationContext(), "PDf сохранён в " + filePath.getAbsolutePath(),
                    Toast.LENGTH_SHORT).show();
            // обновляем список
            //initViews();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            Toast.makeText(this, "Что-то пошло не так: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // закрываем документ
        document.close();
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    private  void showPurposeDialog(){
        LayoutInflater li = LayoutInflater.from(this);
        View purposeDialogView = li.inflate(R.layout.purpose_dialog, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder.setView(purposeDialogView);

        final EditText purposeDialogInput = (EditText) purposeDialogView.findViewById(R.id.purpose_dialog_input);
        purposeDialogInput.setText(inputContent.getPurposeInput().getText());


        mDialogBuilder.setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                inputContent.getPurposeInput().setText(purposeDialogInput.getText());
                            }
                        });
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }

    private void showDatePicketDialog(){
        if (day == 0){
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    this,
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        }
        else{
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    this,
                    year,
                    month,
                    day
            );
            datePickerDialog.show();
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int _year, int _month, int _dayOfMonth) {
        day = _dayOfMonth;
        month = _month;
        year = _year;
        String date = String.valueOf(day) + "." + String.valueOf(month+1) + "." + String.valueOf(year);
        inputContent.getDateInput().setText(date);
    }
}