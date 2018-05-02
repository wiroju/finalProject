public class result {
    String keyword;
    double relevance;
    emotion emotionValues;

    public result(String word, double rel, emotion eResults){
        keyword = word;
        relevance = rel;
        emotionValues=eResults;
    }
}