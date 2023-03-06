package core;

import java.util.ArrayList;
import java.util.List;
/**
 *  RESP - Redis serialization protocol - redis supports several data structures such as
 *  string -> +OK\r\n
 *  error -> -ERROR OCCURRED\r\n
 *  int   -> :1\r\n
 *  array -> *2\r\n$5\r\nHello\r\n:1\r\n
 *  bulk strings - $5\r\nHello\r\n (Binary safe String)
 */
public class Decoder {
    byte [] data;
    int pos = 0;
    public Decoder(byte [] data){
        this.data = data;
    }

    // Decode main function
    public Object decode() throws Exception{
        pos = pos + 1;
        switch (data[pos - 1]){
            case '+' :
                return decodeString();
            case '-' :
                return decodeError();
            case ':' :
                return decodeInt();
            case '$' :
                return decodeBulkString();
            case '*' :
                return decodeArray();
        }
        throw new IllegalArgumentException("Invalid data type");
    }

    // Decodes simple String
    private    String decodeString(){
        StringBuilder sb = new StringBuilder();
        for(;  data[pos] != '\r'; pos++ ){
            sb.append((char)data[pos]);
        }
        pos = pos + 2;
        return sb.toString();
    }

    // Decodes integer
    private int  decodeInt(){
        int val = 0;
        for(;  data[pos] != '\r'; pos++ ){
            val = val*10 + (data[pos] - '0');
        }
        pos = pos + 2;
        return val;
    }

    // Decodes binary safe string - '\r' can occur in the string as well
    private String decodeBulkString(){
        int len = decodeInt();
        StringBuilder sb = new StringBuilder();
        for( int i = 0 ; i < len ; i++ ){
            sb.append((char)data[pos++]);
        }
        pos =  pos  + 2;
        return sb.toString();
    }

    // decodes array
    private List<Object > decodeArray() throws Exception {
      int len = decodeInt();
      List<Object > res = new ArrayList<>();
      for(int i = 0 ; i < len ; i++ ){
          res.add(decode());
      }
      return res;
    }

    // Decode errors
    private String decodeError(){
        return decodeString();
    }
}
