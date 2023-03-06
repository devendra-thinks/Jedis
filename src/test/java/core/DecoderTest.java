package core;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class DecoderTest {
    @Test
    public void decodeStringTest() throws Exception {
        String simpleString  = "+OK\r\n";
        Decoder decoder = new Decoder(simpleString.getBytes());
        Object obj = decoder.decode();
        String expString = (String )obj;
        Assertions.assertEquals("OK", expString);
    }

    @Test
    public void decodeIntTest() throws Exception {
        String simpleString  = ":5\r\n";
        Decoder decoder = new Decoder(simpleString.getBytes());
        Object obj = decoder.decode();
        int expInt = (int )obj;
        Assertions.assertEquals(5, expInt);
    }

    @Test
    public void decodeErrorTest() throws Exception {
        String simpleString  = "-Something wrong\r\n";
        Decoder decoder = new Decoder(simpleString.getBytes());
        Object obj = decoder.decode();
        String  expError = (String  )obj;
        Assertions.assertEquals("Something wrong", expError);
    }

    @Test
    public void decodeBulkString() throws Exception {
        String simpleString  = "$5\r\nHello\r\n";
        Decoder decoder = new Decoder(simpleString.getBytes());
        Object obj = decoder.decode();
        String  expError = (String  )obj;
        Assertions.assertEquals("Hello", expError);
    }

    @Test
    public void decodeArray() throws Exception {
        String simpleString  = "*2\r\n$5\r\nHello\r\n:1\r\n";
        Decoder decoder = new Decoder(simpleString.getBytes());
        Object obj = decoder.decode();
        List<Object > objs = (List<Object> )obj;

        Assertions.assertEquals("Hello", objs.get(0));
        Assertions.assertEquals(1, objs.get(1));

        String simpleString2  = "*2\r\n$5\r\nHello\r\n*2\r\n$5\r\nHello\r\n:1\r\n";
        Decoder decoder2 = new Decoder(simpleString2.getBytes());
        Object obj2 = decoder2.decode();
        List<Object > objs2 = (List<Object> )obj2;
        Assertions.assertEquals("Hello", objs2.get(0));
        List<Object > objs3 = (List<Object> )objs2.get(1);
        Assertions.assertEquals("Hello", objs3.get(0));
        Assertions.assertEquals(1, objs3.get(1));

    }



}