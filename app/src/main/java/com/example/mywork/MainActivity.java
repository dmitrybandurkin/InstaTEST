package com.example.mywork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText dateText;
    private EditText purposeText;
    final Context context = this;
    int day=0, month=0, year=2023;
    private Button pdfCreation;
    private ConstraintLayout cf_pdf;
    private ConstraintLayout cr_pdf;
    private Button mybutton;

    private TextView resDate;
    private TextView resPurpose;



    private static final String TAG = "MyApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateText = findViewById(R.id.testDate);
        purposeText = findViewById(R.id.purposeEdit);
        pdfCreation = findViewById(R.id.createPDF);
        cf_pdf = findViewById(R.id.cf_pdflayout);
        cr_pdf = findViewById(R.id.cr_pdflayout);

        mybutton = findViewById(R.id.mybutton);
        resDate = findViewById(R.id.ResultDate);
        resPurpose = findViewById(R.id.ResultPurpose);

        cr_pdf.setVisibility(View.GONE);


        //pdf_layout=findViewById(R.id.cl_pdflayout);

        mybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreatePDFDocument();
            }
        });

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicketDialog();
            }
        });

        purposeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPurposeDialog();
            }
        });

        pdfCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneratePDFView();
                //CreatePDFDocument();
            }
        });
    }


    private void GeneratePDFView(){
        cf_pdf.setVisibility(View.GONE);
        cr_pdf.setVisibility(View.VISIBLE);
        resDate.setText(dateText.getText());
        resPurpose.setText(purposeText.getText());

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
        Bitmap bitmap = loadBitmapFromView(cr_pdf, cr_pdf.getWidth(), cr_pdf.getHeight());
        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, true);

        // рисуем содержимое и закрываем страницу
        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        File dir = new File(Environment.getExternalStorageDirectory() + "/Documents/PDF");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (dir.exists()) Log.e(TAG, "OK");
        else Log.e(TAG, "NOK");

        // сохраняем записанный контент
        Log.e(TAG, dir.getAbsolutePath());
        String targetPdf = dir.getAbsolutePath() + "/test.pdf";
        File filePath = new File(targetPdf);
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
        LayoutInflater li = LayoutInflater.from(context);
        View purposeDialogView = li.inflate(R.layout.purpose_dialog, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
        mDialogBuilder.setView(purposeDialogView);

        final EditText purposeInput = (EditText) purposeDialogView.findViewById(R.id.purposeEdit);
        purposeInput.setText(purposeText.getText());


        mDialogBuilder.setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                purposeText.setText(purposeInput.getText());
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
        dateText.setText(date);
    }
}