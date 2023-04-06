package com.example.drawing_area.vues;

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.view.View;

import java.util.Random;


public class MyColor<T extends GradientInterface>
{
    public static final MyColor BLACK = new MyColor(255, 0, 0, 0);

    private static final int MAX_PAS_INCREMENT = 100;
    private static final int MIN_PAS_INCREMENT = 0;

    private int       indColor = 0;
    private double    percent  = 0;

    private int a;
    private int r;
    private int g;
    private int b;

    /* Pour le dégrader */
    private T parent;

    /* Pour le clignotement */
    private Thread threadFlashing = null;
    private boolean flashingIsAlive = false;

    /* Pour le dégrader unique */
    private Thread threadSingleGradient = null;

    /* Pour le dégrader automatique */
    private Thread threadAutoGradient = null;
    private boolean increment = true;
    private boolean gradientIsAlive = false;
    private boolean canGradient = false;


    /*------------------------------------------------*/
    /* Contructeur sans class parent (couleur simple) */
    /*------------------------------------------------*/
    /**
     * Constructeur
     * @param a alpha [ 0 ; 255 ]
     * @param r rouge [ 0 ; 255 ]
     * @param g vert  [ 0 ; 255 ]
     * @param b bleu  [ 0 ; 255 ]
     */
    public MyColor(int a, int r, int g, int b)
    {
        this.a = a;
        this.r = r;
        this.g = g;
        this.b = b;

        this.parent = null;
    }

    /**
     * Constructeur avec un alpha à 255
     * @param r rouge [ 0 ; 255 ]
     * @param g vert  [ 0 ; 255 ]
     * @param b bleu  [ 0 ; 255 ]
     */
    public MyColor(int r, int g, int b)
    {
        this(255, r, g, b);
    }

    /**
     * Constructeur à partir d'un int
     * @param argb Couleur ARGB ou RGB (selon le paramètre isArgb) sur un int
     * @param isArgb Si true, alors la couleur est de type ARGB, sinon, la couleur est du type RGB (le alpha sera défini sur 255)
     */
    public MyColor(int argb, boolean isArgb)
    {
        this.setIntToArgb(argb, isArgb);

        this.parent = null;
    }

    /**
     * Constructeur par copie
     * @param mc MyColor à copier
     */
    public MyColor(MyColor mc)
    {
        this.a = mc.a;
        this.r = mc.r;
        this.g = mc.g;
        this.b = mc.b;

        if (mc.parent != null)
            this.parent = (T) mc.parent;
    }


    /*--------------------------------*/
    /* Constructeur avec class parent */
    /*--------------------------------*/
    /**
     * Constructeur
     * @param a alpha [ 0 ; 255 ]
     * @param r rouge [ 0 ; 255 ]
     * @param g vert  [ 0 ; 255 ]
     * @param b bleu  [ 0 ; 255 ]
     * @param c Class parent
     */
    public MyColor(int a, int r, int g, int b, T c)
    {
        this.a = a;
        this.r = r;
        this.g = g;
        this.b = b;

        this.parent = c;
    }

    /**
     * Constructeur avec un alpha à 255
     * @param r rouge [ 0 ; 255 ]
     * @param g vert  [ 0 ; 255 ]
     * @param b bleu  [ 0 ; 255 ]
     * @param c Class parent
     */
    public MyColor(int r, int g, int b, T c)
    {
        this(255, r, g, b, c);
    }

    /**
     * Constructeur à partir d'un int
     * @param argb Couleur ARGB ou RGB (selon le paramètre isArgb) sur un int
     * @param isArgb Si true, alors la couleur est de type ARGB, sinon, la couleur est du type RGB (le alpha sera défini sur 255)
     * @param c Class parent
     */
    public MyColor(int argb, boolean isArgb, T c)
    {
        this.setIntToArgb(argb, isArgb);

        this.parent = c;
    }

    /**
     * Constructeur par copie avec une nouvelle classe parente
     * @param mc MyColor à copier (Attention la classe parente n'est pas copiée).
     * @param c Nouvelle classe parente de la couleur (pour le dégrader).
     */
    public MyColor(MyColor mc, T c)
    {
        this.a = mc.a;
        this.r = mc.r;
        this.g = mc.g;
        this.b = mc.b;

        this.parent = c;
    }



    /*---------*/
    /* Getters */
    /*---------*/
    /**
     * Permet de récupérer uniquement la partie alpha de la couleur
     * @return Alpha de la couleur
     */
    public int getA() { return a; }

    /**
     * Permet de récupérer uniquement la partie rouge de la couleur
     * @return Rouge de la couleur
     */
    public int getR() { return r; }

    /**
     * Permet de récupérer uniquement la partie verte de la couleur
     * @return Vert de la couleur
     */
    public int getG() { return g; }

    /**
     * Permet de récupérer uniquement la partie bleue de la couleur
     * @return Bleu de la couleur
     */
    public int getB() { return b; }

    /**
     * Permet de récupérer la couleur sous forme d'un int
     * @return Couleur ARGB sur un int
     */
    public int getIntColor() { return this.argbToInt(this.a, this.r, this.g, this.b); }

    /**
     * Permet de récupérer la classe parente de la couleur
     * @return Classe parente de la couleur
     */
    public T getParent() { return this.parent; }



    /*---------*/
    /* Setters */
    /*---------*/
    /**
     * Permet de définir uniquement la partie alpha de la couleur.
     * Si la valeur est inférieure à 0, alors elle est mise à 0.
     * Si la valeur est supérieure à 255, alors elle est mise à 255.
     * @param a Alpha de la couleur [ 0 ; 255 ]
     */
    public void setA(int a) { a = max(a, 0); a = min(a, 255); this.a = a; }

    /**
     * Permet de définir uniquement la partie rouge de la couleur.
     * Si la valeur est inférieure à 0, alors elle est mise à 0.
     * Si la valeur est supérieure à 255, alors elle est mise à 255.
     * @param r Rouge de la couleur [ 0 ; 255 ]
     */
    public void setR(int r) { r = max(r, 0); r = min(r, 255); this.r = r; }

    /**
     * Permet de définir uniquement la partie verte de la couleur.
     * Si la valeur est inférieure à 0, alors elle est mise à 0.
     * Si la valeur est supérieure à 255, alors elle est mise à 255.
     * @param g Vert de la couleur [ 0 ; 255 ]
     */
    public void setG(int g) { g = max(g, 0); g = min(g, 255); this.g = g; }

    /**
     * Permet de définir uniquement la partie bleue de la couleur.
     * Si la valeur est inférieure à 0, alors elle est mise à 0.
     * Si la valeur est supérieure à 255, alors elle est mise à 255.
     * @param b Bleu de la couleur [ 0 ; 255 ]
     */
    public void setB(int b) { b = max(b, 0); b = min(b, 255); this.b = b; }

    /**
     * Permet de modifier les attribut ARGB d'un coup.
     * Si les valeurs sont en dehors de la plage [ 0 ; 255 ] elles seront ramenées au nombre le plus près dans la plage [ 0 ; 255 ]
     * @param a Alpha de la couleur [ 0 ; 255 ]
     * @param r Rouge de la couleur [ 0 ; 255 ]
     * @param g Vert de la couleur  [ 0 ; 255 ]
     * @param b Bleu de la couleur  [ 0 ; 255 ]
     */
    public void setARGB(int a, int r, int g, int b)
    {
        this.setA(a);
        this.setR(r);
        this.setG(g);
        this.setB(b);
    }

    /**
     * Permet de modifier les attribut RGB d'un coup sans modifier l'attribut A.
     * Si les valeurs sont en dehors de la plage [ 0 ; 255 ] elles seront ramenées au nombre le plus près dans la plage [ 0 ; 255 ]
     * @param r Rouge de la couleur [ 0 ; 255 ]
     * @param g Vert de la couleur  [ 0 ; 255 ]
     * @param b Bleu de la couleur  [ 0 ; 255 ]
     */
    public void setRGB(int r, int g, int b)
    {
        this.setR(r);
        this.setG(g);
        this.setB(b);
    }

    /**
     * Permet de modifier les attribut RG d'un coup sans modifier aux attributs A et B.
     * Si les valeurs sont en dehors de la plage [ 0 ; 255 ] elles seront ramenées au nombre le plus près dans la plage [ 0 ; 255 ]
     * @param r Rouge de la couleur [ 0 ; 255 ]
     * @param g Vert de la couleur  [ 0 ; 255 ]
     */
    public void setRG(int r, int g)
    {
        this.setR(r);
        this.setG(g);
    }

    /**
     * Permet de modifier les attribut RB d'un coup sans modifier aux attributs A et G.
     * Si les valeurs sont en dehors de la plage [ 0 ; 255 ] elles seront ramenées au nombre le plus près dans la plage [ 0 ; 255 ]
     * @param r Rouge de la couleur [ 0 ; 255 ]
     * @param b Bleu de la couleur  [ 0 ; 255 ]
     */
    public void setRB(int r, int b)
    {
        this.setR(r);
        this.setB(b);
    }

    /**
     * Permet de modifier les attribut GB d'un coup sans modifier aux attributs A et R.
     * Si les valeurs sont en dehors de la plage [ 0 ; 255 ] elles seront ramenées au nombre le plus près dans la plage [ 0 ; 255 ]
     * @param g Vert de la couleur  [ 0 ; 255 ]
     * @param b Bleu de la couleur  [ 0 ; 255 ]
     */
    public void setGB(int g, int b)
    {
        this.setG(g);
        this.setB(b);
    }

    /**
     * Permet de modifier la couleur à partir d'un int
     * @param argb Couleur ARGB sur un int
     * @param isArgb Si true, alors la couleur est du type ARGB, sinon, la couleur est du type RGB (dans ce cas le alpha est défini à 255)
     */
    public void setIntColor(int argb, boolean isArgb) { this.setIntToArgb(argb, isArgb); }

    /**
     * Permet de modifier la classe parente
     * @param c Classe parente
     */
    public void setParent(T c) { this.parent = c; }



    /*----------------------------------------*/
    /* Gestions du dégradé et du clignotement */
    /*----------------------------------------*/
    /**
     * Permet de faire un dégradé dynamique qui passe par toutes les couleurs du tableau de couleur présent dans la classe parente (définie dans le constructeur ou grace à la méthode setParent(T parent)).
     * Cette méthode met à jour la vue passé en paramètre.
     * Cette méthode est asynchrone, elle ne bloque pas le thread principal.
     * Cette méthode nécessite d'être arréter manuellement avec la méthode stopGradient().
     * Cette méthode ne peut être lancé qu'une seule fois à la fois sur le même object de type MyColor ET la couleur ne doit pas être en clignotement, sinon une exception est lancé.
     * Attention, cette méthode modifie la couleur courante.
     *
     * @param pas Augmentation du pourcentage de la couleur de destination (prise dans le tableu de MyColor).
     * Défini la vitesse du passage d'une couleur à l'autre, plus la valeur est petite, plus le dégradé est lent et inversement.
     * Cette valeur doit être comprise entre ] 0 ; 100 ] (c'est à dire entre 0 exclus et 100 inclus).
     *
     * @param view Vue sur laquelle on dessine (Permet de mettre à jour la vue)
     *
     * @param allerRetour Si true, alors le dégradé se fait dans les deux sens, sinon, le dégradé se fait dans un seul sens. (le mettre à true permet souvent d'avoir un dégradé plus joli et plus fluide)
     * C'est à dire que s'il est arrivé au bout du tableau de couleurs et que allerRetour est à true, alors il parcourt le tableau de couleurs dans l'autre sens, sinon, il retourne au début tableau.
     */
    public <V extends View> void startGradientAuto(double pas, V view, boolean allerRetour)
    {
        if (this.isGradient()) return;
        if (pas <= MyColor.MIN_PAS_INCREMENT || pas > MyColor.MAX_PAS_INCREMENT) throw new IllegalArgumentException("Le pas d'incrémentation doit être compris entre ] " + MyColor.MIN_PAS_INCREMENT + " ; " + MyColor.MAX_PAS_INCREMENT + " ]. c'est à dire " + MyColor.MIN_PAS_INCREMENT + " exclu et " + MyColor.MAX_PAS_INCREMENT + " inclus");
        if (view == null) throw new NullPointerException("La vue passé en paramètre de la méthode startFlashing(MyColor, double, T, V) appeler sur la couleur '" + this.toString() + "' ne doit pas être null");
        if (this.isFlashing()) throw new IllegalStateException("Vous ne pouvez pas utiliser la méthode startGradient(double, V) appeler sur la couleur '" + this.toString() + "' car un dégradé automatique est déjà en cours d'exécution. Vous devez d'abord arréter le dégradé automatique avec la méthode stopGradient() avant de pouvoir utiliser la méthode startFlashing(MyColor, double, T, V)");
        if (this.parent == null) throw new NullPointerException("La classe parente de la couleur '" + this.toString() + "' est null. Vous devez d'abord définir la classe parente avec la méthode setParent(T parent) avant de pouvoir utiliser la méthode startGradient(double, V)");

        this.gradientIsAlive = true;
        this.canGradient = true;
        this.threadAutoGradient = new Thread(() ->
        {
            int cpt = 0;
            while (!threadAutoGradient.isInterrupted() && this.gradientIsAlive)
            {
                if (cpt >= 30000)
                {
                    cpt = 0;
                    if (increment)
                        this.setIntColor(this.incrementGradientAuto(pas, allerRetour).getIntColor(), true);
                    else
                        this.setIntColor(this.decrementGradientAuto(pas).getIntColor(), true);

                    view.invalidate();
                }
                else
                    cpt ++;
            }

            this.gradientIsAlive = false;
        });

        this.threadAutoGradient.start();
    }

    /**
     * Permet d'arréter le dégradé dynamique.
     */
    public void stopGradient()
    {
        if (this.threadAutoGradient != null && this.threadAutoGradient.isAlive()) this.gradientIsAlive = false;
    }

    /**
     * Permet de savoir si un dégradé à déjà été lancer puis arrêter.
     * @return true si un dégradé à déjà été lancer puis arrêter, false sinon.
     */
    public boolean canGradient() { return this.canGradient; }

    /**
     * Permet de faire un dégradé dynamique qui passe par toutes les couleurs du tableau de couleur présent dans la classe parente (définie dans le constructeur ou grace à la méthode setParent(T parent)).
     * @param pas Augmentation du pourcentage de la couleur de destination (prise dans le tableu de MyColor).
     * Défini la vitesse du passage d'une couleur à l'autre, plus la valeur est petite, plus le dégradé est lent et inversement.
     * Cette valeur doit être comprise entre ] 0 ; 100 ] (c'est à dire entre 0 exclus et 100 inclus).
     *
     * @param allerRetour Si true, alors le dégradé se fait dans les deux sens, sinon, le dégradé se fait dans un seul sens. (le mettre à true permet souvent d'avoir un dégradé plus joli et plus fluide)
     * C'est à dire que s'il est arrivé au bout du tableau de couleurs et que allerRetour est à true, alors il parcourt le tableau de couleurs dans l'autre sens, sinon, il retourne au début tableau.
     *
     * Classe parente (c'est dans cette classe que les couleurs iront chercher les variables nécessaires au dégradé, cela permet de faire un dégradé uniforme entre plusieurs Instances de MyColor différentes).
     * En général, on met 'this' et on implements l'interface 'GradientInterface' à la class qui gère le dégradé.
     *
     * @return Couleur dégradée (Attention, la couleur courante n'est pas modifiée, il faut donc la réaffecter à la variable qui contient la couleur courante)
     */
    private MyColor incrementGradientAuto(double pas, boolean allerRetour)
    {
        if (this.parent == null) throw new NullPointerException("Utilisé la méthode setParent() sur la couleur '" + this.toString() + "' avant d'utiliser la méthode incrementGradient()");
        if (this.parent.getColors() == null || this.parent.getColors().length < 2) throw new NullPointerException("Vous devez définir au moins 2 couleurs dans le tableau de couleur présent dans la class '" + this.parent.getClass().getName() + "' avant d'utiliser la méthode incrementGradient()");
        if (pas <= MyColor.MIN_PAS_INCREMENT || pas > MyColor.MAX_PAS_INCREMENT) throw new IllegalArgumentException("Le pas doit être compris entre ] " + MyColor.MIN_PAS_INCREMENT + " ; " + MyColor.MAX_PAS_INCREMENT + " ]. " + MyColor.MIN_PAS_INCREMENT + " exclu et " + MyColor.MAX_PAS_INCREMENT + " inclus");

        this.percent += pas;
        if (this.percent > MyColor.MAX_PAS_INCREMENT)
        {
            this.percent = 0;
            this.incrementIndColorAuto(allerRetour);
        }

        return this.gradientTo(this.percent);
    }

    /**
     * Permet d'incrémenter l'indice de la couleur prédéfinie
     */
    private void incrementIndColorAuto(boolean allerRetour)
    {
        this.indColor ++;
        if (this.indColor >= this.parent.getColors().length)
        {
            if (allerRetour)
            {
                this.indColor = this.parent.getColors().length - 1;
                this.increment = false;
            }
            else
                this.indColor = 0;
        }
    }

    /**
     * Permet de faire un dégradé dynamique entre deux couleurs (la couleur courante et les couleurs définies définie dans le tableau de couleur)
     * @param pas Augmentation du pourcentage de la couleur de destination (prise dans le tableu de MyColor).
     * Défini la vitesse du passage d'une couleur à l'autre, plus la valeur est petite, plus le dégradé est lent et inversement.
     * Cette valeur doit être comprise entre ] 0 ; 100 ] (c'est à dire entre 0 exclus et 100 inclus).
     *
     * @return Couleur dégradée (Attention, la couleur courante n'est pas modifiée, il faut donc la réaffecter à la variable qui contient la couleur courante)
     */
    private MyColor decrementGradientAuto(double pas)
    {
        if (this.parent == null) throw new NullPointerException("Utilisé la méthode setParent() sur la couleur '" + this.toString() + "' avant d'utiliser la méthode incrementGradient()");
        if (this.parent.getColors() == null || this.parent.getColors().length < 2) throw new NullPointerException("Vous devez définir au moins 2 couleurs dans le tableau de couleur présent dans la class '" + this.parent.getClass().getName() + "' avant d'utiliser la méthode incrementGradient()");
        if (pas <= MyColor.MIN_PAS_INCREMENT || pas > MyColor.MAX_PAS_INCREMENT) throw new IllegalArgumentException("Le pas doit être compris entre ] " + MyColor.MIN_PAS_INCREMENT + " ; " + MyColor.MAX_PAS_INCREMENT + " ]. " + MyColor.MIN_PAS_INCREMENT + " exclu et " + MyColor.MAX_PAS_INCREMENT + " inclus");

        this.percent -= pas;
        if (this.percent <= MyColor.MIN_PAS_INCREMENT)
        {
            this.percent = MyColor.MAX_PAS_INCREMENT;
            this.decrementIndColorAuto();
        }

        return this.gradientTo(this.percent);
    }

    /**
     * Permet d'incrémenter l'indice de la couleur prédéfinie
     */
    private void decrementIndColorAuto()
    {
        this.indColor --;
        if (this.indColor < 0)
        {
            this.indColor = 0;
            this.increment = true;
        }
    }

    /**
     * Permet de savoir si un dégrader est en cours d'exécution
     * @return true si un dégradé est en cours d'exécution, false sinon
     */
    public boolean isGradient()
    {
        return this.gradientIsAlive;
    }

    /**
     * Permet de faire un dégradé entre deux couleurs (la couleur courante et la couleur passée en paramètre)
     * @param percent Pourcentage de dégradé compris entre  ] 0 ; 100 ]
     * @return Couleur de dégradé
     */
    private MyColor gradientTo(double percent)
    {
        int rgb[] = this.mixRGB(this.parent.getColorAt(this.indColor), percent, this.r, this.g, this.b);

        return new MyColor(rgb[0], rgb[1], rgb[2], this.parent);
    }

    /**
     * Permet de faire un dégradé dynamique qui passe par toutes les couleurs du tableau de couleur présent dans la classe parente (définie dans le constructeur ou grace à la méthode setParent(T parent)).
     * @param pas Augmentation du pourcentage de la couleur de destination (prise dans le tableu de MyColor).
     * Défini la vitesse du passage d'une couleur à l'autre, plus la valeur est petite, plus le dégradé est lent et inversement.
     * Cette valeur doit être comprise entre ] 0 ; 100 ] (c'est à dire entre 0 exclus et 100 inclus).
     *
     * Classe parente (c'est dans cette classe que les couleurs iront chercher les variables nécessaires au dégradé, cela permet de faire un dégradé uniforme entre plusieurs Instances de MyColor différentes).
     * En général, on met 'this' et on implements l'interface 'GradientInterface' à la class qui gère le dégradé.
     *
     * @return Couleur dégradée (Attention, la couleur courante n'est pas modifiée, il faut donc la réaffecter à la variable qui contient la couleur courante)
     */
    public MyColor incrementGradient(double pas)
    {
        if (this.parent == null) throw new NullPointerException("Utilisé la méthode setParent() sur la couleur '" + this.toString() + "' avant d'utiliser la méthode incrementGradient()");
        if (this.parent.getColors() == null || this.parent.getColors().length < 2) throw new NullPointerException("Vous devez définir au moins 2 couleurs dans le tableau de couleur présent dans la class '" + this.parent.getClass().getName() + "' avant d'utiliser la méthode incrementGradient()");
        if (pas <= MyColor.MIN_PAS_INCREMENT || pas > MyColor.MAX_PAS_INCREMENT) throw new IllegalArgumentException("Le pas doit être compris entre ] " + MyColor.MIN_PAS_INCREMENT + " ; " + MyColor.MAX_PAS_INCREMENT + " ]. " + MyColor.MIN_PAS_INCREMENT + " exclu et " + MyColor.MAX_PAS_INCREMENT + " inclus");
        if (this.isGradient() || this.isFlashing()) throw new IllegalStateException("Vous ne pouvez pas utiliser la méthode incrementGradient(double) appeler sur la couleur '" + this.toString() + "' car un dégradé automatique ou un clignotement est déjà en cours d'exécution. Vous devez d'abord arréter le dégradé automatique ou le clignotement avec la méthode stopGradient() ou stopFlashing() avant de pouvoir utiliser la méthode incrementGradient(double)");

        this.percent += pas;
        if (this.percent > MyColor.MAX_PAS_INCREMENT)
        {
            this.percent = 0;
            this.incrementIndColor();
        }

        return this.gradientTo(this.percent);
    }

    /**
     * Permet d'incrémenter l'indice de la couleur prédéfinie
     */
    private void incrementIndColor()
    {
        this.indColor ++;
        if (this.indColor >= this.parent.getColors().length || this.indColor < 0)
            this.indColor = 0;
    }


    /**
     * Permet de faire un dégradé dynamique entre deux couleurs (la couleur courante et les couleurs définies définie dans le tableau de couleur)
     * @param pas Augmentation du pourcentage de la couleur de destination (prise dans le tableu de MyColor).
     * Défini la vitesse du passage d'une couleur à l'autre, plus la valeur est petite, plus le dégradé est lent et inversement.
     * Cette valeur doit être comprise entre ] 0 ; 100 ] (c'est à dire entre 0 exclus et 100 inclus).
     *
     * @return Couleur dégradée (Attention, la couleur courante n'est pas modifiée, il faut donc la réaffecter à la variable qui contient la couleur courante)
     */
    public MyColor decrementGradient(double pas)
    {
        if (this.parent == null) throw new NullPointerException("Utilisé la méthode setParent() sur la couleur '" + this.toString() + "' avant d'utiliser la méthode incrementGradient()");
        if (this.parent.getColors() == null || this.parent.getColors().length < 2) throw new NullPointerException("Vous devez définir au moins 2 couleurs dans le tableau de couleur présent dans la class '" + this.parent.getClass().getName() + "' avant d'utiliser la méthode incrementGradient()");
        if (pas <= MyColor.MIN_PAS_INCREMENT || pas > MyColor.MAX_PAS_INCREMENT) throw new IllegalArgumentException("Le pas doit être compris entre ] " + MyColor.MIN_PAS_INCREMENT + " ; " + MyColor.MAX_PAS_INCREMENT + " ]. " + MyColor.MIN_PAS_INCREMENT + " exclu et " + MyColor.MAX_PAS_INCREMENT + " inclus");
        if (this.isGradient() || this.isFlashing()) throw new IllegalStateException("Vous ne pouvez pas utiliser la méthode decrementGradient(double) appeler sur la couleur '" + this.toString() + "' car un dégradé automatique ou un clignotement est déjà en cours d'exécution. Vous devez d'abord arréter le dégradé automatique ou le clignotement avec la méthode stopGradient() ou stopFlashing() avant de pouvoir utiliser la méthode decrementGradient(double)");

        this.percent -= pas;
        if (this.percent <= MyColor.MIN_PAS_INCREMENT)
        {
            this.percent = MyColor.MAX_PAS_INCREMENT;
            this.decrementIndColor();
        }

        return this.gradientTo(this.percent);
    }

    /**
     * Permet d'incrémenter l'indice de la couleur prédéfinie
     */
    private void decrementIndColor()
    {
        this.indColor --;
        if (this.indColor < 0 || this.indColor >= this.parent.getColors().length)
            this.indColor = this.parent.getColors().length-1;
    }

    /**
     * Permet de faire un clignotement dégradé entre la couleur courante et la couleur de destination.
     * Cette méthode met à jour la vue passé en paramètre.
     * Cette méthode est asynchrone, elle ne bloque pas le thread principal.
     * Cette méthode nécessite d'être arréter manuellement avec la méthode stopFlashing().
     * Cette méthode ne peut être lancé qu'une seule fois à la fois sur le même object de type MyColor ET la couleur ne doit pas faire l'objet d'un dégradé automatique, sinon une exception est lancé.
     * Attention, cette méthode modifie la couleur courante.
     *
     * @param colorDest Couleur de destination
     * @param pas Augmentation du pourcentage de la couleur de destination (prise dans le tableu de MyColor).
     * Défini la vitesse du passage d'une couleur à l'autre, plus la valeur est petite, plus le dégradé est lent et inversement.
     * Cette valeur doit être comprise entre ] 0 ; 100 ] (c'est à dire entre 0 exclus et 100 inclus).
     *
     * @param nbRepetition Nombre de répétion. Si cette valeur est à -1 alors le clignotement sera infini (vous pouvez le stoper avec la méthode 'stopFlashing()')
     * @param view Vue sur laquelle vous allez faire le clignotement
     * @param <V> class qui extends View. c'est la vue sur laquelle vous allez faire le clignotement.
     */
    public <V extends View> void startFlashing(MyColor colorDest, double pas, int nbRepetition, V view)
    {
        if (this.isFlashing()) return;
        if (pas <= MyColor.MIN_PAS_INCREMENT || pas > MyColor.MAX_PAS_INCREMENT) throw new IllegalArgumentException("Le pas doit être compris entre ] " + MyColor.MIN_PAS_INCREMENT + " ; " + MyColor.MAX_PAS_INCREMENT + " ]. " + MyColor.MIN_PAS_INCREMENT + " exclu et " + MyColor.MAX_PAS_INCREMENT + " inclus");
        if (nbRepetition < -1) throw new IllegalArgumentException("Le nombre de répétition doit être supérieur ou égale à -1, -1 étant sans fin");
        if (view == null) throw new NullPointerException("La vue passé en paramètre de la méthode startFlashing(MyColor, double, int, V) appeler sur la couleur '" + this.toString() + "' ne doit pas être null");
        if (this.isGradient()) throw new IllegalStateException("Vous ne pouvez pas utiliser la méthode startFlashing(MyColor, double, int, V) appeler sur la couleur '" + this.toString() + "' car un dégradé automatique est déjà en cours d'exécution. Vous devez d'abord arréter le dégradé automatique avec la méthode stopGradient() avant de pouvoir utiliser la méthode startFlashing(MyColor, double, int, V)");

        this.flashingIsAlive = true;
        this.threadFlashing = new Thread(() ->
        {
            MyColor[] colors = new MyColor[]{new MyColor(this), new MyColor(colorDest)};

            double percent = 0;
            int indColor = 0;
            int nbRep = 0;
            int cpt = 0;
            while (!threadFlashing.isInterrupted() && this.flashingIsAlive && (nbRepetition == -1 || (nbRepetition != -1 && nbRep <= nbRepetition)))
            {
                if (cpt >= 10000)
                {
                    cpt = 0;
                    percent += pas;
                    if (percent >= MyColor.MAX_PAS_INCREMENT) {
                        percent = 0;
                        indColor = indColor + 1 > colors.length - 1 ? 0 : indColor + 1;

                        nbRep++;
                    }

                    int rgb[] = this.mixRGB(colors[indColor], percent, this.r, this.g, this.b);
                    this.setRGB(rgb[0], rgb[1], rgb[2]);

                    view.invalidate();
                }
                else
                    cpt++;
            }

            this.flashingIsAlive = false;
        });

        this.threadFlashing.start();
    }

    /**
     * Permet d'arrêter le clignotement de la couleur si elle est en train de clignoter.
     * Cette méthode ne fait rien si la couleur n'est pas en train de clignoter.
     */
    public void stopFlashing()
    {
        if (this.threadFlashing != null && this.threadFlashing.isAlive()) this.flashingIsAlive = false;
    }

    /**
     * Permet de savoir si la couleur est en train de clignoter ou non.
     * @return true si la couleur est en train de clignoter, false sinon.
     */
    public boolean isFlashing()
    {
        return this.flashingIsAlive;
    }


    /**
     * Permet de passer de la couleur à la couleur passer en paramètre avec un dégrader.
     * Cette méthode met à jour la vue passé en paramètre.
     * Cette méthode est asynchrone, elle ne bloque pas le thread principal.
     * Cette méthode ne peut être lancé qu'une seule fois à la fois sur le même object de type MyColor.
     * Attention, cette méthode modifie la couleur courante.
     *
     * @param colorDest Couleur de destination
     * @param pas Augmentation du pourcentage de la couleur de destination
     * Défini la vitesse du passage d'une couleur à l'autre, plus la valeur est petite, plus le dégradé est lent et fluide et inversement.
     * Cette valeur doit être comprise entre [ 0.079 ; 100 ] (c'est à dire entre 0.079 inclus et 100 inclus).
     *
     * @param view Vue sur laquelle vous allez faire le clignotement
     * @param <V> class qui extends View. c'est la vue sur laquelle vous allez faire le clignotement.
     */
    public <V extends View> void uniqueGradientTo(MyColor colorDest, double pas, V view)
    {
        if (this.threadSingleGradient != null && this.threadSingleGradient.isAlive()) return;
        if (view == null) throw new NullPointerException("La vue passé en paramètre de la méthode startFlashing(MyColor, double, T, V) appeler sur la couleur '" + this.toString() + "' ne doit pas être null");
        if (pas < 0.079 || pas > MyColor.MAX_PAS_INCREMENT) throw new IllegalArgumentException("Le pas doit être compris entre ] " + MyColor.MIN_PAS_INCREMENT + " ; " + MyColor.MAX_PAS_INCREMENT + " ]. " + MyColor.MIN_PAS_INCREMENT + " exclu et " + MyColor.MAX_PAS_INCREMENT + " inclus");

        this.threadSingleGradient = new Thread(() ->
        {
            MyColor ancienneCouleur = new MyColor(this);
            int memeCouleur = 0;
            double percent = 0;

            int cpt = 0;
            while (!threadSingleGradient.isInterrupted())
            {
                if (cpt >= 10000)
                {
                    cpt = 0;
                    percent = percent + pas > MyColor.MAX_PAS_INCREMENT ? MyColor.MAX_PAS_INCREMENT : percent + pas;

                    int rgb[] = this.mixRGB(colorDest, percent, this.r, this.g, this.b);
                    this.setRGB(rgb[0], rgb[1], rgb[2]);

                    view.invalidate();

                    memeCouleur = ancienneCouleur.equals(this, false) ? memeCouleur + 1 : 0;
                    ancienneCouleur = new MyColor(this);

                    if (memeCouleur >= 50) this.threadSingleGradient.interrupt();
                }
                else
                    cpt++;
            }
        });

        this.threadSingleGradient.start();
    }


    /*----------------------------------------*/
    /* Couleur Aléatoire sur un objet MyColor */
    /*----------------------------------------*/
    /**
     * Permet de générer une couleur aléatoire
     */
    public void setRandomColor() { this.setARGB(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)); }

    /**
     * Permet de générer une couleur aléatoire avec un alpha donné. Si les valeurs sont en dehors de la plage [ 0 ; 255 ] elles seront ramenées au nombre le plus près dans la plage [ 0 ; 255 ]
     * @param a Alpha de la couleur [ 0 ; 255 ]
     */
    public void setRandomColorWithA(int a) { a = max(a, 0); a = min(a, 255); this.setARGB(a, new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)); }

    /**
     * Permet de générer une couleur aléatoire avec un alpha et un rouge donné. Si les valeurs sont en dehors de la plage [ 0 ; 255 ] elles seront ramenées au nombre le plus près dans la plage [ 0 ; 255 ]
     * @param a Alpha de la couleur [ 0 ; 255 ]
     * @param r Rouge de la couleur [ 0 ; 255 ]
     */
    public void setRandomColorWithAR(int a, int r) { a = max(a, 0); a = min(a, 255); r = max(r, 0); r = min(r, 255); this.setARGB(a, r, new Random().nextInt(256), new Random().nextInt(256)); }

    /**
     * Permet de générer une couleur aléatoire avec un alpha et un vert donné. Si les valeurs sont en dehors de la plage [ 0 ; 255 ] elles seront ramenées au nombre le plus près dans la plage [ 0 ; 255 ]
     * @param a Alpha de la couleur [ 0 ; 255 ]
     * @param g Vert de la couleur  [ 0 ; 255 ]
     */
    public void setRandomColorWithAG(int a, int g) { a = max(a, 0); a = min(a, 255); g = max(g, 0); g = min(g, 255); this.setARGB(a, new Random().nextInt(256), g, new Random().nextInt(256)); }

    /**
     * Permet de générer une couleur aléatoire avec un alpha et un vert donné. Si les valeurs sont en dehors de la plage [ 0 ; 255 ] elles seront ramenées au nombre le plus près dans la plage [ 0 ; 255 ]
     * @param a Alpha de la couleur [ 0 ; 255 ]
     * @param b bleu de la couleur  [ 0 ; 255 ]
     */
    public void setRandomColorWithAB(int a, int b) { a = max(a, 0); a = min(a, 255); b = max(b, 0); b = min(b, 255); this.setARGB(a, new Random().nextInt(256), new Random().nextInt(256), b); }



    /*--------------------------*/
    /* Couleur Aléatoire static */
    /*--------------------------*/
    /**
     * Permet de générer une couleur aléatoire
     * @return couleur aléatoire
     */
    public static int getIntRandomColor() { return MyColor.colorArgbToInt(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)); }

    /**
     * Permet de générer une couleur aléatoire avec un alpha donné. Si les valeurs sont en dehors de la plage [ 0 ; 255 ] elles seront ramenées au nombre le plus près dans la plage [ 0 ; 255 ]
     * @param a Alpha de la couleur [ 0 ; 255 ]
     * @return couleur aléatoire
     */
    public static int getIntRandomColorWithA(int a) { return MyColor.colorArgbToInt(a, new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)); }

    /**
     * Permet de générer une couleur aléatoire avec un alpha et un rouge donné. Si les valeurs sont en dehors de la plage [ 0 ; 255 ] elles seront ramenées au nombre le plus près dans la plage [ 0 ; 255 ]
     * @param a Alpha de la couleur [ 0 ; 255 ]
     * @param r Rouge de la couleur [ 0 ; 255 ]
     * @return couleur aléatoire
     */
    public static int getIntRandomColorWithAR(int a, int r) { return MyColor.colorArgbToInt(a, r, new Random().nextInt(256), new Random().nextInt(256)); }

    /**
     * Permet de générer une couleur aléatoire avec un alpha et un vert donné. Si les valeurs sont en dehors de la plage [ 0 ; 255 ] elles seront ramenées au nombre le plus près dans la plage [ 0 ; 255 ]
     * @param a Alpha de la couleur [ 0 ; 255 ]
     * @param g Vert de la couleur  [ 0 ; 255 ]
     * @return couleur aléatoire
     */
    public static int getIntRandomColorWithAG(int a, int g) { return MyColor.colorArgbToInt(a, new Random().nextInt(256), g, new Random().nextInt(256)); }

    /**
     * Permet de générer une couleur aléatoire avec un alpha et un bleu donné. Si les valeurs sont en dehors de la plage [ 0 ; 255 ] elles seront ramenées au nombre le plus près dans la plage [ 0 ; 255 ]
     * @param a Alpha de la couleur [ 0 ; 255 ]
     * @param b Bleu de la couleur  [ 0 ; 255 ]
     * @return couleur aléatoire
     */
    public static int getIntRandomColorWithAB(int a, int b) { return MyColor.colorArgbToInt(a, new Random().nextInt(256), new Random().nextInt(256), b); }



    /*-----------------------------------------*/
    /* Convertion static de ARGB/RGB en un int */
    /*-----------------------------------------*/
    /**
     * Convertie une couleur ARGB en int. Si les valeurs sont en dehors de la plage [ 0 ; 255 ] elles seront ramenées au nombre le plus près dans la plage [ 0 ; 255 ]
     * @param a alpha [ 0 ; 255 ]
     * @param r rouge [ 0 ; 255 ]
     * @param g vert  [ 0 ; 255 ]
     * @param b bleu  [ 0 ; 255 ]
     * @return Couleur ARGB sur un int
     */
    public static int colorArgbToInt(int a, int r, int g, int b)
    {
        a = min(max(a, 0), 255);
        r = min(max(r, 0), 255);
        g = min(max(g, 0), 255);
        b = min(max(b, 0), 255);

        return (a & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
    }

    /**
     * Convertie une couleur RGB en int (avec un alpha à 255)
     * @param r rouge [ 0 ; 255 ]
     * @param g vert  [ 0 ; 255 ]
     * @param b bleu  [ 0 ; 255 ]
     * @return Couleur ARGB sur un int
     */
    public static int colorRgbToInt(int r, int g, int b)
    {
        return MyColor.colorArgbToInt(255, r, g, b);
    }



    /*------------------------*/
    /* Méthodes plus générale */
    /*------------------------*/
    /**
     * Convertie une couleur ARGB en int
     * @param a alpha [ 0 ; 255 ]
     * @param r rouge [ 0 ; 255 ]
     * @param g vert  [ 0 ; 255 ]
     * @param b bleu  [ 0 ; 255 ]
     * @return Couleur ARGB sur un int
     */
    private int argbToInt(int a, int r, int g, int b)
    {
        return (a & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
    }

    /**
     * Convertie une couleur int en ARGB (ou RGB avec alpha à 255 si isArgb est false)
     * @param argb Couleur ARGB ou RGB (selon isArgb) sur un int
     * @param isArgb Si true, alors la couleur est de type ARGB, sinon, la couleur est de type RGB (dans ce cas le alpha est défini à 255)
     */
    private void setIntToArgb(int argb, boolean isArgb)
    {
        this.setA(isArgb ? (argb >> 24) & 0xff : 255);
        this.setR((argb >> 16) & 0xff);
        this.setG((argb >> 8) & 0xff);
        this.setB((argb) & 0xff);
    }

    /**
     * Permet de créer une nouvelle couleur constituer de X% de la couleur passée en paramètre et de (100-X)% de la couleur courante.
     * Cette méthode ne modifie pas la couleur courante.
     * @param colorDest Couleur de destination
     * @param percent Pourcentage de dégradé compris entre  ] 0 ; 100 [ (c'est à dire entre 0 exclus et 100 exclus)
     * @param mixA Si true, alors l'alpha sera pris en compte dans le dégradé, sinon, l'alpha de la nouvelle couleur sera celui de la couleur courante
     * @param copyParent Si true, alors la nouvelle couleur aura le même parent que la couleur courante, sinon, la nouvelle couleur n'aura pas de parent (parent = null)
     * @return Nouvelle couleur. Attention cette méthode créer une nouvelle couleur sans parent. si vous voulez copier le parent de la couleur courante, il faut utiliser la méthode mixColorWithParent(MyColor, double)
     */
    public MyColor mixColor(MyColor colorDest, double percent, boolean mixA, boolean copyParent)
    {
        if (percent <= 0 || percent >= 100) throw new IllegalArgumentException("Le pourcentage doit être compris entre ] 0 ; 100 [ (c'est à dire entre 0 exclus et 100 exclus)");

        T parent = copyParent ? this.parent : null;
        int a = mixA ? min(max((this.a + (int)((percent/(MyColor.MAX_PAS_INCREMENT*100)) * (colorDest.getA() - this.a))), 0), 255) : this.a;
        int rgb[] = this.mixRGB(colorDest, percent, this.r, this.g, this.b);

        return new MyColor(a, rgb[0], rgb[1], rgb[2], parent);
    }

    /**
     * Permet de mixer leux couleurs. Utilisé pour les dégradés et le mixage de deux couleurs.
     * @param colorDest Couleur de destination
     * @param percent Pourcentage de dégradé compris entre  ] 0 ; 100 [ (c'est à dire entre 0 exclus et 100 exclus)
     * @param r rouge [ 0 ; 255 ]
     * @param g vert  [ 0 ; 255 ]
     * @param b bleu  [ 0 ; 255 ]
     * @return Tableau de 3 int contenant les valeurs RGB dans cette ordre : [0] = r, [1] = g, [2] = b
     */
    private int[] mixRGB(MyColor colorDest, double percent, int r, int g, int b)
    {
        int[] rgb = new int[]{r, g, b};

        double percentTemp = percent/(MyColor.MAX_PAS_INCREMENT*10);
        rgb[0] = rgb[0] + (int)(percentTemp * (colorDest.getR() - rgb[0]));
        rgb[1] = rgb[1] + (int)(percentTemp * (colorDest.getG() - rgb[1]));
        rgb[2] = rgb[2] + (int)(percentTemp * (colorDest.getB() - rgb[2]));

        rgb[0] = min(max(rgb[0], 0), 255);
        rgb[1] = min(max(rgb[1], 0), 255);
        rgb[2] = min(max(rgb[2], 0), 255);

        return rgb;
    }

    /**
     * Permet d'obtenir la couleur opposé à la couleur courante
     * @param inclureAlpha Si true, alors l'alpha sera pris en compte dans l'invertion de la couleur, sinon, l'alpha de la nouvelle couleur sera celui de la couleur courante
     * @return Couleur opposé à la couleur courante
     */
    public MyColor opositeColor(boolean inclureAlpha)
    {
        MyColor colorRet;

        if (inclureAlpha) colorRet = new MyColor(255 - this.a, 255 - this.r, 255 - this.g, 255 - this.b, this.parent);
        else              colorRet = new MyColor(this.a, 255 - this.r, 255 - this.g, 255 - this.b, this.parent);

        return colorRet;
    }

    /**
     * Permet de savoir si deux couleurs sont égales
     * @param myColor couleur à comparer
     * @param compareParent si true, alors la méthode compare aussi la class parent, sinon, elle ne compare que les valeurs des attributs ARGB.
     * @return true si les deux couleurs sont égales, false sinon
     */
    public boolean equals(MyColor myColor, boolean compareParent)
    {
        if (myColor == null) throw new NullPointerException("Erreur, la couleur passer en paramètre de la méthode equals est null");

        boolean parentEquals = true;
        if (compareParent) { parentEquals = this.parent == myColor.parent; }

        if (parentEquals)
            return this.a == myColor.a && this.r == myColor.r && this.g == myColor.g && this.b == myColor.b;

        return false;
    }

    /**
     * Permet de récupérer la couleur sous forme d'une chaîne de caractères
     */
    public String toString()
    {
        return this.getClass().getName() + " [a= " + a + ", r= " + r + ", g= " + g + ", b= " + b + "]";
    }
}
