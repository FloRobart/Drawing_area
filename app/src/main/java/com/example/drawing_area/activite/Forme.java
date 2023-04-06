package com.example.drawing_area.activite;

import static java.lang.Math.min;
import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.example.drawing_area.vues.MyColor;


public class Forme
{
    public static final int TYPE_CERCLE = 0;
    public static final int TYPE_LIGNE  = 1;
    public static final int TYPE_RECT   = 2;
    public static final int TYPE_LIBRE  = 3;

    private float xDeb;
    private float yDeb;
    private float xFin;
    private float yFin;
    private float stroke;
    private int type;
    private boolean rempli;
    private MyColor couleur;
    private List<Forme> lstFormes;


    private Forme()
    {
        this(0, 0, 0, 0, 0, 0, false, MyColor.BLACK);
    }

    public Forme(float xDeb, float yDeb, float xFin, float yFin, float stroke, int type, boolean rempli, MyColor couleur)
    {
        this.xDeb = min(xDeb, xFin);
        this.yDeb = min(yDeb, yFin);
        this.xFin = max(xDeb, xFin);
        this.yFin = max(yDeb, yFin);
        this.stroke = stroke;
        this.type = type;
        this.rempli = rempli;
        this.couleur = couleur;

        if (this.type == Forme.TYPE_LIBRE)
            this.lstFormes = new ArrayList<Forme>();
    }

    public Forme(float xDeb, float yDeb, float stroke, int type, boolean rempli, MyColor couleur)
    {
        this(xDeb, yDeb, xDeb, yDeb, stroke, type, rempli, couleur);
    }

    /**
     * Pour la deserialisation
     */
    private Forme(float xDeb, float yDeb, float xFin, float yFin, float stroke, int type, boolean rempli, MyColor couleur, List<Forme> lstFormes,boolean deserialisation)
    {
        this.xDeb = xDeb;
        this.yDeb = yDeb;
        this.xFin = xFin;
        this.yFin = yFin;
        this.stroke = stroke;
        this.type = type;
        this.rempli = rempli;
        this.couleur = couleur;
        this.lstFormes = lstFormes;
    }

    /* Getters */
    public float     getXDeb   () { return xDeb; }
    public float     getYDeb   () { return yDeb; }
    public float     getXFin   () { return xFin; }
    public float     getYFin   () { return yFin; }
    public float     getStroke () { return stroke; }
    public int     getType   () { return type; }
    public boolean isRempli  () { return rempli; }
    public MyColor   getCouleur() { return couleur; }
    public float getRayon() { return (float) Math.sqrt(Math.pow(xFin-xDeb, 2) + Math.pow(yFin-yDeb, 2)); }
    public List<Forme> getLstFormes() { return this.type == Forme.TYPE_LIBRE ? this.lstFormes : null; }

    /* Setters */
    public void setXDeb   (float   xDeb    ) { this.xDeb = xDeb; }
    public void setYDeb   (float   yDeb    ) { this.yDeb = yDeb; }
    public void setXFin   (float   xFin    ) { this.xFin = xFin; }
    public void setYFin   (float   yFin    ) { this.yFin = yFin; }
    public void setStroke (float   stroke  ) { this.stroke = stroke; }
    public void setType   (int   type    ) { this.type = type; }
    public void setRempli (boolean rempli) { this.rempli = rempli; }
    public void setCouleur(MyColor couleur ) { this.couleur = couleur; }
    public void addForme(Forme forme) { if (this.type == Forme.TYPE_LIBRE) this.lstFormes.add(forme); }


    public String serializable()
    {
        String sRet = xDeb + ";" + yDeb + ";" + xFin + ";" + yFin + ";" + stroke + ";" + type + ";" + rempli + ";" + couleur.getIntColor() + ";";
        if (type == Forme.TYPE_LIBRE && this.lstFormes.size() > 0)
        {
            for (int i = 0; i < this.lstFormes.size(); i++) {
                sRet += this.lstFormes.get(i).serializable() + "!";
            }
        }

        return sRet;
    }

    public static Forme deserialisable(String sParam)
    {
        String[] s = sParam.split(";", 9);
        List<Forme> lstFormes = null;

        if (s[8] != null && !s[8].equals(""))
        {
            lstFormes = new ArrayList<>();
            for(String sForme : s[8].split("!"))
                lstFormes.add(Forme.deserialisableFormeLibre(sForme.split(";")));
       }

        return new Forme(Float.parseFloat(s[0]), Float.parseFloat(s[1]), Float.parseFloat(s[2]), Float.parseFloat(s[3]), Float.parseFloat(s[4]), Integer.parseInt(s[5]), Boolean.parseBoolean(s[6]), new MyColor(Integer.parseInt(s[7]), true), lstFormes, true);
    }

    public static Forme deserialisableFormeLibre(String[] s)
    {
        return new Forme(Float.parseFloat(s[0]), Float.parseFloat(s[1]), Float.parseFloat(s[2]), Float.parseFloat(s[3]), Float.parseFloat(s[4]), Integer.parseInt(s[5]), Boolean.parseBoolean(s[6]), new MyColor(Integer.parseInt(s[7]), true), null, true);
    }
}

