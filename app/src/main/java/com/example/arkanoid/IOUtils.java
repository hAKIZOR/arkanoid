package com.example.arkanoid;

import android.content.Context;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * Utility class to manage Streams and generic IO issues.
 */
public final class IOUtils {

  /*
 * The default for the buffer size
 */
  private static final int DEFAULT_BUFFE_SIZE = 2048;

  /*
   * The default encoding we use when not specified
   */
  private static final String DEFAULT_ENCODING = "UTF-8";

  /**
   * Private constructor.
   */
  private IOUtils() {
  }

  /**
   * Copies data from a reader to a writer, using a custom buffer size. Note that the
   * reader nad the writer are not closed at the end of the copy!.
   *
   * @param in         The reader.
   * @param out        The writer.
   * @param bufferSize The buffer size (in bytes).
   * @throws java.io.IOException If an I/O error occurs.
   */
  public static void copy(Reader in, Writer out, int bufferSize) throws IOException {
    char[] buffer = new char[bufferSize];
    int read = 0;
    while ((read = in.read(buffer)) != -1) {
      out.write(buffer, 0, read);
    }
  }

  /**
   * Closes the input/output stream quietly, no matter if it is null or if
   * errors occur closing it.
   *
   * @param stream the input/output stream to close.
   */
  public static void closeQuietly(Closeable stream) {
    try {
      if (stream != null) {
        stream.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Read characters from an input stream to a string. Note that the input
   * stream is not closed at the end of method!.
   *
   * @param in       The input stream.
   * @param encoding The characters encoding.
   * @return The string.
   * @throws java.io.IOException If an IO error occurs.
   */
  public static String toString(InputStream in, String encoding) throws IOException {
    InputStreamReader reader = new InputStreamReader(in, encoding);
    StringWriter writer = new StringWriter();
    copy(reader, writer, DEFAULT_BUFFE_SIZE);
    return writer.getBuffer().toString();
  }

  /**
   * Copies data from an input stream to an output stream, using a buffer of
   * given size Note that the streams are not closed at the end of the copy!.
   *
   * @param in         The input stream.
   * @param out        The output stream.
   * @param bufferSize The buffer size (in bytes).
   * @throws java.io.IOException If an I/O error occurs.
   */
  public static void copy(final InputStream in, final OutputStream out, final int bufferSize)
          throws IOException {
    byte[] buffer = new byte[bufferSize];
    int read = 0;
    while ((read = in.read(buffer)) != -1) {
      out.write(buffer, 0, read);
    }
  }

  /**
   * Copies data from an input stream to an output stream, using a standard
   * buffer. Note that the streams are not closed at the end of the copy!.
   *
   * @param in  The input stream.
   * @param out The output stream.
   * @throws java.io.IOException If an I/O error occurs.
   */
  public static void copy(final InputStream in, final OutputStream out)
          throws IOException {
    copy(in, out, DEFAULT_BUFFE_SIZE);
  }

  public static Object readObjectFromFile(Context context,String fileName) throws IOException, ClassNotFoundException {
    FileInputStream fIn= context.openFileInput(fileName);
    ObjectInputStream inputStream = new ObjectInputStream(fIn);
    Object  object =  inputStream.readObject();

    fIn.close();
    inputStream.close();
    return object;
  }
  public static void writeObjectToFile(Context context,String fileName,Object object) throws IOException {
    FileOutputStream fOut = context.openFileOutput(fileName, Context.MODE_PRIVATE);
    ObjectOutputStream outputStream = new ObjectOutputStream(fOut);
    outputStream.writeObject(object);

    fOut.close();
    outputStream.close();
  }

}
