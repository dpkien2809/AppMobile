package com.example.mobileproject.api;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AssetApi {
   URL url;
   HttpURLConnection con;


   String readUntilChar(BufferedReader reader, char until_char) throws IOException {
      StringBuilder builder = new StringBuilder();
      char current_char;
      while (true){
         int char_int = reader.read();
         if(char_int == -1)
            return "";

         current_char = (char) char_int;
         if (current_char==until_char)
            break;
         builder.append(current_char);
      }
      return builder.toString();
   }


   public AssetApi(String url, String method,String token) throws IOException
   {
      this.url = new URL(url);
      this.con = (HttpURLConnection) this.url.openConnection();
      con.setRequestProperty("Content-Type","application/json");
      con.setRequestProperty("Authorization", "Bearer ".concat(token));
      con.setRequestMethod(method);

   }

   public Map<String, String> GeData() throws IOException {
      InputStream in;
      int status = con.getResponseCode();
      if (status == 200)
         in = con.getInputStream();
      else
         in = con.getErrorStream();


      int length = con.getContentLength();
      byte[] raw_data = new byte[length];
      int byte_read = 0;
      while (byte_read < length) {
         byte_read += in.read(raw_data, byte_read, length-byte_read);
      }
      in.close();
      InputStream response_stream = new ByteArrayInputStream(raw_data);

      Map<String, String> result = new HashMap<>();

      BufferedReader reader = new BufferedReader(new InputStreamReader(response_stream));




      String next ;
      String[] elements;
      String key;
      String value;
      try {
         while (true) {
            next = readUntilChar(reader,',');


            if (next.isEmpty())
               return result;

            if (next.contains("value")) {
               elements = next.split(":");
               value = elements[1];
               next = readUntilChar(reader,',');
               elements = next.split(":");
               key = elements[1];
               result.put(key.replace("\"",""), value.replace("\"",""));
            }
            else if(next.contains("location")){
               readUntilChar(reader,'[');
               String[] coordinate  = readUntilChar(reader,']').split(",");
               result.put("longitude",coordinate[0]);
               result.put("latitude",coordinate[1]);

               for(int i = 0 ;i<3;i++)
                  readUntilChar(reader,',');
            }

         }
      }catch (IOException e)
      {
         return  result;
      }



   }
}
