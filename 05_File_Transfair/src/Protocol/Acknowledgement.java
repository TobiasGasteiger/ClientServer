package Protocol;

public class Acknowledgement extends Message{

    private boolean noticed = false;

    public Acknowledgement(boolean noticed) {
        this.noticed = noticed;
    }

    public boolean isNoticed() {
        return noticed;
    }

    public void setNoticed(boolean noticed) {
        this.noticed = noticed;
    }
}
