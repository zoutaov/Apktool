package brut.androlib.res.decoder;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import brut.directory.Directory;
import brut.directory.DirectoryException;

public class FixChunkClass {

    public static byte[] toByteArray(String filename) throws IOException {

        File f = new File(filename);
        if (!f.exists()) {
            throw new FileNotFoundException(filename);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bos.close();
        }
    }

    public static int getIntFromBytes(byte high_h, byte high_l, byte low_h, byte low_l) {
        return (high_h & 0xff) << 24 | (high_l & 0xff) << 16 | (low_h & 0xff) << 8 | low_l & 0xff;
    }

    public static byte[] deleteAt(byte[] bs, int index)
    {
        int length = bs.length - 1;
        byte[] ret = new byte[length];
        System.arraycopy(bs, 0, ret, 0, index);
        System.arraycopy(bs, index + 1, ret, index, length - index);
        return ret;
    }
    public void writeXmlFile(byte[] bytes,String writePath){
        try {
            FileOutputStream fos = new FileOutputStream(writePath);
            fos.write(bytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024*4];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    public static final InputStream byte2Input(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }

    public static byte[] inputStream2byte(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inputStream.read(buff, 0, 100)) > 0) {
            byteArrayOutputStream.write(buff, 0, rc);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static InputStream fixChunkValue(Directory inDir, String inFileName)
    {
        InputStream in = null;
        int flag = 0;
        try {
            in = inDir.getFileInput(inFileName);
            byte[] xmlByte = FixChunkClass.inputStream2byte(in);
            int stringCount = FixChunkClass.getIntFromBytes(xmlByte[0x13], xmlByte[0x12], xmlByte[0x11], xmlByte[0x10]);
            int offset = stringCount*4+0x24;
            flag = FixChunkClass.getIntFromBytes(xmlByte[offset+3], xmlByte[offset+2], xmlByte[offset+1], xmlByte[offset]);
            if(flag == 0){
                System.out.println("Fix Caused by: java.io.IOException: Invalid chunk type (0)");
                xmlByte = FixChunkClass.deleteAt(xmlByte, offset+3);
                xmlByte = FixChunkClass.deleteAt(xmlByte, offset+2);
                xmlByte = FixChunkClass.deleteAt(xmlByte, offset+1);
                xmlByte = FixChunkClass.deleteAt(xmlByte, offset);
            }
            in = FixChunkClass.byte2Input(xmlByte);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DirectoryException e) {
            e.printStackTrace();
        }
        return in;
    }
}
