package edu.upc.eseiaat.pma.shoppinglist3;

/**
 * Created by Marta on 23/10/2017.
 */
// Alt + Insert ens permet crear un constructor facilment, un getter i un setter, etc..
public class ShoppingItem {
    //Variables privades de la classe
    private String text;
    private boolean checked;

    public ShoppingItem(String text) {
        this.text = text;
        //S'assumeix que el booleà és false per defecte.
    }

    public ShoppingItem(String text, boolean checked) {
        this.text = text;
        this.checked = checked;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void toggleChecked() {
        this.checked = !this.checked;
    }
}
