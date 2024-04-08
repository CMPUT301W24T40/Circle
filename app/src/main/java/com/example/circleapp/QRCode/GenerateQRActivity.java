package com.example.circleapp.QRCode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;

/**
 * This class is used to generate QR codes for a particular event using a unique eventID.
 */
public class GenerateQRActivity extends AppCompatActivity{

    /**
     * Upon the creation of this Activity, a QRCode is generated for the event and displayed.
     * The eventID is encoded into the QR code. The user can share the QR code by clicking the share
     * button. The QR code is saved as a .png file and shared using the file provider.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note:
     *                           Otherwise it is null.</i></b>
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);

        ImageView qrImage = findViewById(R.id.qrImage);
        TextView qrInfo = findViewById(R.id.qr_info);


        Event event = getIntent().getParcelableExtra("event");
        String qrType = getIntent().getStringExtra("qrType");

        String eventID;
        if (event != null) {
            eventID = event.getID();
            String qrText = event.getEventName() + ": " + qrType;
            qrInfo.setText(qrText);
        }
        else { eventID = "No event ID"; }

        Bitmap qrBitmap = generateQRCode(eventID, qrType);
        qrImage.setImageBitmap(qrBitmap);

        Button shareQRbutton = findViewById(R.id.share_QR_button);

        if (qrType != null && qrType.equals("details")) { shareQRbutton.setVisibility(View.VISIBLE); }
        else { shareQRbutton.setVisibility(View.GONE); }

        shareQRbutton.setOnClickListener(v -> shareQRImage(qrBitmap));

    }

    /**
     * This generates a QRCode for an event, using an eventID (the data) encoded into it.
     *
     * @param data      The eventID
     * @param qrType    The type of QR code to be generated
     * @return          Return the bitmap QRCode
     */
    private Bitmap generateQRCode(String data, String qrType){
        QRCodeWriter writer = new QRCodeWriter();
        try {
            String encodedData = qrType + "~" + data;
            BitMatrix bitMatrix = writer.encode(encodedData, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This saves and shares QRCode image. It saves the QRCode as a .png file and shares it using the file provider.
     * Sharing is done through the android share intent. The save location is the cache directory and
     * gets deleted when the app is uninstalled. the saved imaf\ge is overwritten every time.
     *
     * @param bitmap The QRCode bitmap to be shared
     */
    private void shareQRImage(Bitmap bitmap){
        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs();
            File file = new File(cachePath, "QR_Image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            Uri contentUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);

            if (contentUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
            }

        }
        catch (Exception e) { throw new RuntimeException(e); }
    }
}