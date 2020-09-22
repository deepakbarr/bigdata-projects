package com.demo.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;

import java.io.File;

public class S3Test2 {

    private static String S3ENDPOINT = "s3-ap-southeast-1.amazonaws.com";

    public static void main(String[] args) {
        boolean result = checkFiles("grab-mercury", "datalake/transformed/myteksi/referrers");
        System.out.println("result = " + result);
    }

    public static boolean checkFiles(String bucket, String s3Path) {
        AmazonS3 client = new AmazonS3Client();
        client.setEndpoint(S3ENDPOINT);
        ObjectListing objectList = client.listObjects(bucket, s3Path + File.separator);
        if (objectList.getObjectSummaries().size() != 0) {
            return true;
        }
        return false;
    }
}
