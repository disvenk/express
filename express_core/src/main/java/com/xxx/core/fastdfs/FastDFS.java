package com.xxx.core.fastdfs;

import com.xxx.core.fastdfs.common.MyException;
import com.xxx.core.fastdfs.common.NameValuePair;
import com.xxx.core.fastdfs.source.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FastDFS {
    static {
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:fdfs_client.conf");

            Assert.isTrue(resources.length > 0);
            Resource resource = resources[0];
            if (resources.length > 2) {
                for (Resource rc : resources) {
                    if (rc.getFilename().contains("dev")) {
                        resource = rc;
                        break;
                    }
                }
            }
            ClientGlobal.init(resource.getURL().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    public static String upload(String fileName, byte[] file, Map<String, String> nameValuePair) throws IOException {
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        try {
            StorageServer storageServer = null;
            StorageClient1 client = new StorageClient1(trackerServer, storageServer);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new NameValuePair("fileName", fileName));
            if (nameValuePair != null && nameValuePair.size() > 0) {
                for (String key : nameValuePair.keySet()) {
                    list.add(new NameValuePair(key, nameValuePair.get(key)));
                }
            }
            String ext = null;
            if (fileName != null) {
                int index = fileName.lastIndexOf(".");
                if (index > 0) {
                    ext = fileName.substring(index + 1);
                }
            }
            try {
                return client.upload_file1(file, ext, list.toArray(new NameValuePair[list.size()]));
            } catch (MyException e) {
                e.printStackTrace();
                throw new IOException("上传失败");
            }
        } finally {
            if (trackerServer != null) {
                trackerServer.close();
            }
        }
    }

    public static String upload(String fileName, String filePath, Map<String, String> nameValuePair) throws IOException {
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        try {
            StorageServer storageServer = null;
            StorageClient1 client = new StorageClient1(trackerServer, storageServer);
            List<NameValuePair> list = new ArrayList<>();
            list.add(new NameValuePair("fileName", fileName));
            if (nameValuePair != null && nameValuePair.size() > 0) {
                for (String key : nameValuePair.keySet()) {
                    list.add(new NameValuePair(key, nameValuePair.get(key)));
                }
            }
            String ext = null;
            if (fileName != null) {
                int index = fileName.lastIndexOf(".");
                if (index > 0) {
                    ext = fileName.substring(index + 1);
                }
            }
            try {
                return client.upload_file1(filePath, ext, list.toArray(new NameValuePair[list.size()]));
            } catch (MyException e) {
                e.printStackTrace();
                throw new IOException("上传失败");
            }
        } finally {
            if (trackerServer != null) {
                trackerServer.close();
            }
        }
    }

    public static byte[] download(String fileId) throws IOException {
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        try {
            StorageServer storageServer = null;
            StorageClient1 client = new StorageClient1(trackerServer, storageServer);
            try {
                return client.download_file1(fileId);
            } catch (MyException e) {
                e.printStackTrace();
                throw new IOException("下载失败");
            }
        } finally {
            if (trackerServer != null) {
                trackerServer.close();
            }
        }
    }

    public static int delete(String fileId) throws IOException {
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        try {
            StorageServer storageServer = null;
            StorageClient1 client = new StorageClient1(trackerServer, storageServer);
            try {
                return client.delete_file1(fileId);
            } catch (MyException e) {
                e.printStackTrace();
                throw new IOException("删除失败");
            }
        } finally {
            if (trackerServer != null) {
                trackerServer.close();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        FileInputStream fis = new FileInputStream("/Users/wanghua/other/projects/架构图/拾羽无状态登录认证流程图.png");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len;
        while ((len = fis.read(b)) > 0)
            bos.write(b, 0, len);

        String str = FastDFS.upload("shiyu_no_status_pic.png", bos.toByteArray(), null);
        System.out.println(str);  //输出格式：group1/M00/08/11/cxy5HVjbewWAb6NfAAEFlU1ZCIg962.png

        byte[] bb = FastDFS.download(str);
        FileOutputStream fos = new FileOutputStream("/Users/wanghua/other/projects/架构图/拾羽无状态登录认证流程图2.png");
        fos.write(bb);
        fos.flush();
    }
}
