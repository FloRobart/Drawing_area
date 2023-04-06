package com.example.drawing_area.activite;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import com.example.drawing_area.R;
import com.example.drawing_area.colorPicker.ColorPicker;
import com.example.drawing_area.colorPicker.ColorPickerCallback;
import com.example.drawing_area.vues.DrawingView;
import com.example.drawing_area.vues.MyColor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class DrawingActivity extends Activity implements SeekBar.OnSeekBarChangeListener, ColorPickerCallback
{
    public static final String FILE_NAME = "drawingArea.txt";
    public static final int STATIC_COLOR = 0;
    public static final int ALEATOIRE_COLOR = 1;

    private DrawingView drawingView;
    private boolean resume;

    /* Couleurs */
    private Button btnColorPicker;
    private MyColor colorSelected;
    private int typeCouleur;
    private boolean degrader;
    private ColorPicker colorPicker;

    /* Formes */
    private TextView tvStroke;

    private List<Forme> lstFormes;
    private List<Forme> lstFormesSupprimer;

    private int typeForme;
    private boolean rempli;
    private int stroke;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.lstFormes = new ArrayList<>();
        this.lstFormesSupprimer = new ArrayList<>();
        setContentView(R.layout.activity_drawing);
        this.drawingView = (DrawingView) findViewById(R.id.drawingArea);
        this.resume = this.getIntent().getBooleanExtra(MainActivity.RESUME, false);

        /* Couleurs */
        this.btnColorPicker = (Button) findViewById(R.id.btnColorPicker);
        this.colorSelected = new MyColor(MyColor.getIntRandomColorWithA(255), true);
        this.typeCouleur = ALEATOIRE_COLOR;
        this.degrader = false;
        this.colorPicker = new ColorPicker(this, this.colorSelected.getA(), this.colorSelected.getR(), this.colorSelected.getG(), this.colorSelected.getB());
        this.colorPicker.enableAutoClose();
        this.colorPicker.setCallback(this);


        /* Formes */
        this.tvStroke = (TextView) findViewById(R.id.tvStroke);
        SeekBar sbStroke = (SeekBar) findViewById(R.id.sbStroke);

        this.typeForme = Forme.TYPE_LIGNE;
        this.rempli = false;
        this.stroke = 1;

        /* Initialisation de la zone de dessin */
        if (this.resume)
            this.resumeDrawingArea();


        /* Listener pour le SeekBar */
        sbStroke.setOnSeekBarChangeListener(this);
    }

    /*==========================*/
    /* Gestion de la sauvegarde */
    /*==========================*/
    /**
     * Permet de reprendre la zone de dessin
     * Cette méthode charge les listes de formes en fonction du fichier
     */
    private void resumeDrawingArea()
    {
        try
        {
            FileInputStream inputStream = openFileInput(DrawingActivity.FILE_NAME);
            byte[] buffer = new byte[1024];
            int n;
            String s = "";
            while ((n = inputStream.read(buffer)) != -1)
                s += new String(buffer, 0, n, StandardCharsets.UTF_8);

            inputStream.close();

            /* Séparation des listes */
            String[] sListsFormes = s.split("CHANGEMENT DE LISTE");

            /* Chargement de la liste des formes */
            if (sListsFormes.length > 0)
            {
                String[] sLstFormes = sListsFormes[0].split(":");
                for (int i = 0; i < sLstFormes.length; i++)
                    if (!sLstFormes[i].equals(""))
                        this.lstFormes.add(Forme.deserialisable(sLstFormes[i]));
            }

            /* Chargement de la liste des formes supprimées */
            if (sListsFormes.length > 1)
            {
                String[] sLstFormesSupprimer = sListsFormes[1].split(":");
                for (int i = 0; i < sLstFormesSupprimer.length; i++)
                    if (!sLstFormesSupprimer[i].equals(""))
                        this.lstFormesSupprimer.add(Forme.deserialisable(sLstFormesSupprimer[i]));
            }
        }
        catch (IOException e) { e.printStackTrace(); System.out.println("Erreur lors de la lecture du fichier"); }
    }

    /**
     * Permet de sauvegarder la zone de dessin
     * Cette méthode sauvegarde les listes de formes dans un fichier
     */
    private void saveDrawingArea()
    {
        try
        {
            FileOutputStream outputStream=openFileOutput(DrawingActivity.FILE_NAME, Context.MODE_PRIVATE);
            for (int i = 0; i < this.lstFormes.size(); i++)
                outputStream.write((this.lstFormes.get(i).serializable() + ":").getBytes(StandardCharsets.UTF_8));

            outputStream.write("CHANGEMENT DE LISTE".getBytes(StandardCharsets.UTF_8));

            for (int i = 0; i < this.lstFormesSupprimer.size(); i++)
                outputStream.write((this.lstFormesSupprimer.get(i).serializable() + ":").getBytes(StandardCharsets.UTF_8));

            outputStream.close();
        } catch (IOException e) { e.printStackTrace(); System.out.println("Erreur lors de l'écriture du fichier"); }
    }

    @Override
    public void onPause()
    {
        this.saveDrawingArea();
        super.onPause();
    }

    /**
     * Permet de savoir si on doit reprendre un dessin ou pas
     * @return true si on doit reprendre un dessin, false sinon
     */
    public boolean isResume() { return resume; }



    /*========================*/
    /* Gestion de la rotation */
    /*========================*/
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        /* Sauvegarde des listes */
        for (int i = 0; i < this.lstFormes.size(); i++)
            savedInstanceState.putString("forme" + i, this.lstFormes.get(i).serializable());

        for (int i = 0; i < this.lstFormesSupprimer.size(); i++)
            savedInstanceState.putString("formeSupp" + i, this.lstFormesSupprimer.get(i).serializable());


        /* Tout les attribbuts */
        savedInstanceState.putInt("colorSelected", this.colorSelected.getIntColor());
        savedInstanceState.putInt("typeCouleur", this.typeCouleur);
        savedInstanceState.putBoolean("degrader", this.degrader);
        savedInstanceState.putInt("typeForme", this.typeForme);
        savedInstanceState.putBoolean("rempli", this.rempli);
        savedInstanceState.putInt("stroke", this.stroke);

        /* Sauvegarde */
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        /* Restauration */
        super.onRestoreInstanceState(savedInstanceState);


        /* Restauration des listes */
        for (int i = 0; i < savedInstanceState.size(); i++)
        {
            if (savedInstanceState.containsKey("forme" + i))
                this.lstFormes.add(Forme.deserialisable(savedInstanceState.getString("forme" + i)));

            if (savedInstanceState.containsKey("formeSupp" + i))
                this.lstFormesSupprimer.add(Forme.deserialisable(savedInstanceState.getString("formeSupp" + i)));
        }


        /* Tout les attributs */
        this.colorSelected = new MyColor(savedInstanceState.getInt("colorSelected"), true);
        this.typeCouleur = savedInstanceState.getInt("typeCouleur");
        this.degrader = savedInstanceState.getBoolean("degrader");
        this.typeForme = savedInstanceState.getInt("typeForme");
        this.rempli = savedInstanceState.getBoolean("rempli");
        this.stroke = savedInstanceState.getInt("stroke");
    }



    /*===================*/
    /* Gestion des menus */
    /*===================*/
    /**
     * Permet de récupérer la liste des formes
     * @return liste des formes
     */
    public List<Forme> getLstFormes() { return this.lstFormes; }

    /**
     * Permer d'annuler la dernière création de forme
     * @param v vue qui a appelé la méthode
     */
    public void annuler(View v)
    {
        if (this.lstFormes.size() > 0)
        {
            Forme f = this.lstFormes.get(this.lstFormes.size() - 1);
            if (f.getCouleur().isGradient())
                f.getCouleur().stopGradient();

            this.lstFormesSupprimer.add(f);
            this.lstFormes.remove(f);
            this.drawingView.invalidate();
        }
    }

    /**
     * Permet de récupérer la dernière formes supprimées
     * @param v vue qui a appelé la méthode
     */
    public void restorer(View v)
    {
        if (this.lstFormesSupprimer.size() > 0)
        {
            Forme f = this.lstFormesSupprimer.get(this.lstFormesSupprimer.size() - 1);
            if (f.getCouleur().canGradient())
                f.getCouleur().startGradientAuto(1, this.drawingView, true);

            this.lstFormes.add(f);
            this.lstFormesSupprimer.remove(f);
            this.drawingView.invalidate();
        }
    }

    /**
     * Permet de supprimer tout se qui a été dessiné
     * @param v vue qui a appelé la méthode
     */
    public void annulerTout(View v)
    {
        this.lstFormesSupprimer.addAll(this.lstFormes);
        this.lstFormes.clear();
        this.drawingView.invalidate();
        for (Forme f : this.lstFormesSupprimer)
            if (f.getCouleur().isGradient())
                f.getCouleur().stopGradient();
    }

    /**
     * Permet de restaurer tout se qui a été supprimé
     * @param v vue qui a appelé la méthode
     */
    public void restorerTout(View v)
    {
        this.lstFormes.addAll(this.lstFormesSupprimer);
        this.lstFormesSupprimer.clear();
        this.drawingView.invalidate();

        for (Forme f : this.lstFormes)
            if (f.getCouleur().canGradient())
                f.getCouleur().startGradientAuto(1, this.drawingView, true);
    }

    /**
     * Permet de quitter l'activité
     * @param v vue qui a appelé la méthode
     */
    public void quitDrawingArea(View v)
    {
        this.finish();
    }



    /*======================*/
    /* Gestion des Couleurs */
    /*======================*/
    public void setTypeColor(View v)
    {
        this.typeCouleur = v.getId() == R.id.btnAleatoireColor ? DrawingActivity.ALEATOIRE_COLOR : DrawingActivity.STATIC_COLOR;

        if (this.typeCouleur == DrawingActivity.STATIC_COLOR)
            this.colorPicker.show();
    }

    /**
     * Permet de changer le fait de devoir faire des dégrader ou pas
     * @param v vue qui a appelé la méthode
     */
    public void setDegrader(View v)
    {
        this.degrader = !this.degrader;
    }

    /**
     * Permet de savoir si on doit dégrader les couleurs ou pas
     * @return true si on doit dégrader les couleurs, false sinon
     */
    public boolean isDegrader() { return this.degrader; }

    /**
     * Permet de recuperer la couleur sélectionnée (si elle est fixe)
     * @return couleur sélectionnée
     */
    public MyColor getColorSelected() { return this.colorSelected; }

    /**
     * Permet de récuperer le type de couleur (couleur aléatoire ou couleur fixe)
     * @return type de couleur
     */
    public int getTypeCouleur() { return this.typeCouleur; }



    /*====================*/
    /* Gestion des Formes */
    /*====================*/
    /**
     * Permet de changer le type de forme
     * @param v vue qui a appelé la méthode
     */
    public void changeTypeForme(View v)
    {
        switch (v.getId())
        {
            case R.id.btnLigne:
                this.typeForme = Forme.TYPE_LIGNE;
                break;
            case R.id.btnRectangle:
                this.typeForme = Forme.TYPE_RECT;
                break;
            case R.id.btnCercle:
                this.typeForme = Forme.TYPE_CERCLE;
                break;
            case R.id.btnLibre:
                this.typeForme = Forme.TYPE_LIBRE;
                break;
        }
    }

    /**
     * Permet de changer le remplissage de la forme
     * @param v vue qui a appelé la méthode
     */
    public void setRempli(View v) { this.rempli = !this.rempli; }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b)
    {
        this.stroke = i;
        runOnUiThread(() -> tvStroke.setText(""+i));
    }

    /**
     * Permet de récupérer le type de forme sélectionné
     * @return type de forme sélectionné
     */
    public int getTypeForme() { return this.typeForme; }

    /**
     * Permet de savoir si la forme qui va être créée doit être remplie ou non
     * @return true si la forme doit être remplie, false sinon
     */
    public boolean getRempli() { return this.rempli; }

    /**
     * Permet de récupérer l'épaisseur des traits de la forme qui va être créée
     * @return épaisseur des traits
     */
    public int getStroke() { return this.stroke; }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}


    @Override
    public void onColorChosen(@ColorInt int color)
    {
        this.colorSelected = new MyColor(this.colorPicker.getColor(), true);
    }
}