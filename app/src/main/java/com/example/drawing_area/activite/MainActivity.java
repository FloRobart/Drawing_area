package com.example.drawing_area.activite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.drawing_area.R;


public class MainActivity extends AppCompatActivity
{
    public static final String RESUME = "resume";

    private Button btnNewDrawing;
    private Button btnResumeDrawing;
    private Button btnQuit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.btnNewDrawing = findViewById(R.id.btnNewDrawing);
        this.btnResumeDrawing = findViewById(R.id.btnResumeDrawing);
        this.btnQuit = findViewById(R.id.btnQuit);
    }

    public void newDrawing(View view)
    {
        Intent drawingIntent = new Intent(this, DrawingActivity.class);
        drawingIntent.putExtra(MainActivity.RESUME, false);
        this.startActivity(drawingIntent);
    }

    public void resumeDrawing(View view)
    {
        Intent drawingIntent = new Intent(this, DrawingActivity.class);
        drawingIntent.putExtra(MainActivity.RESUME, true);
        this.startActivity(drawingIntent);
    }

    public void quit(View view)
    {
        this.finish();
    }
}