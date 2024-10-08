package events;

/**
 * A message to be sent or received.
 */
public class Message {

    public byte[] bytes; 
    int offset;
    int length;

    public Message(byte[] bytes, int offset, int length) {
        this.bytes = bytes;
        this.offset = offset;
        this.length = length;
    }
}
