package com.example.drawing_area.vues;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.drawing_area.activite.DrawingActivity;
import com.example.drawing_area.activite.Forme;

import java.util.List;


public class DrawingView extends View implements View.OnClickListener, View.OnTouchListener, GradientInterface
{
    /* Gestion du dégrader */
    private MyColor[] colors   = new MyColor[]{new MyColor(255,0,0), new MyColor(255,165,0), new MyColor(255,255,0), new MyColor(0,128,0), new MyColor(0,0,255), new MyColor(75,0,130), new MyColor(238,130,238), new MyColor(100,0,100)};

    private DrawingActivity parent;
    private Forme currentShape;
    private List<Forme> lstFormes;
    private Paint paint;


    @SuppressLint("ClickableViewAccessibility")
    public DrawingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.parent = (DrawingActivity) context;

        this.currentShape = null;
        this.lstFormes = this.parent.getLstFormes();
        this.paint = new Paint();
        this.paint.setAntiAlias(true);


        this.setOnClickListener(this);
        this.setOnTouchListener(this);
    }

    @Override
    public void onDraw(Canvas c)
    {
        super.onDraw(c);

        for (Forme f : this.lstFormes)
        {
            if (f.isRempli() && f.getType() != Forme.TYPE_LIGNE)
            {
                this.paint.setStyle(Paint.Style.FILL);
            }
            else
            {
                this.paint.setStyle(Paint.Style.STROKE);
                this.paint.setStrokeWidth(f.getStroke());
            }
            this.paint.setColor(f.getCouleur().getIntColor());

            if (f.getType() == Forme.TYPE_LIGNE)
            {
                c.drawLine(f.getXDeb(), f.getYDeb(), f.getXFin(), f.getYFin(), this.paint);
            }
            else if (f.getType() == Forme.TYPE_CERCLE)
            {
                c.drawCircle(f.getXDeb(), f.getYDeb(), f.getRayon(), this.paint);
            }
            else if (f.getType() == Forme.TYPE_RECT)
            {
                c.drawRect(f.getXDeb(), f.getYDeb(), f.getXFin(), f.getYFin(), this.paint);
            }
            else if (f.getType() == Forme.TYPE_LIBRE)
            {
                this.paint.setStyle(Paint.Style.FILL);
                for (Forme f2 : f.getLstFormes())
                {
                    c.drawCircle(f2.getXDeb(), f2.getYDeb(), f2.getRayon(), this.paint);
                }
            }
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent me)
    {
        /* Modification */
        if (me.getAction() == MotionEvent.ACTION_MOVE)
        {
            if (this.parent.getTypeForme() == Forme.TYPE_LIBRE)
            {
                this.currentShape.addForme(new Forme(me.getX(), me.getY(), me.getX() + this.parent.getStroke(), me.getY() + this.parent.getStroke(), 0, Forme.TYPE_CERCLE, true, this.parent.getColorSelected()));
            }
            else
            {
                this.currentShape.setXFin(me.getX());
                this.currentShape.setYFin(me.getY());
            }
        }
        /* Création */
        else if (me.getAction() == MotionEvent.ACTION_DOWN)
        {
            MyColor color = this.parent.getTypeCouleur() == DrawingActivity.ALEATOIRE_COLOR ? new MyColor(MyColor.getIntRandomColorWithA(255), true) : this.parent.getColorSelected();
            if (this.parent.getTypeForme() != Forme.TYPE_LIBRE)
            {
                if (this.parent.isDegrader())
                {
                    color.setParent(this);
                    color.startGradientAuto(1, this, true);
                }

                this.currentShape = new Forme(me.getX(), me.getY(), (float)this.parent.getStroke(), this.parent.getTypeForme(), this.parent.getRempli(), color);
                this.lstFormes.add(this.currentShape);
            }
            else
            {
                this.currentShape = new Forme(0, 0, 0, 0, 0, Forme.TYPE_LIBRE, true, color);
                this.currentShape.addForme(new Forme(me.getX(), me.getY(), me.getX() + this.parent.getStroke(), me.getY() + this.parent.getStroke(), 0, Forme.TYPE_CERCLE, true, color));
                this.lstFormes.add(this.currentShape);
            }

        }
        /* Fin de modification */
        else if (me.getAction() == MotionEvent.ACTION_UP)
        {
            if (this.parent.getTypeForme() != Forme.TYPE_LIBRE)
            {
                this.currentShape.setXFin(me.getX());
                this.currentShape.setYFin(me.getY());
                this.currentShape = null;
            }
        }

        this.invalidate();
        return false;
    }

    @Override
    public void onClick(View view) {}



    /*=====================*/
    /* Gestion du dégrader */
    /*=====================*/
    @Override
    public MyColor[] getColors() { return this.colors; }
    @Override
    public MyColor getColorAt(int indColor) { if (indColor < 0 || indColor > this.colors.length-1) return null; return this.colors[indColor]; }
}
