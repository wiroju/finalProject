import java.util.ArrayList;

public class documentResults {
    double docSentiment;
    int sentimentRGB;
    ArrayList<keyScores> keywordsList;

    public documentResults(double sentScore, int sentInt, ArrayList<keyScores> keywords){
        docSentiment = sentScore;
        sentimentRGB = sentInt;
        keywordsList = keywords;
    }

    public double getDocSentiment() {
        return docSentiment;
    }

    public int getSentimentRGB() {
        return sentimentRGB;
    }

    public ArrayList<keyScores> getKeywordsList() {
        return keywordsList;
    }
}
