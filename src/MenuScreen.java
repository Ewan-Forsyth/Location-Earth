import java.util.ArrayList;

/**
 * MenuScreen Class
 * An instance of this class represents an individual screen on a menu. (i.e Options, New Game, ect)
 * It consists of elements such as buttons, text, images and background rectangles.
 * It acts as a go-between for and menu class and the elements.
 */
class MenuScreen {

    private ArrayList<MenuUIText> textElements;
    private ArrayList<MenuUIButton> buttonElements;
    private ArrayList<MenuUIBackground> backgroundElements;
    private ArrayList<MenuUIImage> imageElements;
    private int activeButton; //This index of the button that is currently active.

    public MenuScreen() {

        this.textElements = new ArrayList<>();
        this.buttonElements = new ArrayList<>();
        this.backgroundElements = new ArrayList<>();
        this.imageElements = new ArrayList<>();
        this.activeButton = 0;
    }

    public void addTextElement(MenuUIText textElement) {

        this.textElements.add(textElement);

    }

    public void setText(String ID, String newText) {

        for (MenuUIText textElement : this.textElements) {

            if (textElement.getID().equals(ID)) {

                textElement.setText(newText);
                break;

            }

        }

    }

    public String getText(String ID) {

        String result = "";
        for (MenuUIText textElement : this.textElements) {
            if (textElement.getID().equals(ID)) {

                result = textElement.getText();
                break;

            }

        }

        return result;

    }

    public void setTextColour(String ID, int[] newColour) {

        for (MenuUIText textElement : this.textElements) {

            if (textElement.getID().equals(ID)) {

                textElement.setColour(newColour);
                break;

            }

        }

    }

    public void addButtonElement(MenuUIButton buttonElement) {

        if (this.buttonElements.isEmpty())
            buttonElement.setActive(true);

        this.buttonElements.add(buttonElement);

    }

    public void addBackgroundElement(MenuUIBackground backgroundElement) {

        this.backgroundElements.add(backgroundElement);

    }

    public void setBackgroundColour(String ID, int[] colour) {

        for (MenuUIBackground backgroundElement : this.backgroundElements) {

            if (backgroundElement.getID().equals(ID)) {

                backgroundElement.setBackgroundColour(colour);
                break;

            }

        }

    }

    public void resetActiveButton() {

        this.buttonElements.get(this.activeButton).setActive(false);
        this.activeButton = 0;
        this.buttonElements.get(this.activeButton).setActive(true);
    }

    public void changeActiveButton(boolean directionDown) {

        if (directionDown) { //If the next item is to be the next in the array

            this.buttonElements.get(this.activeButton).setActive(false);

            if (this.activeButton + 1 == this.buttonElements.size())
                this.activeButton = 0;
            else
                this.activeButton++;

            this.buttonElements.get(this.activeButton).setActive(true);

        } else { //If the button before is the item the user is trying to get to.

            this.buttonElements.get(this.activeButton).setActive(false);

            if (this.activeButton - 1 == -1)
                this.activeButton = this.buttonElements.size() - 1;
            else
                this.activeButton--;

            this.buttonElements.get(this.activeButton).setActive(true);

        }

    }

    public String getActiveButtonID() {

        return this.buttonElements.get(this.activeButton).getID();

    }

    public void setButtonText(String ID, String newText) {

        for (MenuUIButton buttonElement : this.buttonElements) {

            if (buttonElement.getID().equals(ID)) {

                buttonElement.setText(newText);
                break;

            }

        }

    }


    public String onClick() {

        String result = null;

        //Loops through the buttons to get to the one that has been clicked.
        for (MenuUIButton button : buttonElements) {

            if (button.isHovered()) {

                result = button.getID();
                break;
            }

        }

        return result;

    }

    public void addImageElement(MenuUIImage imageElement) {

        this.imageElements.add(imageElement);

    }

    public void setImageElement(String ID, String sprite) {

        for (MenuUIImage imageElement : this.imageElements) {
            if (imageElement.getID().equals(ID)) {
                imageElement.sprite(sprite, -1, false);
                break;
            }
        }

    }

    public void setImageElementTint(String ID, boolean mode) {

        for (MenuUIImage imageElement : this.imageElements) {
            if (imageElement.getID().equals(ID)) {
                imageElement.setTintMode(mode);
                break;
            }
        }

    }

    public void removeElement(String ID) {
        ArrayList<MenuUIBackground> tempBackgroundElements = new ArrayList<>();
        for (MenuUIBackground backgroundElement : this.backgroundElements)
            if (!backgroundElement.getID().equals(ID))
                tempBackgroundElements.add(backgroundElement);
        this.backgroundElements = tempBackgroundElements;

        ArrayList<MenuUIButton> tempButtonElements = new ArrayList<>();
        for (MenuUIButton buttonElement : this.buttonElements)
            if (!buttonElement.getID().equals(ID))
                tempButtonElements.add(buttonElement);
        this.buttonElements = tempButtonElements;

        ArrayList<MenuUIText> tempTextElements = new ArrayList<>();
        for (MenuUIText textElement : this.textElements)
            if (!textElement.getID().equals(ID))
                tempTextElements.add(textElement);
        this.textElements = tempTextElements;

        ArrayList<MenuUIImage> tempImageElements = new ArrayList<>();
        for (MenuUIImage imageElement : this.imageElements)
            if (!imageElement.getID().equals(ID))
                tempImageElements.add(imageElement);
        this.imageElements = tempImageElements;

    }

    public void draw() {

        for (MenuUIBackground backgroundElement : this.backgroundElements)
            backgroundElement.draw();

        for (MenuUIButton buttonElement : this.buttonElements)
            buttonElement.draw();

        for (MenuUIText textElement : this.textElements)
            textElement.draw();

        for (MenuUIImage imageElement : this.imageElements)
            imageElement.draw();


    }

}
