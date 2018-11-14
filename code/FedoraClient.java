import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.activation.MimetypesFileTypeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class FedoraClient {
    public static final String HOME_ADDRESS = "http://sead-fed.d2i.indiana.edu:8080/fed/rest/";

    public FedoraClient() {
    }



    public void disconnection(HttpURLConnection httpConn){
        httpConn.disconnect();
        System.out.println("Disconnet the link ["+httpConn.getURL()+"]");
    }

    public boolean validHttp(HttpURLConnection httpConn){
        int responseCode;
        boolean re = false;
        try {
            responseCode = httpConn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                re = true;
            }else{
                re = false;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            re = false;
            e.printStackTrace();
        }
        return re;
    }

    //create Container with specified path
    public void createContainer(String containerName){
        try{
            URL url = new URL(HOME_ADDRESS+containerName);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

            httpConn.setRequestMethod("PUT");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if (httpConn.getResponseCode() == 201){
                System.out.println("Create Contianer "+containerName + " successfully: "+response);
            }else{
                System.out.println("Fail to create Container: check the tombstone");
            }
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }

    //upload files
    public void uploadFile(String containerName, String fileName, String file_path){
        try{
            URL url = new URL(HOME_ADDRESS+containerName+"/"+fileName);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            File file = new File(file_path);
            httpConn.setDoOutput(true);
            httpConn.setRequestMethod("PUT");
            httpConn.setRequestProperty("Content-Type", new MimetypesFileTypeMap().getContentType(file));
            httpConn.setRequestProperty("Content-disposition", "attachment; filename="+ file_path.substring(file_path.lastIndexOf("/")+1));

            OutputStream out = httpConn.getOutputStream();

            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();


            BufferedReader res = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream()));
            String inputLine;


            while ((inputLine = res.readLine()) != null) {
                System.out.println(inputLine);
            }
            res.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void uploadMetadataForContainer(String containerName, String file_path){
        try{
            HttpClient client = HttpClientBuilder.create().build();
            HttpPatch patch = new HttpPatch(HOME_ADDRESS+containerName);
            patch.addHeader("Content-Type", "application/sparql-update");
            String input = "";
            BufferedReader br = new BufferedReader(new FileReader(new File(file_path)));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append("\n");
                    line = br.readLine();
                }
                input = sb.toString();
            }catch(Exception e){
                e.printStackTrace();
            }
            HttpEntity a = new StringEntity(input);
            patch.setEntity(a);
            HttpResponse dcResp = client.execute(patch);
            patch.releaseConnection();
            if (dcResp.getStatusLine().getStatusCode() == 204){
                System.out.println("Successfully update metadata");
            }else{
                System.out.println("Failed to update metadata");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void uploadMetadataForFile(String containerName, String file_path){
        try{
            HttpClient client = HttpClientBuilder.create().build();
            HttpPatch patch = new HttpPatch(HOME_ADDRESS+containerName+"/fcr:metadata");
            patch.addHeader("Content-Type", "application/sparql-update");
            String input = "";
            BufferedReader br = new BufferedReader(new FileReader(new File(file_path)));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append("\n");
                    line = br.readLine();
                }
                input = sb.toString();
            }catch(Exception e){
                e.printStackTrace();
            }
            HttpEntity a = new StringEntity(input);
            patch.setEntity(a);
            HttpResponse dcResp = client.execute(patch);
            patch.releaseConnection();
            if (dcResp.getStatusLine().getStatusCode() == 204){
                System.out.println("Successfully update metadata");
            }else{
                System.out.println("Failed to update metadata");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //delete a file or a Container
    public int delete(HttpURLConnection httpConn){
        try{

            httpConn.setRequestMethod("DELETE");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println("Delete path ["+httpConn.getURL()+"]");
            return httpConn.getResponseCode();
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }



    //delete the file with its tombstone
    public void deleteContainerORBinaryFile(String name) throws MalformedURLException, IOException{
        String access_address = HOME_ADDRESS + name;
        HttpURLConnection httpConn = (HttpURLConnection) (new URL(access_address)).openConnection();
        if (httpConn != null){
            int response = delete(httpConn);
            if (response == 204){
                System.out.println("Delete the specified file/container");
                disconnection(httpConn);
                access_address += "/fcr:tombstone";
                HttpURLConnection httpConn_tombstone = (HttpURLConnection) (new URL(access_address)).openConnection();

                if (httpConn_tombstone != null){
                    response = delete(httpConn_tombstone);
                    disconnection(httpConn_tombstone);
                    if (response == 204){
                        System.out.println("Delete tombstone");
                    }else{
                        System.out.println("Cannot delete: no such path");
                    }
                }else{
                    System.out.println("Cannot delete tombstone");
                }
            }else{
                System.out.println("Cannot delete: no such path");
            }
        }else{
            System.out.println("Cannot delete file/container and its' tombstone");
        }

    }

    //download files from Fedora 4 URL
    public void downloadFile(String url, String downloadPath) throws IOException{
        HttpURLConnection httpConn = (HttpURLConnection) (new URL(url)).openConnection();
        if (validHttp(httpConn)){
            try{

                String fileName = "";
                String disposition = httpConn.getHeaderField("Content-Disposition");
                String contentType = httpConn.getContentType();
                int contentLength = httpConn.getContentLength();

                if (disposition != null) {
                    // extracts file name from header field
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        String file_Name = disposition.split(";")[1].split("=")[1];
                        fileName = file_Name.substring(1, file_Name.length()-1);
                    }
                } else {
                    // extracts file name from URL
                    fileName = url.substring(url.lastIndexOf("/") + 1,
                            url.length());
                }

                System.out.println("Content-Type = " + contentType);
                System.out.println("Content-Disposition = " + disposition);
                System.out.println("Content-Length = " + contentLength);
                System.out.println("fileName = " + fileName);

                // opens input stream from the HTTP connection
                InputStream inputStream = httpConn.getInputStream();
                String saveFilePath = downloadPath + File.separator + fileName;

                // opens an output stream to save into file
                FileOutputStream outputStream = new FileOutputStream(saveFilePath);

                int bytesRead = -1;
                byte[] buffer = new byte[4096];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();



                System.out.println("Successfully download file "+fileName+" into this Path "+downloadPath+fileName);


            }catch(Exception e){
                e.printStackTrace();
                System.out.println("Unexpected Error on this URL "+url);

            }
        }else{
            System.out.println("Error: Thie URL is invalid ["+url+"]");
        }
    }



    public static void main(String[] args) throws MalformedURLException, IOException{

        FedoraAPIv2 test = new FedoraAPIv2();
        long startTime = System.currentTimeMillis();
        //test.createContainer("luoyutest");
        test.deleteContainerORBinaryFile("luoyutest/100");
        //test.uploadFile("luoyutest", "test", "/Users/yuluo/Desktop/test.tar");
        //test.downloadFile("http://sead-fed.d2i.indiana.edu:8080/fed/rest/luoyutest/test", "/Users/yuluo/Desktop/");
        //test.uploadMetadataForContainer("luoyutest", "/Users/yuluo/Desktop/container-indexing.rdf");
        //test.uploadMetadataForFile("luoyutest/test", "/Users/yuluo/Desktop/container-indexing.rdf");
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);
        String a = "adas1231";
        System.out.println(a.trim());
    }
}


