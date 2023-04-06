package com.example.drawing_area.vues;

/**
 * Interface permettant de définir les méthodes utilisées par la classe MyColor pour faire un dégradé.
 * Vous n'avez pas à utiliser ces méthode, la classe MyColor s'en charge.
 * Voir la classe MyColor pour plus d'informations.
 */
public interface GradientInterface
{
    /**
     * Permet de récupérer la liste des couleurs
     * @return la liste des couleurs
     */
    public MyColor[] getColors();

    /**
     * Permet de récupérer la couleur à l'indice donner en paramètre
     * @return couleur
     */
    public MyColor getColorAt(int indColor);
}
