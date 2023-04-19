package raymondbdev.eyeturner.UnusedJavaFiles;


import raymondbdev.eyeturner.Model.Enums.ColourMode;

public class SettingsManager2 {
    private ColourMode colourMode = ColourMode.LIGHT_MODE;

    private Integer[] availableSizes = {8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24};
    private Integer sizeIndex;

    public SettingsManager2() {
        sizeIndex = 4;
    }

    public Integer getFontSize() {
        return availableSizes[sizeIndex];
    }

    public String getColourMode() {
        if(colourMode == ColourMode.LIGHT_MODE) {
            return "Light";
        } else if(colourMode == ColourMode.DARK_MODE) {
            return "Dark";
        }

        return null;
    }

    public void switchColourMode() {
        if(colourMode == ColourMode.DARK_MODE) {
            colourMode = ColourMode.LIGHT_MODE;
        } else {
            colourMode = ColourMode.DARK_MODE;
        }
    }

    public Integer getNextFontSize() {

        if(sizeIndex == availableSizes.length - 1) {
            return availableSizes[availableSizes.length - 1];
        }

        sizeIndex++;
        return availableSizes[sizeIndex];
    }

    public Integer getPreviousFontSize() {
        if(sizeIndex == 0) {
            return availableSizes[0];
        }

        sizeIndex--;
        return availableSizes[sizeIndex];
    }
}
