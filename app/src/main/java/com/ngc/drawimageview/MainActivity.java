package com.ngc.drawimageview;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    LinearLayout mDrawingPad;
    Button loadImage, undoButton, clearButton, nextButton;
    DrawingView mDrawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        final DrawingView mDrawingView = new DrawingView(this);

        mDrawingView = new DrawingView(this);
        setContentView(R.layout.activity_main);

        mDrawingPad = (LinearLayout) findViewById(R.id.view_drawing_pad);
        mDrawingPad.addView(mDrawingView);
        loadImage = (Button) findViewById(R.id.load_image_button);
        loadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setImagefrmGallery();
            }
        });

        undoButton = (Button) findViewById(R.id.undo_button);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawingView.onClickUndo();
            }
        });

        clearButton = (Button) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawingView.onClickClear();
            }
        });

        nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //Write file
                    String filename = "bitmap.png";
                    FileOutputStream stream = MainActivity.this.openFileOutput(filename, Context.MODE_PRIVATE);
                    Bitmap bmp = mDrawingView.getBitmap();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    //Cleanup
                    stream.close();
                    bmp.recycle();

                    //Pop intent
                    Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                    intent.putExtra("image", filename);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void setImagefrmGallery() {
        // To open up a gallery browser
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        // To handle when an image is selected from the browser, add the following to your Activity
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(this.getResources(), bitmap);
//                mDrawingPad.setBackground(bitmapDrawable);
                mDrawingView.setBackground(bitmapDrawable);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
