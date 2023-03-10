package com.example.mywork;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
        int viewId;
        Uri imageURI;
        String currentPhotoPath;

        private static final String TAG = "MyLog";


    ActivityResultLauncher<Intent> startCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Log.e(TAG,"Activity result is ok");
//                        ImageView iv = findViewById(viewId);
//                        if (iv!=null) iv.setImageURI(imageURI);
//                        else Log.e(TAG,"iv variable is not found");

                        ImageView iv = findViewById(R.id.image_input_1);
                        iv.setImageURI(imageURI);
                        Drawable drawable = inputContent.getImageInput1().getDrawable();
                        inputContent.getImageInput2().setImageDrawable(drawable);

                    }
                }
            });




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
            findViewById(R.id.image_input_1),
            findViewById(R.id.image_input_2)
        );

        outputContent = new OutputContent(
            findViewById(R.id.content_output_pdf_layout),
            findViewById(R.id.date_output),
            findViewById(R.id.purpose_output),
            findViewById(R.id.numreport_output),
            findViewById(R.id.image_output_1),
            findViewById(R.id.image_output_2)
        );

        outputContent.getOutputContentPDF().setVisibility(View.GONE);

        day=month=year=0;

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

    }

    private File createImageFile() throws IOException {

        String imageFileName = "PNG_" + viewId;
        File storageDir = new File( Environment.getExternalStorageDirectory()+ "/Documents/PDF/");

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void MakePhoto(View view){
        viewId = view.getId();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException ex) {
                Log.e(TAG,"Exception in createImageFile()");
            }

            if (photoFile != null) {
                imageURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                startCamera.launch(takePictureIntent);
            }
        }
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

        Drawable drawable = inputContent.getImageInput1().getDrawable();
        outputContent.getImageOutput1().setImageDrawable(drawable);

        drawable = inputContent.getImageInput2().getDrawable();
        outputContent.getImageOutput2().setImageDrawable(drawable);

    }

    private void CreatePDFDocument(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float height = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;


        int convertHeight = (int) height, convertWidth = (int) width;

        // ?????????????? ????????????????
        PdfDocument document = new PdfDocument();
        // ???????????????????? ???????????? ????????????????
        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 1).create();
        // ???????????????? ????????????????, ???? ?????????????? ?????????? ???????????????????????? ??????????????
        PdfDocument.Page page = document.startPage(pageInfo);

        // ???????????????? ?????????? (Canvas) ????????????????
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        // ???????????????? ??????????????, ?????????????? ?????????? ???????????????? ?? PDF, ?? ?????????????????? ?????? ?? Bitmap
        Bitmap bitmap = loadBitmapFromView(outputContent.getOutputContentPDF(), outputContent.getOutputContentPDF().getWidth(), outputContent.getOutputContentPDF().getHeight());
        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, true);

        // ???????????? ???????????????????? ?? ?????????????????? ????????????????
        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        File dir = new File(Environment.getExternalStorageDirectory() + "/Documents/PDF");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // ?????????????????? ???????????????????? ??????????????

        String targetPdf = dir.getAbsolutePath() + "/"+outputContent.getNumreportOutput().getText()+"report.pdf";
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
            Toast.makeText(getApplicationContext(), "PDf ???????????????? ?? " + filePath.getAbsolutePath(),
                    Toast.LENGTH_SHORT).show();
            // ?????????????????? ????????????
            //initViews();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            Toast.makeText(this, "??????-???? ?????????? ???? ??????: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // ?????????????????? ????????????????
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