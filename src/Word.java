public class Word {
    protected String key;
    protected int keyCount;

    public Word (String key) {
        this.key = key;
        keyCount = 1;
    }
    public Word (String key, int keyCount) {
        this.key = key;
        this.keyCount = keyCount;
    }

    public String getKey() {
        return key;
    }

    public int getKeyCount() {
        return keyCount;
    }

    public void incKeyCount() {
        this.keyCount++;
    }
    public void decKeyCount() {
        this.keyCount--;
    }

    @Override
    public String toString(){
        return this.key;
    }
}
