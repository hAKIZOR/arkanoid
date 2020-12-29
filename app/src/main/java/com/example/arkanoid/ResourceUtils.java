package com.example.arkanoid;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility method to read raw resources ad String
 */
public final class ResourceUtils {

  /**
   * The default encoding for the files
   */
  private static final String DEFAULT_ENCODING = "UTF8";

  /*
   * Private constructor
   */
  private ResourceUtils() {
    throw new AssertionError("Never call this!!!");
  }

  /**
   * Return the content of a raw resource as a String given the encoding
   *
   * @param context  The Context reference
   * @param encoding The encoding to use for reading
   * @param rawId    The id for the raw resource
   * @return The raw Resource as a String
   * @throws java.io.IOException                  In case of error reading from the Stream
   * @throws java.io.UnsupportedEncodingException In case of wrong encoding
   */
  public static String getRawAsString(Context context, String encoding, int rawId) throws IOException {
    InputStream is = context.getResources().openRawResource(rawId);
    String result = IOUtils.toString(is, encoding);
    return result;
  }

  /**
   * Return the content of a raw resource as a String using UTF-8 encoding
   *
   * @param context The Context reference
   * @param rawId   The id for the raw resource
   * @return The raw Resource as a String
   * @throws java.io.IOException                  In case of error reading from the Stream
   * @throws java.io.UnsupportedEncodingException In case of wrong encoding
   */
  public static String getRawAsString(Context context, int rawId) throws IOException {
    return getRawAsString(context, DEFAULT_ENCODING, rawId);
  }

}
