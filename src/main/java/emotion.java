import java.awt.*;

public class emotion {
    String emotionValue;
    Color colourToUse;

    public emotion(String emotion, Color colour){
        emotionValue = emotion;
        colourToUse = colour;
    }

    public String getEmotionValue(){
        return emotionValue;
    }

    public Color getColourToUse() {
        return colourToUse;
    }
}
