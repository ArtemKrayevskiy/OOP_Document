package ua.edu.ucu.apps;

public class TimedDocument implements Document{
    private Document document;
    public TimedDocument(Document doc){
        this.document = doc;
    }


    @Override
    public String parse() {
        long startTime = System.currentTimeMillis();
        document.parse();
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        return elapsedTime + " milliseconds";
    }
}
