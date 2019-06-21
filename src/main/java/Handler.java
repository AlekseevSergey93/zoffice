public interface Handler {
    String handle(String line);

    void setNext(Handler handler);
}
