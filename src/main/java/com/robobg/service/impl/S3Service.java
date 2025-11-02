package com.robobg.service.impl;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {
    private final S3Client s3Client;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }


    public void putFile(String bucketName, String key, Path filePath) {
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("application/octet-stream")
                .serverSideEncryption(ServerSideEncryption.AES256)
                .build();
        s3Client.putObject(req, RequestBody.fromFile(filePath));
    }

    public int deleteAllButNewest(String bucket, String prefix, int keepCount) {
        // 1) List ALL objects under the prefix
        List<S3Object> all = new ArrayList<>();
        ListObjectsV2Request req = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix)
                .build();

        // Use paginator so we handle >1000 keys safely
        ListObjectsV2Iterable pages = s3Client.listObjectsV2Paginator(req);
        for (ListObjectsV2Response page : pages) {
            all.addAll(page.contents());
        }

        // Only .tar.gz (optional – remove this filter if you want to count everything)
        List<S3Object> tarGz = all.stream()
                .filter(o -> o.key().endsWith(".tar.gz"))
                .collect(Collectors.toList());

        if (tarGz.size() <= keepCount) return 0;

        // 2) Sort newest first by LastModified (robust even if key format changes)
        tarGz.sort(Comparator.comparing(S3Object::lastModified).reversed());

        // 3) Everything after the first keepCount is to delete
        List<ObjectIdentifier> toDelete = tarGz.subList(keepCount, tarGz.size()).stream()
                .map(o -> ObjectIdentifier.builder().key(o.key()).build())
                .collect(Collectors.toList());

        // 4) Batch-delete (S3 supports up to 1000 per request)
        int deleted = 0;
        for (int i = 0; i < toDelete.size(); i += 1000) {
            int end = Math.min(i + 1000, toDelete.size());
            var batch = toDelete.subList(i, end);
            s3Client.deleteObjects(DeleteObjectsRequest.builder()
                    .bucket(bucket)
                    .delete(Delete.builder().objects(batch).build())
                    .build());
            deleted += batch.size();
        }

        return deleted;
    }
}
