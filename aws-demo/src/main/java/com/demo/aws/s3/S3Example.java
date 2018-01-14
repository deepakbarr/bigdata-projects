package com.demo.aws.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Hello world!
 */
public class S3Example
{
    public static void main( String[] args ) throws IOException {
        AWSCredentials credentials = new BasicAWSCredentials(
                "AKIAITC2Y4PCEWYCL4AA",
                "I0xKUPyT540sqhUn4o40B2GVRvLzmgKTzF7F+tMK"
        );

        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_WEST_2)
                .build();

        String bucketName = "dbarr-kafka-bucket";

        if(s3client.doesBucketExist(bucketName)) {
            System.out.println("Bucket name is not available."
                    + " Try again with a different Bucket name.");
            return;
        }

        s3client.createBucket(bucketName);

        List<Bucket> buckets = s3client.listBuckets();
        for(Bucket bucket : buckets) {
            System.out.println(bucket.getName());
        }

//        s3client.putObject(
//                bucketName,
//                "hello-key",
//                new File("/Users/dbarr/coderep_2/Workspace_bigdata/bigdata-projects/aws-demo/src/main/resources/hello.txt")
//        );
//
//        S3Object s3object = s3client.getObject(bucketName, "hello-key");
//        S3ObjectInputStream inputStream = s3object.getObjectContent();
//        FileUtils.copyInputStreamToFile(inputStream, new File("/Users/dbarr/coderep_2/Workspace_bigdata/bigdata-projects/aws-demo/src/main/resources/hello-from-s3.txt"));
    }
}
