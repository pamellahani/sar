package hybrid;

/**
 * A message to be sent or received.
 */
public class Message {

    private byte[] bytes; 
    private int offset;
    private int length;

    public Message(byte[] bytes, int offset, int length) {
        this.bytes = bytes;
        this.offset = offset;
        this.length = length;
    }

    public byte getByteAt(int i) {
        return bytes[offset + i];
    }

    public int getLength() {
        return length;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getOffset() {
        return offset;
    }
}
