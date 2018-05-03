public class keyScores {
    String keyword;
    double relevance;
    int intRelevance;
    emotion emotionValues;

    public keyScores(String word, double rel, int intRel, emotion eResults){
        keyword = word;
        relevance = rel;
        emotionValues=eResults;
        intRelevance = intRel;
    }

    public String getKeyword() {
        return keyword;
    }

    public emotion getEmotionValues() {
        return emotionValues;
    }

    public double getRelevance() {
        return relevance;
    }

    public int getIntRelevance() {
        return intRelevance;
    }
}