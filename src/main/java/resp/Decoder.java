package resp;

/**
 * This class is A implementation for RESP based decoder
 */
public class Decoder {

    public Object[] decode(byte [] data) throws RuntimeException {
        if(data.length == 0 ){
            throw new RuntimeException("No data present ");
        }

        switch(data[0]){
            case '+' :
                return readSimpleString(data);
            case '-' :
                return readError(data);
            case ':' :
                return readInt(data);
            case '$' :
                return readBulkString(data);
            case '*' :
                return readArray(data);
            default:
                throw new RuntimeException("Invalid command sent");
        }
    }

    //Reads Array
    // Arrays are written as -> *1\r\n$5\r\nhello\r\n
    private Object[] readArray(byte[] data) {
         Object [] arr = readInt(data);

         int pos = (int )arr[1] + 1, count = (int)arr[0];
         Object [] ele = new Object[count];

         for(int i = 0; i < count ; i++ ){
              byte [] buff = makeNewBuffer(pos, data);

              Object[] decoded = decode(buff);
              pos += (int)decoded[1];

              ele[i] = decoded[0];
         }

         return new Object[]{ele, pos};
    }

    byte [] makeNewBuffer(int st, byte [] buff){
        byte[] ret = new byte[buff.length - st];
        int j = 0;
        for(int i = st; i < buff.length; i++ ){
            ret[j++] = buff[i];
        }
        return ret;
    }

    // Reads Binary safe bulk string
    // Bulk string are encoded as -> Hello - $5\r\nHello\r\n
    private Object[] readBulkString(byte[] data) {
        // first(0th) byte is '$'
        Object [] arr = readInt(data);

        int pos = (int )arr[1], len = (int)arr[0];

        String str = new String(data, pos , len);

        return new Object[]{str, pos + len + 2};
    }


    // Reads Integers
    // Integers are encodes as 1000 -> :1000\r\n
    private Object[] readInt(byte[] data) {
        // first(0th) byte is ':'
        int pos = 1, res = 0;
        for(; data[pos] != '\r' ; ++pos)
            res = res*10  + (data[pos] - '0');
        return new Object[]{res, pos + 2};
    }

    // Reads Error
    // Error are encodes as  -Error occurred\r\n
    private Object[] readError(byte[] data) {
        /// first(0th) byte is '-'
        return readSimpleString(data);
    }

    // Simple String  are encodes as  +Simple String\r\n
    // Reads Simple String
    private Object[] readSimpleString( byte[] data) {
         // first(0th) byte is '+'
         int pos = 1;
         for(; data[pos] != '\r' ; ++pos);
         String str = new String(data, 0, pos);
         return new Object[]{str, pos + 2};
    }


}
