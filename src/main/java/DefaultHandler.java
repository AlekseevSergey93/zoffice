public class DefaultHandler implements Handler {
    Handler nextHandler = null;


    public String handle(String line) {
        if (nextHandler != null) {
            nextHandler.handle(line);
        }
        return "default string";
    }

    public void setNext(Handler handler) {
        this.nextHandler = handler;
    }
}
