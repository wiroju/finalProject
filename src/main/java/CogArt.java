import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.io.File;
import java.util.List;
import javax.imageio.*;

public class CogArt extends JFrame {

    private static final int KEYWORDS = 25;
    private static final int EMOTIONS = 5;
    private static final String VERSION = "2018-03-16";
    private static final String USERNAME = "43bff58b-d86a-4a0c-8897-cbf1ce7466ca";
    private static final String PASSWORD = "OoV266OHHSRx";

    /*public CogArt(){
        initInterface();
    }

    private void initInterface(){

        JTextArea inputArea = new JTextArea(5,10);
        JScrollPane sPane = new JScrollPane();

        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);

        createLayout(sPane);

        setTitle("Cognitive Art");
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createLayout(JComponent... arg) {

        Container pane = getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        gl.setHorizontalGroup(gl.createParallelGroup()
                .addComponent(arg[0])

        );

        gl.setVerticalGroup(gl.createSequentialGroup()
                .addComponent(arg[0])
        );

        pack();
    }*/

    public static void main(String[] args) {

        String text = "Japanese submarine slammed two torpedoes into her side, Chief. "
                + "We was comin' back from the island of Tinian to Leyte. We'd just delivered the bomb. "
                + "The Hiroshima bomb. Eleven hundred men went into the water. Vessel went down in 12 minutes. "
                + "Didn't see the first shark for about a half-hour. Tiger. 13-footer. You know how you know that in the water, Chief? "
                + "You can tell by lookin' from the dorsal to the tail. "
                + "What we didn't know, was that our bomb mission was so secret, no distress signal had been sent. "
                + "They didn't even list us overdue for a week. "
                + "Very first light, Chief, sharks come cruisin' by, so we formed ourselves into tight groups. "
                + "It was sorta like you see in the calendars, you know the infantry squares in the old calendars like the Battle of Waterloo "
                + "and the idea was the shark come to the nearest man, "
                + "that man he starts poundin' and hollerin' and sometimes that shark he go away... but sometimes he wouldn't go away. "
                + "Sometimes that shark looks right at ya. Right into your eyes. "
                + "And the thing about a shark is he's got lifeless eyes. Black eyes. Like a doll's eyes. "
                + "When he comes at ya, he doesn't even seem to be livin'... "
                + "'til he bites ya, and those black eyes roll over white and then... ah then you hear that terrible high-pitched screamin'. "
                + "The ocean turns red, and despite all your poundin' and your hollerin' those sharks come in and... they rip you to pieces. "
                + "You know by the end of that first dawn, lost a hundred men. "
                + "I don't know how many sharks there were, maybe a thousand. I do know how many men, they averaged six an hour. "
                + "Thursday mornin', Chief, I bumped into a friend of mine, Herbie Robinson from Cleveland. Baseball player. "
                + "Boson's mate. I thought he was asleep. I reached over to wake him up. He bobbed up, down in the water, he was like a kinda top. "
                + "Upended. Well, he'd been bitten in half below the waist. "
                + "At noon on the fifth day, a Lockheed Ventura swung in low and he spotted us, a young pilot, "
                + "lot younger than Mr. Hooper here, anyway he spotted us and a few hours later a big ol' fat PBY come down and started to pick us up. "
                + "You know that was the time I was most frightened. Waitin' for my turn. I'll never put on a lifejacket again. "
                + "So, eleven hundred men went into the water. 316 men come out, the sharks took the rest, June the 29th, 1945. "
                + " Anyway, we delivered the bomb.";

        AnalysisResults response = textAnalysis(text);
        ArrayList<keyScores> keyEmotions =  processResults(response);

        double sentiment = response.getSentiment().getDocument().getScore();
        double scaleSentiment = (sentiment+1)*112.5;
        int sentimentRGB = (int)scaleSentiment;

        documentResults results = new documentResults(sentiment, sentimentRGB, keyEmotions);

        try {
            drawImage(results);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AnalysisResults textAnalysis(String inputText) {

        NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(VERSION,
                USERNAME,
                PASSWORD
        );

        EntitiesOptions entitiesOptions = new EntitiesOptions.Builder()
                .emotion(false)
                .sentiment(false)
                .limit(2)
                .build();

        SentimentOptions sentiment = new SentimentOptions.Builder()
                .build();

        KeywordsOptions keywordsOptions = new KeywordsOptions.Builder()
                .emotion(true)
                .sentiment(false)
                .limit(KEYWORDS)
                .build();

        Features features = new Features.Builder()
                .sentiment(sentiment)
                .entities(entitiesOptions)
                .keywords(keywordsOptions)
                .build();

        AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                .text(inputText)
                .features(features)
                .build();

        AnalysisResults response = service
                .analyze(parameters)
                .execute();

        return response;
    }

    public static ArrayList processResults(AnalysisResults response) {

        List<KeywordsResult> keywordList = response.getKeywords();

        ArrayList<keyScores> resultArrayList = new ArrayList<>();

        for (int i = 0; i < KEYWORDS; i++) {

            String key;
            double relevance = keywordList.get(i).getRelevance();
            double scaleRelevance = relevance*100;
            int intRelevance = (int)scaleRelevance;

            //System.out.println(intRelevance);

            emotion emotion;
            double[] score = new double[5];

            key = keywordList.get(i).getText();
            score[0] = keywordList.get(i).getEmotion().getAnger();
            score[1] = keywordList.get(i).getEmotion().getDisgust();
            score[2] = keywordList.get(i).getEmotion().getFear();
            score[3] = keywordList.get(i).getEmotion().getJoy();
            score[4] = keywordList.get(i).getEmotion().getSadness();

            emotion = processEmotion(score);

            resultArrayList.add(new keyScores(key, relevance, intRelevance, emotion));
        }

        return resultArrayList;
    }

    public static emotion processEmotion(double[] scores) {
        String topEmotion;
        Color colour;
        int emotion = 0;
        double highestScore = scores[0];
        topEmotion = "anger";
        colour = null;

        for (int i = 0; i < EMOTIONS; i++) {
            if (scores[i] > highestScore) {
                highestScore = scores[1];
                emotion = i;
            } else {
                i++;
            }
        }

        switch (emotion) {
            case 0:
                topEmotion = "anger";
                colour = Color.red;
                break;

            case 1:
                topEmotion = "disgust";
                colour = Color.green;
                break;

            case 2:
                topEmotion = "fear";
                colour = Color.magenta;
                break;

            case 3:
                topEmotion = "joy";
                colour = Color.yellow;
                break;

            case 4:
                topEmotion = "sadness";
                colour = Color.blue;
                break;
        }

        //System.out.println(topEmotion + " -> " + colour);

        return new emotion(topEmotion,colour);
    }

    public static void drawImage(documentResults processedResponse) throws IOException {

        int sentimentRGB = processedResponse.getSentimentRGB();
        ArrayList<keyScores> keywords = processedResponse.getKeywordsList();
        String word=null;

        Color background = new Color(sentimentRGB,sentimentRGB,sentimentRGB);

        BufferedImage wordCloud = new BufferedImage(1280,720,BufferedImage.TYPE_INT_RGB);
        Graphics2D graphic = wordCloud.createGraphics();
        graphic.setColor(background);
        graphic.fillRect(0,0,1280,720);

        for (int i = 0;i<keywords.size();i++){

            Color colour = keywords.get(i).emotionValues.getColourToUse();
            word = keywords.get(i).keyword.toString();
            System.out.println(word);
            int intRelevance = keywords.get(i).getIntRelevance();
            System.out.println(intRelevance);

            graphic.setColor(colour);
            graphic.setFont(new Font("ARIAL",Font.LAYOUT_LEFT_TO_RIGHT,intRelevance));
            graphic.drawString(word,180,180);
        }

        ImageIO.write(wordCloud, "jpg", new File(
                "D:/Work/Year_3/Project/java/src/main/resources/generatedImages/cloud.jpg"));

        System.out.println("Image Created");
    }

}