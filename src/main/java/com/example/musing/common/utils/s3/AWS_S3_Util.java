package com.example.musing.common.utils.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AWS_S3_Util {
    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName; //버킷 이름
    private final AmazonS3 amazonS3;

    @Transactional
    public String uploadImageToS3(MultipartFile file, String useServiceName, String fileName){
        try{ //이미지를 S3에 업로드하고 이미지의 url을 반환
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            PutObjectResult putObjectResult = amazonS3.putObject(new PutObjectRequest(
                    bucketName,useServiceName + "/" + fileName, file.getInputStream(),objectMetadata
            ).withCannedAcl(CannedAccessControlList.PublicRead));//서버에 이미지 업로드
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 중 오류 발생: " + e.getMessage());
        }
        return amazonS3.getUrl(bucketName,useServiceName + "/" + fileName).toString(); //데이터베이스에 저장할 이미지가 저장된 주소
    }

    @Transactional
    public void deleteFile(String useServiceName, String filename){
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName,useServiceName + "/" + filename));
    }
}
